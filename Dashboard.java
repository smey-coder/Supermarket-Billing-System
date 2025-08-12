import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class Dashboard extends JPanel {

    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
    private final String dbuser = "sa";
    private final String password = "hello";

    private JLabel lblUsers, lblProducts, lblRevenue, lblTodayRevenue;
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public Dashboard() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Summary Panel (Top)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        summaryPanel.setBackground(new Color(245, 245, 245));

        lblUsers = createSummaryLabel("Users: 0");
        lblProducts = createSummaryLabel("Products: 0");
        lblRevenue = createSummaryLabel("Revenue: $0.00");
        lblTodayRevenue = createSummaryLabel("Today Revenue: $0.00");

        summaryPanel.add(createCard(lblUsers, new Color(66, 133, 244)));
        summaryPanel.add(createCard(lblProducts, new Color(52, 168, 83)));
        summaryPanel.add(createCard(lblRevenue, new Color(251, 188, 5)));
        summaryPanel.add(createCard(lblTodayRevenue, new Color(234, 67, 53)));

        add(summaryPanel, BorderLayout.NORTH);

        // Sales Table (Center)
        tableModel = new DefaultTableModel();
        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(25);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Today's Sales"));
        add(scrollPane, BorderLayout.CENTER);

        // Refresh Button (Bottom)
        JButton btnRefresh = new JButton("Refresh Dashboard");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(66, 133, 244));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshDashboard());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        initTable();
        refreshDashboard();
    }

    private JLabel createSummaryLabel(String text) {
        JLabel label = new JLabel("<html><div style='text-align:center;'>" + text + "</div></html>", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel createCard(JLabel label, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setPreferredSize(new Dimension(200, 100));
        card.add(label, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return card;
    }

    private void initTable() {
        tableModel.setColumnIdentifiers(new String[]{
                "Bill ID", "Customer ID", "UserName", "$Total Amount", "Date"
        });
    }

    private void refreshDashboard() {
        try (Connection conn = DriverManager.getConnection(url, dbuser, password)) {
            Statement stmt = conn.createStatement();

            // Total Users
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Users");
            if (rs.next()) lblUsers.setText("Users: " + rs.getInt(1));

            // Total Products
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Products");
            if (rs.next()) lblProducts.setText("Products: " + rs.getInt(1));

            // Total Revenue
            rs = stmt.executeQuery("SELECT ISNULL(SUM(totalAmount), 0) FROM Bills");
            if (rs.next()) {
                double total = rs.getDouble(1);
                lblRevenue.setText("Revenue: $" + String.format("%.2f", total));
            }

            // Today Revenue
            LocalDate today = LocalDate.now();
            PreparedStatement pst = conn.prepareStatement("SELECT ISNULL(SUM(totalAmount), 0) FROM Bills WHERE CAST(date AS DATE) = ?");
            pst.setDate(1, Date.valueOf(today));
            rs = pst.executeQuery();
            if (rs.next()) {
                double todayRev = rs.getDouble(1);
                lblTodayRevenue.setText("Today Revenue: $" + String.format("%.2f", todayRev));
            }

            // Today's Sales Table
            tableModel.setRowCount(0);
            String sql = """
                    SELECT 
                        b.id, 
                        c.name AS customer_name, 
                        u.username, 
                        b.date, 
                        SUM(i.price * i.quantity) AS totalAmount
                    FROM Bills b
                    LEFT JOIN Bill_Items i ON b.id = i.bill_id
                    LEFT JOIN Users u ON b.user_id = u.id
                    LEFT JOIN Customers c ON b.customer_id = c.id
                    WHERE CAST(b.date AS DATE) = ?
                    GROUP BY b.id, c.name, u.username, b.date
            """;
            pst = conn.prepareStatement(sql);
            pst.setDate(1, Date.valueOf(today));
            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        rs.getString("id"),
                        rs.getString("customer_name"),
                        rs.getString("username"),
                        rs.getDouble("totalAmount"),
                        rs.getDate("date")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Supermarket Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new Dashboard());
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}