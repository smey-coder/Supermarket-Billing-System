import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import java.sql.*;
import javax.swing.*;
public class Category_manager extends JPanel{
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String user = "sa";
    private final String dbPassword = "hello";
    JTextField nameField = new JTextField(20);
    JTextField descriptionField = new JTextField(20);

    String [] columns = {"ID", "Name", "Description","Date Created", "Date updated"};
    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

    JTable categoryTable = new JTable(tableModel);

    public Category_manager(){

        //Read categories from database
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryTable.setRowHeight(25);
        categoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        categoryTable.getTableHeader().setBackground(new Color(230, 230, 230));
        readCategory(tableModel);
        selectCategory();
        
        setLayout(new BorderLayout(10, 10));

        //Title Lable
        JPanel cateryPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        cateryPanel.setBackground(Color.WHITE);
        cateryPanel.setBorder(BorderFactory.createTitledBorder("Category Details"));
        cateryPanel.add(new JLabel("Name:"));
        cateryPanel.add(nameField);
        cateryPanel.add(new JLabel("Description: "));
        cateryPanel.add(descriptionField);

        //Buttons Panel
        JPanel buttoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("Create");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");
        
        buttoPanel.setBackground(Color.WHITE);
        addButton.setBackground(new Color(52, 152, 219)); // Blue
        updateButton.setBackground(new Color(241, 196, 15)); // Yellow
        deleteButton.setBackground(new Color(231, 76, 60));  // Red
        clearButton.setBackground(new Color(155, 89, 182));   // Purple
        refreshButton.setBackground(new Color(26, 188, 156)); // Green
        

        for (JButton btn : new JButton[]{addButton, updateButton, deleteButton, clearButton, refreshButton}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        buttoPanel.add(addButton);
        buttoPanel.add(updateButton);
        buttoPanel.add(deleteButton);
        buttoPanel.add(clearButton);
        buttoPanel.add(refreshButton);

        //Action buttons
        addButton.addActionListener(e -> addCategory());
        updateButton.addActionListener(e -> updateCategory());
        clearButton.addActionListener(e -> clearFields());
        deleteButton.addActionListener(e -> deleteCategory());

        refreshButton.addActionListener(e ->{
            tableModel.setRowCount(0);
            readCategory(tableModel);
            clearFields();
        });

        JPanel tableAndButtoPanel = new JPanel(new BorderLayout(10, 10));
        JScrollPane tableScroll = new JScrollPane(categoryTable);
        tableAndButtoPanel.add(buttoPanel, BorderLayout.NORTH);
        tableAndButtoPanel.add(tableScroll, BorderLayout.CENTER);

        add(cateryPanel, BorderLayout.NORTH);
        add(tableAndButtoPanel, BorderLayout.CENTER);

    }
    public void addCategory(){
        String name = nameField.getText();
        String description = descriptionField.getText();

        if(name.isEmpty() || description.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Warning", JOptionPane.WARNING_MESSAGE);
        }else{
            try{
                Connection conn = DriverManager.getConnection(url, user, dbPassword);
                String sql = "Insert into Category (name, description, date_created) Values (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, description);
                Timestamp today = new Timestamp(System.currentTimeMillis());
                ps.setTimestamp(3, today);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Categories added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
                readCategory(tableModel);
                clearFields();
                conn.close();

            }catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error creating category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //Update category
    public void updateCategory(){
        int selectedRow =  categoryTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a product to update", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int selectedCateryId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText();
        String description = descriptionField.getText();
        if(name.isEmpty() || description.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try(Connection connection = DriverManager.getConnection(url, user, dbPassword)){
            String sql = "Update Category set name = ?, description = ?, date_updated = ? Where id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, description);
            Timestamp date_updated = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(3, date_updated);
            ps.setInt(4, selectedCateryId);

            int rowAffected = ps.executeUpdate();

            if(rowAffected > 0){
                JOptionPane.showMessageDialog(this, "Product updated successfully");
                tableModel.addRow(new Object[] {name, description, date_updated});
                tableModel.setRowCount(0);
                readCategory(tableModel);

              }else{
                JOptionPane.showMessageDialog(this, "No product found with the given ID", "Error", JOptionPane.ERROR_MESSAGE);
              }

        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Error updating products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Delete category
    public void deleteCategory(){
        int selectedRow = categoryTable.getSelectedRow();
        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a category to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        int selectedCategoryId = (int) tableModel.getValueAt(selectedRow, 0);
        try(Connection connection = DriverManager.getConnection(url, user, dbPassword)){
            String sql = "Delete From Category Where id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, selectedCategoryId);
            int rowAffected = ps.executeUpdate();
            if(rowAffected > 0){
                JOptionPane.showMessageDialog(this, "Category deleted successfully");
                tableModel.setRowCount(0);
                readCategory(tableModel);
            }else{
                JOptionPane.showMessageDialog(this, "No category found with the given ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Error deleting category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Clear fields
    public void clearFields(){
        nameField.setText("");
        descriptionField.setText("");
        nameField.requestFocus();
    }
    //Select category from table
    public void selectCategory(){
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = categoryTable.getSelectedRow();
            if(selectedRow != -1){
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                descriptionField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        });
    }
    //Read database
    public void readCategory(DefaultTableModel tableModel){
        try (Connection connection = DriverManager.getConnection(url, user, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, name, description , date_created, date_updated From Category");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Timestamp dateCreated = resultSet.getTimestamp("date_created");
                Timestamp dateUpdated = resultSet.getTimestamp("date_updated");
                tableModel.addRow(new Object[]{id, name, description, dateCreated, dateUpdated});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}