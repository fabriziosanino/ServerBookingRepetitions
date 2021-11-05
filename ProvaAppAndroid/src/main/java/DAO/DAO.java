package DAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;

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
                u = new User(rs.getString("Account"), rs.getString("Pwd"), rs.getString("Name"), rs.getString("Surname"));
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

    public JSONArray getBookedRepetitions(String query){
        Connection conn = null;
        JSONArray dbBookedRepetitions = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            dbBookedRepetitions = new JSONArray();
            while (rs.next()) {
                try {
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("day", rs.getString("day"));
                    innerObj.put("startTime", rs.getString("startTime"));
                    innerObj.put("IDCourse", rs.getInt("IDCourse"));
                    innerObj.put("IDTeacher", rs.getInt("IDTeacher"));
                    dbBookedRepetitions.put(innerObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        return dbBookedRepetitions;
    }
}
