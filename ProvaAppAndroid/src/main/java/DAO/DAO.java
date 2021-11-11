package DAO;

import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

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

    public boolean checkConnession(){
        Connection conn = null;
        Boolean connected = false;
        PreparedStatement st = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT version()";
            st = conn.prepareStatement(query);
            ResultSet rs = st.executeQuery();
            connected = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return connected;
    }

    public int insertClientUser(String account, String pwd, String name, String surname, String role) {
        Connection conn = null;
        PreparedStatement st = null;
        int ret = -1;
        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "INSERT INTO users VALUES(?, ?, ?, ?, ?);";
            st = conn.prepareStatement(query);
            st.setString(1, account);
            st.setString(2, pwd);
            st.setString(3, name);
            st.setString(4, surname);
            st.setString(5, role);
            ret = st.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return ret;
    }

    public User checkLogin(String account, String pwd) {
        Connection conn = null;
        PreparedStatement st = null;
        User u = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT * FROM users WHERE Account = ? AND Pwd = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, account);
            st.setString(2, pwd);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                u = new User(rs.getString("Account"), rs.getString("Pwd"), rs.getString("Name"), rs.getString("Surname"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return u;
    }

    public User checkSession(String account) {
        Connection conn = null;
        PreparedStatement st = null;
        User u = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT * FROM users WHERE Account = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, account);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                u = new User(rs.getString("Account"), rs.getString("Pwd"), rs.getString("Name"), rs.getString("Surname"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return u;
    }

    public Pair<JSONArray, String> getBookedRepetitions(String state){
        Connection conn = null;
        PreparedStatement st = null;
        JSONArray dbBookedRepetitions = null;
        String error = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT Day, StartTime, IDCourse, IDTeacher FROM repetitions WHERE State = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, state);
            ResultSet rs = st.executeQuery();

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
                    error = e.getMessage();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            error = e.getMessage();
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    error = e.getMessage();
                }
            }
        }

        return new Pair<>(dbBookedRepetitions, error);
    }

    public Pair<JSONArray, String> getBookedHistoryRepetitions(String state, String account){
        Connection conn = null;
        PreparedStatement st = null;
        JSONArray dbBookedHistoryRepetitions = null;
        String error = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT Day, StartTime, IDCourse, IDTeacher FROM repetitions WHERE State = ? and Account = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, state);
            st.setString(2, account);
            ResultSet rs = st.executeQuery();

            dbBookedHistoryRepetitions = new JSONArray();
            while (rs.next()) {
                try {
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("day", rs.getString("day"));
                    innerObj.put("startTime", rs.getString("startTime"));
                    innerObj.put("IDCourse", rs.getInt("IDCourse"));
                    innerObj.put("IDTeacher", rs.getInt("IDTeacher"));
                    dbBookedHistoryRepetitions.put(innerObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = e.getMessage();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            error = e.getMessage();
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    error = e.getMessage();
                }
            }
        }

        return new Pair<>(dbBookedHistoryRepetitions, error);
    }
}
