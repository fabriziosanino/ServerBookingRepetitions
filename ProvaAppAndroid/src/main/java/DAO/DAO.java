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
                int res = st.executeUpdate();
                jsonObject.put("inserted", res);
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
        User u = new User();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT * FROM users WHERE Account = ? AND Pwd = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, account);
            st.setString(2, pwd);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                u = new User(rs.getString("Account"), rs.getString("Pwd"), rs.getString("Role"), rs.getString("Name"), rs.getString("Surname"));
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

    public JSONObject checkSession(String account) {
        Connection conn = null;
        PreparedStatement st = null;
        User u = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT * FROM users WHERE Account = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, account);
            ResultSet rs = st.executeQuery();

            rs.next();
            u = new User(rs.getString("Account"), rs.getString("Pwd"), rs.getString("Role"), rs.getString("Name"), rs.getString("Surname"));

            try {
                jsonObject.put("done", true);
                jsonObject.put("result", u);
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

    public JSONObject getCourses() {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();
        JSONArray courses = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.prepareStatement("SELECT IDCourse, Title FROM courses;");
            ResultSet rs = st.executeQuery();

            courses = new JSONArray();

            while (rs.next()) {
                try {
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("IDCourse", rs.getInt("IDCourse"));
                    innerObj.put("Title", rs.getString("Title"));
                    courses.put(innerObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                jsonObject.put("done", true);
                jsonObject.put("results", courses);
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

    public JSONObject deleteCourse(int idCourse) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.prepareStatement("DELETE FROM courses WHERE IDCourse = ?;");
            st.setInt(1, idCourse);
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

    public JSONObject getTeachers() {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();
        JSONArray teachers = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.prepareStatement("SELECT IDTeacher, Mail, Surname, Name FROM teachers;");
            ResultSet rs = st.executeQuery();

            teachers = new JSONArray();

            while (rs.next()) {
                try {
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("IDTeacher", rs.getInt("IDTeacher"));
                    innerObj.put("Mail", rs.getString("Mail"));
                    innerObj.put("Surname", rs.getString("Surname"));
                    innerObj.put("Name", rs.getString("Name"));
                    teachers.put(innerObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                jsonObject.put("done", true);
                jsonObject.put("results", teachers);
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

    public JSONObject deleteTeacher(int idTeacher) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.prepareStatement("DELETE FROM teachers WHERE IDTeacher = ?;");
            st.setInt(1, idTeacher);
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

    public JSONObject getTeaches() {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();
        JSONArray teaches = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.prepareStatement("SELECT teachers.IDTeacher, courses.IDCourse, courses.Title, teachers.Name, teachers.Surname FROM teachers JOIN (teaches JOIN courses ON teaches.IDCourse = courses.IDCourse) ON teachers.IDTeacher = teaches.IDTeacher;");
            ResultSet rs = st.executeQuery();

            teaches = new JSONArray();

            while (rs.next()) {
                try {
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("Title", rs.getString("Title"));
                    innerObj.put("Name", rs.getString("Name"));
                    innerObj.put("Surname", rs.getString("Surname"));
                    innerObj.put("IDTeacher", rs.getString("IDTeacher"));
                    innerObj.put("IDCourse", rs.getString("IDCourse"));
                    teaches.put(innerObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                jsonObject.put("done", true);
                jsonObject.put("results", teaches);
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

    public JSONObject deleteTeach(int idTeacher, int idCourse) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.prepareStatement("DELETE FROM teaches WHERE IDTeacher = ? AND IDCourse = ?;");
            st.setInt(1, idTeacher);
            st.setInt(2, idCourse);
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

    public JSONObject insertCourse(String title) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "INSERT INTO courses VALUES(NULL, ?);";
            st = conn.prepareStatement(query);
            st.setString(1, title);

            try {
                jsonObject.put("done", true);
                int res = st.executeUpdate();
                jsonObject.put("inserted", res);
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

    public JSONObject insertTeacher(String mail, String surname, String name) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "INSERT INTO teachers VALUES(NULL, ?, ?, ?);";
            st = conn.prepareStatement(query);
            st.setString(1, mail);
            st.setString(2, surname);
            st.setString(3, name);

            try {
                jsonObject.put("done", true);
                int res = st.executeUpdate();
                jsonObject.put("inserted", res);
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

    public JSONObject insertTeach(int idTeacher, int idCourse) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "INSERT INTO teaches VALUES(?, ?);";
            st = conn.prepareStatement(query);
            st.setInt(1, idTeacher);
            st.setInt(2, idCourse);

            try {
                jsonObject.put("done", true);
                int res = st.executeUpdate();
                jsonObject.put("inserted", res);
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

    public JSONObject getFreeRepetitions(String day, String state, String account){
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            st = conn.prepareStatement("SELECT IDCourse, Title FROM courses;");
            ResultSet courses = st.executeQuery();
            st = conn.prepareStatement("SELECT t.IDCourse, c.Title, t.IDTeacher, tc.Surname, tc.Name FROM teaches as t natural join teachers as tc natural join courses as c;");
            ResultSet coursesTeachersAss = st.executeQuery();
            st = conn.prepareStatement("SELECT Account, Day, StartTime, IDCourse, IDTeacher FROM repetitions WHERE State = ? and Day = ?;");
            st.setString(1, state);
            st.setString(2, day);
            ResultSet bookedRepetitions = st.executeQuery();

            try {
                jsonObject.put("done", true);
                jsonObject.put("results", calculateFreeRepetitions(courses, coursesTeachersAss, bookedRepetitions, account));
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

    //TODO: controllare le chiusure di connessione nelle finally

    /*
    * This method returns the ALL the free courses and teachers available in a certain hour and day (for all the week)
    * */
    private JSONArray calculateFreeRepetitions(ResultSet courses, ResultSet coursesTeachersAss, ResultSet bookedRepetitions, String account){
        JSONArray dbFreeRepetitions = new JSONArray();

        for (int j=15; j<19; j++){ //for every hour
            try{
                boolean hasAnotherRepetitionBooked = false;
                if(!account.equals("")){
                    while(bookedRepetitions.next() && !hasAnotherRepetitionBooked) {
                        String [] hour = bookedRepetitions.getString("startTime").split(":");
                        if (bookedRepetitions.getString("Account").equals(account) && String.valueOf(j).equals(hour[0]))
                            hasAnotherRepetitionBooked = true;
                    }
                    bookedRepetitions.beforeFirst();
                }

                if(!hasAnotherRepetitionBooked){
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
                        if(teachersList.length() > 0){
                            coursesList.put(courseItem);
                        }
                    }
                    JSONObject innerObj = new JSONObject();
                    String onHour =  String.valueOf(j).concat(":00");
                    innerObj.put("startTime", onHour);
                    innerObj.put("coursesList", coursesList);
                    dbFreeRepetitions.put(innerObj);
                    courses.beforeFirst();
                }

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

            String query = "SELECT IDRepetition, Day, StartTime, Title, Surname, Name, r.IDTeacher AS IDTeacher, r.IDCourse AS IDCourse FROM courses c JOIN (repetitions r JOIN teachers t ON r.IDTeacher = t.IDTeacher ) ON r.IDCourse = c.IDCourse WHERE State = ? and Account = ?;";
            st = conn.prepareStatement(query);
            st.setString(1, state);
            st.setString(2, account);
            ResultSet rs = st.executeQuery();

            dbBookedHistoryRepetitions = new JSONArray();
            while (rs.next()) {
                try {
                    JSONObject innerObj = new JSONObject();
                    innerObj.put("IDRepetition", rs.getString("IDRepetition"));
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

    public JSONObject bookRepetition(String account, String IDTeacher, String IDCourse, String day, String startTime, String state) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            String query = "INSERT INTO repetitions VALUES(NULL, ?, ?, ?, ?, ?, ?);";
            st = conn.prepareStatement(query);
            st.setString(1, day);
            st.setString(2, startTime);
            st.setInt(3, Integer.parseInt(IDCourse));
            st.setInt(4, Integer.parseInt(IDTeacher));
            st.setString(5, account);
            st.setString(6, state);

            try {
                jsonObject.put("done", true);
                int res = st.executeUpdate();
                
                if(res > 0)
                    jsonObject.put("results", "Repetition Booked Succesfully.");
                else
                    jsonObject.put("results", "Failed to book the repetition. Try Again.");
            }catch (SQLException | JSONException e) {
                Service.setError(jsonObject, e.getMessage());
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

    public JSONObject changeState(String IDRepetition, String state) {
        Connection conn = null;
        PreparedStatement st = null;
        JSONObject jsonObject = new JSONObject();

        try {
            conn = DriverManager.getConnection(url, user, password);

            st = conn.prepareStatement("UPDATE repetitions SET state = ? WHERE IDRepetition=?;");
            st.setString(1, state);
            st.setString(2, IDRepetition);
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