import java.sql.*;
public class Supermarket_database_connect {
    public void supermarketDatabase(){
        final String url = "jdbc:sqlserver://localhost:1433;databaseName=Supermarket Billing System_Assignment_Java;encrypt=true;trustServerCertificate=true;";
        final String user = "sa";
        final String password = "hello";

    try{
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    } catch(ClassNotFoundException ex){
        System.out.println("Sql Server Driver not found!: ");
        ex.printStackTrace();
        return;
    }

    try (Connection conn = DriverManager.getConnection(url, user, password)){
        System.out.println("Connected Successfully!");
    } catch(SQLException e){
        System.out.println("Connection failed!");
        e.printStackTrace();
    }
  }
  public static void main(String[] args) {
       Supermarket_database_connect co = new Supermarket_database_connect();
       co.supermarketDatabase();
  }

}
 
