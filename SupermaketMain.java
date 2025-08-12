 import javax.swing.*;
 import java.awt.*;
 public class SupermaketMain {

    public SupermaketMain(){
        JFrame frame = new JFrame("Supermarket Billing System");
        frame.setSize(900, 600);
        ImageIcon image = new ImageIcon(SupermaketMain.class.getResource("/Image/icons8-supermarket-64.png"));
        frame.setIconImage(image.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Create the main menu
        main_menu(frame);

        // Set the frame to be visible
        frame.setVisible(true);
        
    }
    
    public void main_menu(JFrame frame){
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", new Dashboard());
        tabbedPane.addTab("Product", new Products_Management());
        tabbedPane.addTab("Category", new Category_manager());
        tabbedPane.addTab("Billing", new Bills());
        tabbedPane.addTab("Customer", new CustomerForm());
        tabbedPane.addTab("Settings", new SettingForm());

        frame.add(tabbedPane);

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupermaketMain::new);
    }
 }
        