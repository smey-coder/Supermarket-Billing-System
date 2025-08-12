import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Bills extends JPanel {
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String dbuser = "sa";
    private final String dbPassword = "hello";
    private final Map<String, Integer> CustomerMap = new HashMap<>();
    private final Map<String, Integer> UserMap = new HashMap<>();

    private JTextField txtBillID, txtDate;
    private JComboBox<String> comboBoxCustomer = new JComboBox<>();
    private JComboBox<String> comboBoxUser = new JComboBox<>();
    private JButton btnCreateBill, btnRefreshBill;
    private JTable billTable;
    private DefaultTableModel tableModel;

    public Bills() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(250, 250, 250));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Bill"));
        formPanel.setBackground(Color.WHITE);

        txtBillID = new JTextField(generateBillId());
        txtBillID.setEditable(false);

        txtDate = new JTextField(LocalDate.now().toString());
        txtDate.setEditable(false);

        comboBoxCustomer.addItem("-- Select Customer --");
        loadCustomers();
        comboBoxUser.addItem("-- Select Username --");
        loadUsers();

        btnCreateBill = createStyledButton("Create Bill", new Color(52, 152, 219), "/Image/icons8-add-28.png");
        btnRefreshBill = createStyledButton("Refresh", new Color(243, 156, 18), "/Image/icons8-refresh-28.png");  

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton viewButton = new JButton("View");
        viewButton.setBackground(new Color(26, 188, 156));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewButton.setFocusPainted(false);
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(searchButton);
        buttonPanel.add(viewButton);
        buttonPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Bill ID:")); formPanel.add(txtBillID);
        formPanel.add(new JLabel("Customer:")); formPanel.add(comboBoxCustomer);
        formPanel.add(new JLabel("User Name: ")); formPanel.add(comboBoxUser);
        formPanel.add(new JLabel("Date:")); formPanel.add(txtDate);
        formPanel.add(new JLabel("")); formPanel.add(btnCreateBill);
        formPanel.add(new JLabel("")); formPanel.add(btnRefreshBill);

        // ✅Initialize tableModel
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[] { "ID", "Customer ID", " UserName", "Total Amount", "Date" });
        billTable = new JTable(tableModel);
        designTable(billTable);
        JScrollPane tableScroll = new JScrollPane(billTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("📋 All Bills"));

        btnCreateBill.addActionListener(e -> insertBill());
        btnRefreshBill.addActionListener(e -> loadBillsFromDatabase());
        searchButton.addActionListener(e -> searchBill());
        viewButton.addActionListener(e -> viewSelectedProduct());

        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
        add(tableScroll, BorderLayout.CENTER);

        loadBillsFromDatabase();
    }

    private void loadCustomers() {
        try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM Customers");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                comboBoxCustomer.addItem(name);
                CustomerMap.put(name, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠ Failed to load customers.");
        }
    }

    private void loadUsers() {
        try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT id, username FROM Users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("username");
                comboBoxUser.addItem(name);
                UserMap.put(name, id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠ Failed to load users.");
        }
    }

    private JButton createStyledButton(String text, Color bgColor, String iconPath) {
        JButton button = new JButton(text);
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                System.err.println("⚠ Failed to load icon: " + iconPath);
            }
        }
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void designTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
    }

    private String generateBillId() {
        return "BILL" + System.currentTimeMillis();
    }

    public void searchBill() {
        String keyword = JOptionPane.showInputDialog(this, "Enter bill ID to search:");
        if (keyword == null || keyword.trim().isEmpty()) return;

        tableModel.setRowCount(0); // Clear existing rows

        String sql = "SELECT b.id, c.name AS customer_name, u.username AS user_name, " +
                    "b.totalAmount, b.date " +
                    "FROM Bills b " +
                    "JOIN Customers c ON b.customer_id = c.id " +
                    "JOIN Users u ON b.user_id = u.id " +
                    "WHERE b.id LIKE ?";

        try (Connection connection = DriverManager.getConnection(url, dbuser, dbPassword);
            PreparedStatement ps = connection.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword.trim() + "%";
            ps.setString(1, likeKeyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("customer_name"),
                        rs.getString("user_name"),
                        rs.getDouble("totalAmount"),
                        rs.getTimestamp("date")
                    });
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Search failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //View
    public void viewSelectedProduct() {
            int selectedRow = billTable.getSelectedRow();
            if (selectedRow != -1) {
                StringBuilder billInfo = new StringBuilder();
                billInfo.append("Bills Details:\n\n");
                billInfo.append("ID: ").append(tableModel.getValueAt(selectedRow, 0)).append("\n");
                billInfo.append("Customer Name: ").append(tableModel.getValueAt(selectedRow, 1)).append("\n");
                billInfo.append("User Name: ").append(tableModel.getValueAt(selectedRow, 2)).append("\n");
                billInfo.append("Total Amount: ").append(tableModel.getValueAt(selectedRow, 3)).append("\n");
                billInfo.append("Date: ").append(tableModel.getValueAt(selectedRow, 4)).append("\n");
        

                JOptionPane.showMessageDialog(this, billInfo.toString(), "Bills Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a bill to view", "Warning", JOptionPane.WARNING_MESSAGE);
            }
    }
    private void insertBill() {
        String billId = txtBillID.getText().trim();
        String customer = comboBoxCustomer.getSelectedItem() != null ? comboBoxCustomer.getSelectedItem().toString() : "";
        String user = comboBoxUser.getSelectedItem() != null ? comboBoxUser.getSelectedItem().toString() : "";
        String date = txtDate.getText().trim();

        if (customer.equals("-- Select Customer --") || customer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Please select a customer.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword)) {
            String sql = "INSERT INTO Bills (id, customer_id, user_id, totalAmount, date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, billId);
            stmt.setInt(2, CustomerMap.get(customer));
            stmt.setInt(3, UserMap.get(user));
            stmt.setDouble(4, 0.0);
            stmt.setDate(5, Date.valueOf(date));
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Bill created successfully.");

            // Open Billing_Item form (must exist in your project)
            Billing_Item billingItemForm = new Billing_Item(billId);
            JFrame itemFrame = new JFrame("🛒 Add Items to Bill: " + billId);
            itemFrame.setContentPane(billingItemForm);
            itemFrame.setSize(650, 500);
            itemFrame.setLocationRelativeTo(null);
            itemFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            itemFrame.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    // Optional: refresh table after items added
                    updateTotalAmount(billId);
                    loadBillsFromDatabase();
                    txtBillID.setText(generateBillId());
                }
            });
            itemFrame.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
        }
    }

    private void loadBillsFromDatabase() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword)) {
            String sql = """
                SELECT b.id, c.name AS customer_name, u.username AS user_name, b.date, b.totalAmount
                FROM Bills b
                LEFT JOIN Customers c ON b.customer_id = c.id
                LEFT JOIN Users u ON b.user_id = u.id
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String customer = rs.getString("customer_name");
                String user = rs.getString("user_name");
                String total = String.format("$%.2f", rs.getDouble("totalAmount"));
                String date = new SimpleDateFormat("dd-MM-yyyy").format(rs.getDate("date"));
                tableModel.addRow(new Object[]{id, customer, user, total, date});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Load Failed: " + ex.getMessage());
        }
    }
    public void updateTotalAmount(String billId) {
        String sql = """
            UPDATE Bills
            SET totalAmount = (
                SELECT SUM(price * quantity)
                FROM Bill_Items
                WHERE bill_id = ?
            )
            WHERE id = ?
        """;

        try (Connection conn = DriverManager.getConnection(url, dbuser, dbPassword);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, billId);
            ps.setString(2, billId);
            ps.executeUpdate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Failed to update total amount: " + ex.getMessage());
        }
    }

}
