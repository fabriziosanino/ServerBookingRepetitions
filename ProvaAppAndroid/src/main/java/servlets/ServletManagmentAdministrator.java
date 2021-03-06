package servlets;

import DAO.DAO;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletManagmentAdministrator", value = "/servlet-managment-administrator")
public class ServletManagmentAdministrator extends HttpServlet {
    private DAO dao;

    public void init(ServletConfig config) {
        dao = new DAO(config.getServletContext());
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        super.doOptions(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        HttpSession session = request.getSession(false);

        if(session != null) {
            if (type == null) {
                Service.setError(jsonObject, "type not found");
            } else if(type.equals("getCourses")){
                JSONObject json = dao.getCourses();

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
                        jsonObject.put("results", json.getJSONArray("results"));
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("deleteCourse")) {
                int idCourse = Integer.valueOf(request.getParameter("idCourse"));

                JSONObject json = dao.deleteCourse(idCourse);

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("getTeachers")){
                JSONObject json = dao.getTeachers();

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
                        jsonObject.put("results", json.getJSONArray("results"));
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("deleteTeacher")) {
                int idTeacher = Integer.valueOf(request.getParameter("idTeacher"));

                JSONObject json = dao.deleteTeacher(idTeacher);

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("getTeaches")){
                JSONObject json = dao.getTeaches();

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
                        jsonObject.put("results", json.getJSONArray("results"));
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("deleteTeach")) {
                int idTeacher = Integer.valueOf(request.getParameter("idTeacher"));
                int idCourse = Integer.valueOf(request.getParameter("idCourse"));

                JSONObject json = dao.deleteTeach(idTeacher, idCourse);

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("addCourse")) {
                String title = request.getParameter("title");

                JSONObject json = dao.insertCourse(title);

                try {
                    if(json.getBoolean("done")) {
                        if(json.getInt("inserted") == -1)
                            Service.setError(jsonObject, "failed to insert new course");
                        else {
                            jsonObject.put("done", true);
                        }
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("addTeacher")) {
                String mail = request.getParameter("mail");
                String surname = request.getParameter("surname");
                String name = request.getParameter("name");

                JSONObject json = dao.insertTeacher(mail, surname, name);

                try {
                    if(json.getBoolean("done")) {
                        if(json.getInt("inserted") == -1)
                            Service.setError(jsonObject, "failed to insert new teacher");
                        else {
                            jsonObject.put("done", true);
                        }
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(type.equals("addTeach")) {
                int idTeacher = Integer.valueOf(request.getParameter("idTeacher"));
                int idCourse = Integer.valueOf(request.getParameter("idCourse"));

                JSONObject json = dao.insertTeach(idTeacher, idCourse);

                try {
                    if(json.getBoolean("done")) {
                        if(json.getInt("inserted") == -1)
                            Service.setError(jsonObject, "failed to insert new teach");
                        else {
                            jsonObject.put("done", true);
                        }
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Service.setError(jsonObject, "no session");
        }

        out.print(jsonObject);
        out.flush();
        out.close();
    }
}
