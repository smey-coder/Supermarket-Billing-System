import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.sql.*;


public class Billing_Item extends JPanel {
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String user = "sa";
    private final String dbPassword = "hello";

    private JComboBox<String> productBox = new JComboBox<>();
    private JComboBox<String> billBox = new JComboBox<>();
    private JTextField priceField = new JTextField();
    private JTextField quantityField = new JTextField();
    private JTextField totalField = new JTextField();
    private JLabel grandTotalLabel;
    private double grandTotal = 0.0;
    private JTextArea billTextArea = new JTextArea(20, 30);

    String[] columns = {"ID", "Bill ID", "ProductName", "Price", "Quantity", "Total"};
    DefaultTableModel bill_ITableModel = new DefaultTableModel(columns, 0);
    JTable bill_ItemTable = new JTable(bill_ITableModel);

    public Billing_Item(String billIdFromMain) {
        setLayout(new BorderLayout(10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Product to Bill"));

        // Load Bill IDs
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM Bills");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                billBox.addItem(rs.getString("id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + e.getMessage());
        }

        // Load Product Names
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM Products");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productBox.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }

        // Add input fields
        inputPanel.add(new JLabel("Bill ID:"));
        inputPanel.add(billBox);
        inputPanel.add(new JLabel("Product Name:"));
        inputPanel.add(productBox);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Total:"));
        inputPanel.add(totalField);

        JButton addButton = new JButton("Add to Bill");
        JButton clearButton = new JButton("Clear");
        inputPanel.add(addButton);
        inputPanel.add(clearButton);

        // Table Scroll
        JScrollPane tableScroll = new JScrollPane(bill_ItemTable);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        grandTotalLabel = new JLabel("Grand Total: $0.00");
        grandTotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(grandTotalLabel, BorderLayout.WEST);

        JButton clearAllButton = new JButton("Clear All");
        JButton printButton = new JButton("Print");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(clearAllButton);
        buttonPanel.add(printButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // TextArea
        billTextArea.setEditable(false);
        JScrollPane billScrollPane = new JScrollPane(billTextArea);
        initBillTextArea();

        // Auto update total field
        quantityField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    double price = Double.parseDouble(priceField.getText());
                    int qty = Integer.parseInt(quantityField.getText());
                    totalField.setText(String.format("%.2f", price * qty));
                } catch (Exception ex) {
                    totalField.setText("");
                }
            }
        });

        // Button Events
        addButton.addActionListener(e -> addProductToBill());
        clearButton.addActionListener(e -> clearFields());
        clearAllButton.addActionListener(e -> clearAll());

        printButton.addActionListener(e -> {
            try {
                Timestamp today_bill = new Timestamp(System.currentTimeMillis());
                billTextArea.append("=================================\n");
                billTextArea.append(String.format("Grand Total: $%.2f\n", grandTotal));
                billTextArea.append("=================================\n");
                billTextArea.append("Thank you for shopping!\n");
                billTextArea.append("\n[Date: " + today_bill + "]");
                if (billTextArea.print()) {
                    JOptionPane.showMessageDialog(this, "Bill printed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Print canceled.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage());
            }
        });

        productBox.addActionListener(e -> {
            String selectedProduct = (String) productBox.getSelectedItem();
            if (selectedProduct != null) {
                try (Connection conn = DriverManager.getConnection(url, user, dbPassword);
                     PreparedStatement ps = conn.prepareStatement("SELECT price FROM Products WHERE name = ?")) {
                    ps.setString(1, selectedProduct);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        priceField.setText(String.format("%.2f", rs.getDouble("price")));
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error fetching price: " + ex.getMessage());
                }
            }
        });

        // Layout
        add(inputPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(billScrollPane, BorderLayout.EAST);
    }

    private void addProductToBill() {
        String billId = (String) billBox.getSelectedItem();
        String productName = (String) productBox.getSelectedItem();
        String price = priceField.getText().trim();
        String quantity = quantityField.getText().trim();
        String total = totalField.getText().trim();

        if (billId == null || productName == null || price.isEmpty() || quantity.isEmpty() || total.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            double priceVal = Double.parseDouble(price);
            int qtyVal = Integer.parseInt(quantity);
            double totalVal = Double.parseDouble(total);

            try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
                int productId = getProductIdByName(conn, productName);
                if (productId == -1) {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                    return;
                }
                String stockQty = "Select stockQuantity From Products Where id = ?";
                PreparedStatement stockStmt = conn.prepareStatement(stockQty);
                stockStmt.setInt(1, productId);
                ResultSet stockRs = stockStmt.executeQuery();
                int currentStock = 0;

                if (stockRs.next()){
                    currentStock = stockRs.getInt("stockQuantity");
                }
                //Check if enouh stock

                if(currentStock < qtyVal){
                    JOptionPane.showMessageDialog(this, "Not enough stock! Anvalable: " + currentStock);
                    return;
                }
                // Subtract qty from product table
                String updateStockSql = "Update Products Set stockQuantity = stockQuantity - ? Where id = ?";
                PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql);
                updateStockStmt.setInt(1, qtyVal);
                updateStockStmt.setInt(2, productId);

                updateStockStmt.executeUpdate();

                //Input into billing_item
                String sql = "INSERT INTO Bill_Items (bill_id, product_id, price, quantity, item_total) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, billId); // fixed: setString for NVARCHAR
                ps.setInt(2, productId); // productId is INT
                ps.setDouble(3, priceVal);
                ps.setInt(4, qtyVal);
                ps.setDouble(5, totalVal);
                ps.executeUpdate();

                grandTotal += totalVal;
                
                grandTotalLabel.setText("Grand Total: $" + String.format("%.2f", grandTotal));
                billTextArea.append(String.format("%-15s %-10s %-10s\n", productName, quantity, total));

                bill_ITableModel.addRow(new Object[]{"Auto", billId, productName, price, quantity, total});
                clearFields();
            }

        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private int getProductIdByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT id FROM Products WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    private void clearFields() {
        priceField.setText("");
        quantityField.setText("");
        totalField.setText("");
    }

    private void clearAll() {
        clearFields();
        bill_ITableModel.setRowCount(0);
        grandTotal = 0.0;
        grandTotalLabel.setText("Grand Total: $0.00");
        billTextArea.setText("");
        initBillTextArea();
    }

    private void initBillTextArea() {
        billTextArea.append("=================================\n");
        billTextArea.append("Company Name: SRK\n");
        billTextArea.append("Contact No: xxxxxxxxxx\n");
        billTextArea.append("Address: Phnom Penh\n");
        billTextArea.append("=================================\n");
        billTextArea.append(String.format("%-15s %-10s %-10s\n", "Item", "Qty", "Total"));
    }
}