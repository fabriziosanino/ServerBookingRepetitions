package DAO;

import java.sql.*;

public class DAO {
    private String url;
    private String user;
    private String password;

    public DAO(String url, String usr, String pwd) {
        this.url = url;
        this.user = usr;
        this.password = pwd;
    }

    public static void registerDriver() {
        try{
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public int queryDB(String query) {
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

    public User getUser(String query) {
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
