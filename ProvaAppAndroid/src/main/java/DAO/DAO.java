package DAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;
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

    /*
    * @return: a JSONArray like this:
      JSONArray dbFreeRepetitions =
               [
                   {
                     day:monday,
                     startTime:15:00,
                     coursesList:[
                                   {
                                       Title: Prog III,
                                       IDCourse: 5,
                                       teachersList:[
                                            {
                                                Surname: Esposito,
                                                Name: Roberto,
                                                IDTeacher: 1
                                            },
                                            {
                                                 Surname: Aringhieri,
                                                 Name: Roberto,
                                                 IDTeacher:  6
                                            }
                                       ]
                                   },
                                   {
                                       Title: IUM,
                                       IDCourse: 8,
                                       teachersList:[
                                            {
                                                Surname: Esposito,
                                                Name: Roberto,
                                                IDTeacher: 1
                                            }
                                       ]
                                   },
                                   {....}
                    ]
                  }
               ]
    * */
    public JSONArray getFreeRepetitions(String day, String state){
        Connection conn = null;
        PreparedStatement st = null;
        JSONArray dbFreeRepetitions = null;

        try {
            conn = DriverManager.getConnection(url, user, password);

            st = conn.prepareStatement("SELECT IDCourse, Title FROM courses;");
            ResultSet courses = st.executeQuery();
            st = conn.prepareStatement("SELECT t.IDCourse, c.Title, t.IDTeacher, tc.Surname, tc.Name FROM teaches as t natural join teachers as tc natural join courses as c;");
            ResultSet coursesTeachersAss = st.executeQuery();
            st = conn.prepareStatement("SELECT Day, StartTime, IDCourse, IDTeacher FROM repetitions WHERE State = ? and Day = ?;");
            st.setString(1, state);
            st.setString(2, day);
            ResultSet bookedRepetitions = st.executeQuery();

            dbFreeRepetitions = calculateFreeRepetitions(courses, coursesTeachersAss, bookedRepetitions);
            
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

        return dbFreeRepetitions;
    }

    /*
    * This method returns the ALL the free courses and teachers available in a certain hour and day (for all the week)
    * */
    private JSONArray calculateFreeRepetitions(ResultSet courses, ResultSet coursesTeachersAss, ResultSet bookedRepetitions){
        JSONArray dbFreeRepetitions = new JSONArray();

        for (int j=15; j<19; j++){ //for every hour
            try{
                JSONArray coursesList = new JSONArray();

                while(courses.next()){
                    JSONObject courseItem = new JSONObject();
                    courseItem.put("Title", courses.getString("Title"));
                    courseItem.put("IDCourse", courses.getString("IDCourse"));

                    JSONArray teachersList = new JSONArray();

                    if(courseItem.getString("Title").equals("Reti I"))
                        System.out.println("");

                    while(coursesTeachersAss.next()) {
                        if(coursesTeachersAss.getString("IDCourse").equals(courses.getString("IDCourse"))){
                            boolean isBooked=false;
                            while(bookedRepetitions.next() && !isBooked){
                                String [] atHour = bookedRepetitions.getString("startTime").split(":");
                                if(String.valueOf(j).equals(atHour[0]) && coursesTeachersAss.getString("IDCourse").equals(bookedRepetitions.getString("IDCourse")) && coursesTeachersAss.getString("IDTeacher").equals(bookedRepetitions.getString("IDTeacher")))
                                    isBooked=true;
                            }
                            if(!isBooked){
                                JSONObject teacherItem = new JSONObject();
                                teacherItem.put("Surname", coursesTeachersAss.getString("Surname"));
                                teacherItem.put("Name", coursesTeachersAss.getString("Name"));
                                teacherItem.put("IDTeacher", coursesTeachersAss.getInt("IDTeacher"));
                                teachersList.put(teacherItem);
                            }
                            bookedRepetitions.beforeFirst();
                        }
                    }
                    coursesTeachersAss.beforeFirst();
                    courseItem.put("teachersList", teachersList);
                    coursesList.put(courseItem);
                }
                JSONObject innerObj = new JSONObject();
                String onHour =  String.valueOf(j).concat(":00");
                innerObj.put("startTime", onHour);
                innerObj.put("coursesList", coursesList);
                dbFreeRepetitions.put(innerObj);
                courses.beforeFirst();
            }catch (SQLException | JSONException e){
                System.out.println(e.getMessage());
            }
        }
        return dbFreeRepetitions;
    }

    public JSONArray getBookedHistoryRepetitions(String state, String account){
        Connection conn = null;
        PreparedStatement st = null;
        JSONArray dbBookedHistoryRepetitions = null;

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
                }
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

        return dbBookedHistoryRepetitions;
    }
}