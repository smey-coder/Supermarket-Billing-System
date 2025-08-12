import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.sql.*;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Products_Management extends JPanel {
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String user = "sa";
    private final String dbPassword = "hello";
    private final Map<String, Integer> categoryMap = new HashMap<>();
    JTextField productNamTextField = new JTextField(15);
    JComboBox<String> categorField = new JComboBox<>();
    JTextField pricTextField = new JTextField(15);
    JTextField stockQtyField = new JTextField(15);
    JTextField descriptionField = new JTextField(15);
    JTextField txtproductIdField = new JTextField(15);
    private int selectedProductId = -1;
    
    String[] columns = {"ID","Name", "Category", "Price", "Stock Quantity", "Description", "Date Created", "Date updated"};
    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);  // Use the class-level one
     
    JTable productTable = new JTable(tableModel);
    public Products_Management() {
        // Table for products
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.setRowHeight(25);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productTable.getTableHeader().setBackground(new Color(230, 230, 230));

        JScrollPane tableScroll = new JScrollPane(productTable);
        readProducts(tableModel);
        selectProducts();

        setLayout(new BorderLayout(10, 10));

        // Product panel (top)
        JPanel productPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        productPanel.setBackground(Color.WHITE);
        productPanel.setBorder(BorderFactory.createTitledBorder("Products Details:"));
        productPanel.add(new JLabel("Name: "));
        productPanel.add(productNamTextField);
        productPanel.add(new JLabel("Category:"));
        productPanel.add(categorField);
        productPanel.add(new JLabel("Price:"));
        productPanel.add(pricTextField);
        productPanel.add(new JLabel("Stock Quantity:"));
        productPanel.add(stockQtyField);
        productPanel.add(new JLabel("Description:"));
        productPanel.add(descriptionField);
        productPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC)), 
            "Product Details", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 14), 
            new Color(0x333333)
        ));
        productPanel.setBackground(new Color(250, 250, 250));
        //Category database
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id,name FROM Category");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                categorField.addItem(name);
                categoryMap.put(name, id); // Store the mapping of category name to ID
                
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton createBtn = new JButton("Create");
        JButton deleteBtn = new JButton("Delete");
        JButton updateBtn = new JButton("Update");
        JButton viewBtn = new JButton("View");
        JButton searchBtn = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        JButton refreshBtu = new JButton("Refresh");
        createBtn.setBackground(new Color(52, 152, 219)); // Blue
        updateBtn.setBackground(new Color(241, 196, 15)); // Yellow
        deleteBtn.setBackground(new Color(231, 76, 60));  // Red
        viewBtn.setBackground(new Color(155, 89, 182));   // Purple
        clearButton.setBackground(Color.BLACK);
        searchBtn.setBackground(new Color(26, 188, 156)); // Green
        refreshBtu.setBackground(new Color(149, 165, 166));// Grey

        for (JButton btn : new JButton[]{createBtn, updateBtn, deleteBtn, viewBtn, searchBtn, clearButton, refreshBtu}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        buttonPanel.add(createBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshBtu);
        buttonPanel.setBackground(Color.WHITE);

        //Button Action

        
        //Action buttons created
        createBtn.addActionListener(e -> createdProduct());
        updateBtn.addActionListener(e -> updateProduts());
        deleteBtn.addActionListener(e -> {
            deleteProduct();
            tableModel.setRowCount(0);
            readProducts(tableModel);
        });
        searchBtn.addActionListener(e -> searchProduct());
        clearButton.addActionListener(e -> clearFields(productNamTextField, categorField, pricTextField, stockQtyField, descriptionField));
        viewBtn.addActionListener(e -> viewSelectedProduct());
        //Button refresh
        refreshBtu.addActionListener(e -> {
            tableModel.setRowCount(0);
            readProducts(tableModel);

        });
        
        // New panel for buttons and table
        JPanel tableAndButtonPanel = new JPanel(new BorderLayout(10, 10));
        tableAndButtonPanel.add(buttonPanel, BorderLayout.NORTH);
        tableAndButtonPanel.add(tableScroll, BorderLayout.CENTER);

        // Add panels to this JPanel
        add(productPanel, BorderLayout.NORTH);
        add(tableAndButtonPanel, BorderLayout.CENTER);
        
    }
    public void viewSelectedProduct() {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                StringBuilder productInfo = new StringBuilder();
                productInfo.append("Product Details:\n\n");
                productInfo.append("ID: ").append(tableModel.getValueAt(selectedRow, 0)).append("\n");
                productInfo.append("Name: ").append(tableModel.getValueAt(selectedRow, 1)).append("\n");
                productInfo.append("Category: ").append(tableModel.getValueAt(selectedRow, 2)).append("\n");
                productInfo.append("Price: ").append(tableModel.getValueAt(selectedRow, 3)).append("\n");
                productInfo.append("Stock Quantity: ").append(tableModel.getValueAt(selectedRow, 4)).append("\n");
                productInfo.append("Description: ").append(tableModel.getValueAt(selectedRow, 5)).append("\n");
                productInfo.append("Date Created: ").append(tableModel.getValueAt(selectedRow, 6)).append("\n");
                productInfo.append("Date Updated: ").append(tableModel.getValueAt(selectedRow, 7));

                JOptionPane.showMessageDialog(this, productInfo.toString(), "Product Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product to view", "Warning", JOptionPane.WARNING_MESSAGE);
            }
    }
    public void selectProducts(){
        productTable.getSelectionModel().addListSelectionListener(e -> {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            productNamTextField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            categorField.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
            pricTextField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            stockQtyField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            descriptionField.setText(tableModel.getValueAt(selectedRow, 5).toString());
        }
      });

    }
    //Method created products
    public void createdProduct(){
            String name = productNamTextField.getText();
            String category = categorField.getSelectedItem()  != null ? categorField.getSelectedItem().toString() : "";
            String price = pricTextField.getText();
            String stock = stockQtyField.getText();
            String description = descriptionField.getText();
            if(name.isEmpty() ||  category.isEmpty() || price.isEmpty() || stock.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Add logic to create product in database
                try{
                    Connection connection = DriverManager.getConnection(url, user, dbPassword);
                    String sql = "INSERT INTO Products (name, category_id, price, stockQuantity, description, date_created) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, categoryMap.get(category)); // Use the map to get
                    preparedStatement.setDouble(3, Integer.parseInt(price));
                    preparedStatement.setInt(4, Integer.parseInt(stock));
                    preparedStatement.setString(5, description);
                    Timestamp dateTime = new Timestamp(System.currentTimeMillis());
                    preparedStatement.setTimestamp(6, dateTime);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Product created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.addRow(new Object[] {name, category, price, stock, description, dateTime});
                    tableModel.setRowCount(0);
                    readProducts(tableModel);
                    clearFields(productNamTextField, categorField, pricTextField, stockQtyField, descriptionField);
                    connection.close();

                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this, "Error creating product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

    }
    //Method updated
    public void updateProduts(){
        int selectedRow =  productTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a product to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedProductId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = productNamTextField.getText();
        String category = categorField.getSelectedItem().toString();
        String price = pricTextField.getText();
        String stock = stockQtyField.getText();
        String description = descriptionField.getText();
        try(Connection connection = DriverManager.getConnection(url, user, dbPassword)){
            String sql = "Update Products set name = ?, category_id = ?, price = ?, stockQuantity = ?, description = ?, date_updated = ? Where id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, categoryMap.get(category));
            ps.setDouble(3, Double.parseDouble(price));
            ps.setInt(4, Integer.parseInt(stock));
            ps.setString(5, description);
            Timestamp date_updated = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(6, date_updated);
            ps.setInt(7, selectedProductId);

            int rowAffected = ps.executeUpdate();

            if(rowAffected > 0){
                JOptionPane.showMessageDialog(this, "Product updated successfully");
                tableModel.addRow(new Object[] {name, category, price, stock, description, date_updated});
                tableModel.setRowCount(0);
                readProducts(tableModel);
                clearFields(productNamTextField, categorField, pricTextField, stockQtyField, descriptionField);
              }

        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Error updating products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Deleted products
    public void deleteProduct(){
        String input = JOptionPane.showInputDialog(this, "Enter product ID to delete:");
        if (input == null || input.trim().isEmpty()) return;

        int productId;
        try {
            productId = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Product ID! Please enter a number.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {

            // Step 1: Check if product is used in Bill_Items
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM Bill_Items WHERE product_id = ?");
            check.setInt(1, productId);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                int confirmBillItems = JOptionPane.showConfirmDialog(this,
                        "This product is used in bills. Do you want to delete related bill items as well?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (confirmBillItems != JOptionPane.YES_OPTION) {
                    return;
                }

                // Step 2: Delete related Bill_Items
                PreparedStatement pst1 = conn.prepareStatement("DELETE FROM Bill_Items WHERE product_id = ?");
                pst1.setInt(1, productId);
                pst1.executeUpdate();
        }

        // Step 3: Delete the product
        PreparedStatement pst2 = conn.prepareStatement("DELETE FROM Products WHERE id = ?");
        pst2.setInt(1, productId);
        int rowsDeleted = pst2.executeUpdate();

        if (rowsDeleted > 0) {
            JOptionPane.showMessageDialog(this, "Product deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Product ID not found.");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error deleting product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    //Search products
    public void searchProduct(){
         String keyword = JOptionPane.showInputDialog(this, "Enter product name or category ID to search:");
        if (keyword == null || keyword.trim().isEmpty()) return;

        tableModel.setRowCount(0);
        String sql = "SELECT id, name, category_id, price, stockQuantity, description, date_created, date_updated FROM Products WHERE name LIKE ? OR CAST(category_id AS VARCHAR) LIKE ?";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"), rs.getInt("category_id"),
                        rs.getDouble("price"), rs.getInt("stockQuantity"),
                        rs.getString("description"), rs.getTimestamp("date_created"), rs.getTimestamp("date_updated")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Method to clear input fields
    public void clearFields(JTextField productNamTextField, JComboBox<String> categorField, JTextField pricTextField, JTextField stockQtyField, JTextField descriptionField) {
        // Clear all input fields
        productNamTextField.setText("");
        categorField.setSelectedItem(null);
        pricTextField.setText("");
        stockQtyField.setText("");
        descriptionField.setText("");
        productNamTextField.requestFocus();
    }
    
    // Method to read products from the database and populate the table model
    public void readProducts(DefaultTableModel tableModel) {
        
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id,name, category_id, price, stockQuantity, description, date_created , date_updated From Products");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category_id");
                double price = resultSet.getDouble("price");
                int stockQuantity = resultSet.getInt("stockQuantity");
                String description = resultSet.getString("description");
                Timestamp dateCreated = resultSet.getTimestamp("date_created");
                Timestamp dateUpdated = resultSet.getTimestamp("date_updated");
                tableModel.addRow(new Object[]{id, name, category, price, stockQuantity, description, dateCreated, dateUpdated});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
       
    }
}
