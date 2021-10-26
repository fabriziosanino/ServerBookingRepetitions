package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DAO {
    private static final String url = "jdbc:mysql://localhost:3306/prova_app";
    private static final String user = "root";
    private static final String password = "";

    public static void registerDriver() {
        try{
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public static int queryDB(String query) {
        Connection conn = null;
        int ret = -1;
        try {
            conn = DriverManager.getConnection(url, user, password);

            Statement st = conn.createStatement();
            ret = st.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return ret;
    }
}
