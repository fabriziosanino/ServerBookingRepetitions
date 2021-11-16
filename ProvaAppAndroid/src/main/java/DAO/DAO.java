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

    //TODO: tornare al server gli erroi che possono essere avvenuti

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

    public JSONObject insertClientUser(String account, String pwd, String name, String surname, String role) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "INSERT INTO users VALUES(?, ?, ?, ?, ?);";
            st = conn.prepareStatement(query);
            st.setString(1, account);
            st.setString(2, pwd);
            st.setString(3, name);
            st.setString(4, surname);
            st.setString(5, role);

            try {
                jsonObject.put("done", true);
                jsonObject.put("inserted", st.executeQuery());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Service.setError(jsonObject, e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    Service.setError(jsonObject, e.getMessage());
                }
            }
        }

        return jsonObject;
    }

    public JSONObject checkLogin(String account, String pwd) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();
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

            try {
                jsonObject.put("done", true);
                jsonObject.put("user", u);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Service.setError(jsonObject, e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    Service.setError(jsonObject, e.getMessage());
                }
            }
        }

        return jsonObject;
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
    public JSONObject getFreeRepetitions(String day, String state){
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

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

            try {
                jsonObject.put("done", true);
                jsonObject.put("results", calculateFreeRepetitions(courses, coursesTeachersAss, bookedRepetitions));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Service.setError(jsonObject, e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    Service.setError(jsonObject, e.getMessage());
                }
            }
        }

        return jsonObject;
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

    public JSONObject getBookedHistoryRepetitions(String state, String account){
        Connection conn = null;
        PreparedStatement st = null;
        JSONArray dbBookedHistoryRepetitions = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT Day, StartTime, Title, Surname, Name, r.IDTeacher AS IDTeacher, r.IDCourse AS IDCourse FROM courses c JOIN (repetitions r JOIN teachers t ON r.IDTeacher = t.IDTeacher ) ON r.IDCourse = c.IDCourse WHERE State = ? and Account = ?;";
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
                    innerObj.put("title", rs.getString("Title"));
                    innerObj.put("surname", rs.getString("Surname"));
                    innerObj.put("name", rs.getString("Name"));
                    innerObj.put("idCourse", rs.getInt("IDCourse"));
                    innerObj.put("idTeacher", rs.getInt("IDTeacher"));
                    dbBookedHistoryRepetitions.put(innerObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                jsonObject.put("done", true);
                jsonObject.put("results", dbBookedHistoryRepetitions);
            } catch (JSONException e){
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Service.setError(jsonObject, e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    Service.setError(jsonObject, e.getMessage());
                }
            }
        }

        return jsonObject;
    }

    public JSONObject changeState(String newState, String day, String startTime, int idCourse, int idTeacher, String account) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            st = conn.prepareStatement("UPDATE repetitions SET state = ? WHERE Day = ? AND StartTime = ? AND IDCourse = ? AND IDTeacher = ? AND Account = ?");
            st.setString(1, newState);
            st.setString(2, day);
            st.setString(3, startTime);
            st.setInt(4, idCourse);
            st.setInt(5, idTeacher);
            st.setString(6, account);

            st.executeUpdate();

            try {
                jsonObject.put("done", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Service.setError(jsonObject, e.getMessage());
        } finally {
            if(conn != null && st != null) {
                try {
                    st.close();
                    conn.close();
                } catch (SQLException e) {
                    Service.setError(jsonObject, e.getMessage());
                }
            }
        }

        return jsonObject;
    }
}