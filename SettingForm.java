import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class SettingForm extends JPanel{
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String user = "sa";
    private final String dbPassword = "hello";

    private JPasswordField currentPasswordField, passwordField, confirmField;
    private JTextField currentIdField;
    private JComboBox<String> roleComboBox = new JComboBox<>();

    public SettingForm(){

        setLayout(new BorderLayout(10, 10));
        //Window
        setBackground(Color.CYAN);

        //Text Label
        JLabel textJLabel = new JLabel("Welcome to Supermarket Billing System", SwingConstants.CENTER);
        textJLabel.setFont(new Font("Arial", Font.BOLD, 20));
         

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Setting");

        //Profile Setting
        JMenuItem profilSetting = new JMenuItem("Change Password");
        profilSetting.addActionListener(e -> profilSetting());

        //About
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Supermarket Billing System\nVersion 1.0\nDeveloped using Java swing and SQL Server\n© 2025 Norton University", "About", JOptionPane.INFORMATION_MESSAGE);
        });

        //Logout
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            Login_Form loginForm = new Login_Form();
            loginForm.setVisible(true);
            Window window = SwingUtilities.getWindowAncestor(this);
            if(window != null){
                window.dispose();
            }
            
        });

        //Exit
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(profilSetting);
        fileMenu.add(aboutItem);
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        add(menuBar, BorderLayout.NORTH);
        add(textJLabel);
        

    }
    private void profilSetting(){
        JFrame frame = new JFrame("Change Admin password:");
        frame.setSize(500, 330);
        ImageIcon imageChangePass = new ImageIcon(Register_Form.class.getResource("/Image/icons8-change-password-58.png"));
        frame.setIconImage(imageChangePass.getImage());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel profilePanel = new JPanel(new GridLayout(9, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilePanel.setBackground(Color.WHITE);

        // Fields
        profilePanel.add(new JLabel("Current ID: "));
        currentIdField = new JTextField(15);
        profilePanel.add(currentIdField);

        profilePanel.add(new Label("Role: "));
        roleComboBox = new JComboBox<>(new String[]{"Admin", "User"});
        profilePanel.add(roleComboBox);

        profilePanel.add(new JLabel("Current password:"));
        currentPasswordField = new JPasswordField(15);

        profilePanel.add(currentPasswordField);
        profilePanel.add(new JLabel("New password:"));
        passwordField = new JPasswordField(15);
        profilePanel.add(passwordField);

        profilePanel.add(new JLabel("Confirm password:"));
        confirmField = new JPasswordField(15);
        profilePanel.add(confirmField);

        JCheckBox checkPass = new JCheckBox("Show Password");
        checkPass.setBackground(Color.WHITE);
        profilePanel.add(new JLabel());
        profilePanel.add(checkPass);

        checkPass.addActionListener(e ->{
            if (checkPass.isSelected()) {
                //currentPasswordField.setEchoChar((char) 0);
                passwordField.setEchoChar((char) 0);
                confirmField.setEchoChar((char) 0);
            } else {
                currentPasswordField.setEchoChar('●');
                passwordField.setEchoChar('●');
                confirmField.setEchoChar('●');

            }
        
        });

        JButton profileChangeButton = new JButton("Change password");
        profileChangeButton.setBackground(Color.GREEN);
        profilePanel.add(new JLabel());
        profilePanel.add(profileChangeButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.RED);
        profilePanel.add(new JLabel());
        profilePanel.add(cancelButton);

        //---Action of button----
        profileChangeButton.addActionListener(e -> UpdateProfile());
        cancelButton.addActionListener(e -> clearFiled());


        frame.add(profilePanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
    private void UpdateProfile(){
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmField.getPassword());
        int userId = Integer.parseInt(currentIdField.getText());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            // Step 1: Check current password
            String checkSQL = "SELECT * FROM Users Where id = ? And  password = ? And role = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, currentPassword);
            checkStmt.setString(3, roleComboBox.getSelectedItem().toString());
            var rs = checkStmt.executeQuery();

            if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Current password or id is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            // Step 2: Update to new password
            String updateSQL = "UPDATE Users SET password = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateSQL);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } else {
                JOptionPane.showMessageDialog(this, "Update failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void clearFiled(){
        currentIdField.setText("");
        currentPasswordField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        currentIdField.requestFocus();
    }
    /*public static void main(String[] args) {
        new SettingForm();
    }
    */
}