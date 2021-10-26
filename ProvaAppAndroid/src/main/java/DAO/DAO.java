package DAO;

import java.sql.*;

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

    public static User getUser(String query) {
        Connection conn = null;
        User u = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                u = new User(rs.getString("Name"), rs.getString("Email"), rs.getString("Password"));
            }
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

        return u;
    }
}
