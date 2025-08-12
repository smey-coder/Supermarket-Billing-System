import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.FlatLightLaf;

public class Register_Form extends Supermarket_database_connect {
    private JTextField usernameField;
    private JPasswordField passwordField, confirmField;

    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String user = "sa";
    private final String dbPassword = "hello";

    public void register() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 999);
            UIManager.put("Component.arc", 999);
            UIManager.put("ProgressBar.arc", 999);
            UIManager.put("TextComponent.arc", 999);
            UIManager.put("Component.innerFocusWidth", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Register new User!");
        frame.setSize(500, 330);
        ImageIcon imageRegister = new ImageIcon(Register_Form.class.getResource("/Image/icons8-register-48.png"));
        frame.setIconImage(imageRegister.getImage());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel registerPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        registerPanel.setBackground(Color.WHITE);

        // Fields
        registerPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        registerPanel.add(usernameField);

        registerPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        registerPanel.add(passwordField);

        registerPanel.add(new JLabel("Confirm Password:"));
        confirmField = new JPasswordField(15);
        registerPanel.add(confirmField);

        JCheckBox checkPass = new JCheckBox("Show Password");
        checkPass.setBackground(Color.WHITE);
        registerPanel.add(new JLabel());
        registerPanel.add(checkPass);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.GREEN);
        registerPanel.add(new JLabel());
        registerPanel.add(registerButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.RED);
        registerPanel.add(new JLabel());
        registerPanel.add(cancelButton);

        JLabel loginLabel = new JLabel("Already have an account? Sign in here!");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerPanel.add(new JLabel());
        registerPanel.add(loginLabel);

        frame.add(registerPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // === Events ===
        checkPass.addActionListener(e -> {
            char echoChar = checkPass.isSelected() ? (char) 0 : '●';
            passwordField.setEchoChar(echoChar);
            confirmField.setEchoChar(echoChar);
        });

        cancelButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            confirmField.setText("");
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
                    String checkSql = "Select * from Users Where username = ?";
                    PreparedStatement checkStatement = conn.prepareStatement(checkSql);
                    checkStatement.setString(1, username);
                    ResultSet rs = checkStatement.executeQuery();
                    
                    if(rs.next()){
                        JOptionPane.showMessageDialog(frame, "Admin name already exists!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }else{
                        String sql = "INSERT INTO Users (username, password, role, date_created) VALUES (?, ?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, username);
                        pstmt.setString(2, password);  // Plain password (upgradeable to hashed)
                        pstmt.setString(3, "User");
                        pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                        int rowsInserted = pstmt.executeUpdate();
                        if(rowsInserted > 0){
                            JOptionPane.showMessageDialog(frame, "New user registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            frame.dispose();
                            new Login_Form(); // Open Login Form directly
                        }else{
                            JOptionPane.showMessageDialog(frame, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        
                    }
                    // Open Login Form directly
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new Login_Form(); // Open Login Form
            }
        });
    }
}
