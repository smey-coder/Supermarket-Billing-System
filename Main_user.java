import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import com.formdev.flatlaf.FlatLightLaf;

public class Main_user extends JFrame {

    public Main_user() {
        setTitle("Supermarket Billing System");
        setIconImage(new ImageIcon(Main_menu.class.getResource("/Image/icons8-supermarket-64.png")).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Sidebar panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 45, 60));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));

        // Content panel with CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);

        // Navigation items
        Map<String, String> navItems = new LinkedHashMap<>();
        //navItems.put("Dashboard", "/Image/icons8-dashboard-28.png");
        //navItems.put("Products", "/Image/icons8-product-28.png");
        //navItems.put("Category", "/Image/icons8-category-28.png");
        navItems.put("Bills", "/Image/icons8-bill-28.png");
        navItems.put("Customer", "/Image/icons8-customer-28.png");
        navItems.put("Setting", "/Image/icons8-setting-28.png");
        
        //Image sidebar and text
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/Image/icons8-supermarket-96.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        JLabel textlLabel = new JLabel("Welcome To User!");
        //Font
        textlLabel.setForeground(Color.WHITE);
        textlLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        textlLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //Design Layout
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(textlLabel);
        sidebar.add(Box.createVerticalStrut(20));


        for (Map.Entry<String, String> entry : navItems.entrySet()) {

            String item = entry.getKey();
            String iconPath = entry.getValue();

            JButton btn = createSidebarButton(item, iconPath);
            sidebar.add(Box.createVerticalStrut(10));
            sidebar.add(btn);

            JPanel panel;
            switch (item) {
                
                case "Products":
                    panel = new Products_Management();
                    break;
                case "Category":
                    panel = new Category_manager();
                    break;
                case "Bills":
                    panel = new Bills();
                    break;
                case "Customer":
                    panel = new CustomerForm();
                    break;
                
                default:
                    panel = createPlaceholderPanel(item);
                    break;
            }

            contentPanel.add(panel, item);

            // Switch card on click
            btn.addActionListener(e -> cardLayout.show(contentPanel, item));
        }

        // Add Logout Button
        sidebar.add(Box.createVerticalGlue());
        JButton logoutButton = createSidebarButton("Logout", "/Image/icons8-logout-28.png");
        logoutButton.setBackground(new Color(200, 60, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setMaximumSize(new Dimension(200, 45));
        logoutButton.addActionListener(e -> {
            dispose(); // Close current form
            new Login_Form().setVisible(true); // Open Login Form
        });
        sidebar.add(logoutButton);
        sidebar.add(Box.createVerticalStrut(15));

        // Frame Layout
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private static JButton createSidebarButton(String text, String iconPath) {
        JButton btn = new JButton(text);

        java.net.URL resource = Main_menu.class.getResource(iconPath);
        if (resource != null) {
            ImageIcon icon = new ImageIcon(resource);
            btn.setIcon(icon);
        } else {
            System.err.println("⚠️ Icon not found: " + iconPath);
        }

        
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60, 65, 80));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(90, 95, 110));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 65, 80));
            }
        });

        return btn;
    }

    private static JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("<html><h1>" + title + "</h1><p>This is the " + title + " page.</p></html>", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 20f));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 999);
            UIManager.put("Component.arc", 999);
            UIManager.put("ProgressBar.arc", 999);
            UIManager.put("TextComponent.arc", 999);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Main_menu mainMenu = new Main_menu();
            mainMenu.setVisible(true);
        });
    }
}
