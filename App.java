import java.sql.*;
import javax.swing.SwingUtilities;

public class App extends Login_Form{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login_Form().setVisible(true);
        });
    }
}

