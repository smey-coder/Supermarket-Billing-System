import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.TitledBorder;

public class CustomerForm extends JPanel {
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String user = "sa";
    private final String dbPassword = "hello";

    private JTextField txtName, txtPhone, txtAddress;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerForm() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Customer Management"));

       
        // ----------- Input Form Panel -----------
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        txtName = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();

        formPanel.add(new JLabel("Customer Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(txtAddress);

        JButton btnAdd = new JButton("Add Customer");
        //Design Button
        btnAdd.setBackground(new Color(26, 188, 156)); // Green

        JPanel buttoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10 , 10));
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton viewButton = new JButton("View");
        updateButton.setBackground(new Color(52, 152, 219)); // Blue
        deleteButton.setBackground(new Color(241, 196, 15)); // Yellow
        clearButton.setBackground(new Color(231, 76, 60));  // Red
        viewButton.setBackground(new Color(155, 89, 182));   // Purple
        buttoPanel.add(updateButton);
        buttoPanel.add(deleteButton);
        buttoPanel.add(viewButton);
        buttoPanel.add(clearButton);
        buttoPanel.setBackground(Color.WHITE);
        
        

        for (JButton btn : new JButton[]{btnAdd, updateButton, deleteButton, clearButton, viewButton}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        formPanel.add(new JLabel(""));
        formPanel.add(btnAdd);
        
        

        // ----------- Table Panel -----------
        String[] columns = {"Customer ID", "Name", "Phone", "Address"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
         //Design Table
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.setRowHeight(25);
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerTable.getTableHeader().setBackground(new Color(230, 230, 230));
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Customer List"));

        // ----------- Add to Layout -----------
        JPanel tableAndButtonPanel = new JPanel(new BorderLayout(10, 10));
        tableAndButtonPanel.add(buttoPanel, BorderLayout.NORTH);
        tableAndButtonPanel.add(scrollPane, BorderLayout.CENTER);

        add(formPanel, BorderLayout.NORTH);
        add(tableAndButtonPanel, BorderLayout.CENTER);

        // ----------- Button Action -----------
        btnAdd.addActionListener(e -> insertCustomer());
        deleteButton.addActionListener(e ->deleteCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        viewButton.addActionListener(e -> viewSelectedCustomer());
        clearButton.addActionListener(e -> clearFields());

        // ----------- Load existing data -----------
        loadCustomers();
        selectCustomer();
    }
    public void clearFields(){
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtName.requestFocus();

        
    }
    //View Customers
    public void viewSelectedCustomer() {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                StringBuilder customerInfo = new StringBuilder();
                customerInfo.append("Customer Details:\n\n");
                customerInfo.append("ID: ").append(tableModel.getValueAt(selectedRow, 0)).append("\n");
                customerInfo.append("Name: ").append(tableModel.getValueAt(selectedRow, 1)).append("\n");
                customerInfo.append("Phone: ").append(tableModel.getValueAt(selectedRow, 2)).append("\n");
                customerInfo.append("Address: ").append(tableModel.getValueAt(selectedRow, 3)).append("\n");

                JOptionPane.showMessageDialog(this, customerInfo.toString(), "Customer Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a customer to view", "Warning", JOptionPane.WARNING_MESSAGE);
            }
    }
    //Delete Customer
    private void deleteCustomer(){
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Deleting this customer will also delete related bills and bill items. Continue?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {

            // 1. Delete related bill items
            String sqlDeleteBillItems = "DELETE FROM Bill_Items WHERE bill_id IN (SELECT id FROM Bills WHERE customer_id = ?)";
            try (PreparedStatement stmtBillItems = conn.prepareStatement(sqlDeleteBillItems)) {
                stmtBillItems.setInt(1, customerId);
                stmtBillItems.executeUpdate();
            }

            // 2. Delete related bills
            String sqlDeleteBills = "DELETE FROM Bills WHERE customer_id = ?";
            try (PreparedStatement stmtBills = conn.prepareStatement(sqlDeleteBills)) {
                stmtBills.setInt(1, customerId);
                stmtBills.executeUpdate();
            }

            // 3. Delete customer
            String sqlDeleteCustomer = "DELETE FROM Customers WHERE id = ?";
            try (PreparedStatement stmtCustomer = conn.prepareStatement(sqlDeleteCustomer)) {
                stmtCustomer.setInt(1, customerId);
                stmtCustomer.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Customer and all related data deleted successfully.");
            loadCustomers();
            clearInputs();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
        }
    }
    //Update Customer
    private void updateCustomer(){
        int selectedRow = customerTable.getSelectedRow();
            if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update.");
            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields before updating.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            String sql = "UPDATE Customers SET name = ?, phone = ?, address = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, address);
            stmt.setInt(4, customerId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer updated successfully.");
            loadCustomers();
            clearInputs();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage());
        }
    }
    //View
    private void insertCustomer() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            String sql = "INSERT INTO Customers (name, phone, address) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, address);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer added successfully.");
            clearInputs();
            loadCustomers(); // refresh table
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    public void selectCustomer(){
        customerTable.getSelectionModel().addListSelectionListener(e -> {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtPhone.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtAddress.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
      });

    }
    private void loadCustomers() {
        tableModel.setRowCount(0); // clear existing rows

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            String sql = "SELECT * FROM Customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                tableModel.addRow(new Object[]{id, name, phone, address});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage());
        }
    }

    private void clearInputs() {
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
    }

    // ---------- Main to test ----------
    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Customer Form");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new CustomerForm());
            frame.setVisible(true);
        });
    }*/
}

