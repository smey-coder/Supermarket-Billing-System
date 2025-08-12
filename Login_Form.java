import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

public class Login_Form extends JFrame {
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String user = "sa";
    private final String dbPassword = "hello";

    private JComboBox<String> roleComboBox;

    public Login_Form() {
        setTitle("Login Admin or User");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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

        ImageIcon imageLogin = new ImageIcon(Login_Form.class.getResource("/Image/icons8-laptop-password-96.png"));
        setIconImage(imageLogin.getImage());

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(255, 69, 0));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel("🛒");
        logoLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel slogan1 = new JLabel("Supermarket Billing System");
        JLabel slogan2 = new JLabel("Every Product");
        JLabel slogan3 = new JLabel("Nice Service");

        for (JLabel label : new JLabel[]{slogan1, slogan2, slogan3}) {
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 18));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        leftPanel.add(Box.createVerticalStrut(50));
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(slogan1);
        leftPanel.add(slogan2);
        leftPanel.add(slogan3);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("LOGIN SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.ORANGE);

        JLabel iconLabel = new JLabel(new ImageIcon(Login_Form.class.getResource("/Image/icons_admin-96.png")), SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel);
        topPanel.add(iconLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(new String[]{"Admin", "User"});
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(roleComboBox, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passField, gbc);

        JCheckBox checkBox = new JCheckBox("Show Password");
        checkBox.setFont(new Font("Arial", Font.PLAIN, 12));
        checkBox.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(checkBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(255, 165, 0));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        JLabel registerJLabel = new JLabel("Not Account yet? Register here!");
        registerJLabel.setFont(new Font("Arial", Font.BOLD, 12));
        registerJLabel.setForeground(Color.BLUE);
        registerJLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerJLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Register_Form openForm = new Register_Form();
                openForm.register();
                dispose();
            }
        });

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.add(registerJLabel);

        gbc.gridy = 5;
        formPanel.add(registerPanel, gbc);

        checkBox.addActionListener(e -> passField.setEchoChar(checkBox.isSelected() ? (char) 0 : '●'));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = roleComboBox.getSelectedItem().toString().toLowerCase();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isValidUser(username, password, role)) {
                JOptionPane.showMessageDialog(this, "Login successful as " + role.toUpperCase(), "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (role.equals("admin")) {
                    new Main_menu().setVisible(true);
                } else {
                    new Main_user().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or role!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(formPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(250);
        splitPane.setEnabled(false);

        add(splitPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private boolean isValidUser(String username, String password, String role) {
        try (Connection conn = DriverManager.getConnection(url, user, dbPassword)) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login_Form::new);
    }
}
