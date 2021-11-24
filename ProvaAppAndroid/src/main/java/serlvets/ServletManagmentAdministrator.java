package serlvets;

import DAO.DAO;
import DAO.User;
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
    private String url;
    private String user;
    private String password;
    private DAO dao;

    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();
        url = context.getInitParameter("DB-URL");
        user = context.getInitParameter("user");
        password = context.getInitParameter("password");
        dao = new DAO(url, user, password);
        dao.registerDriver();
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
        String token = request.getParameter("sessionToken");

        response.setHeader("Access-Control-Allow-Origin", "*");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        HttpSession session = request.getSession(false);

        if(session != null && session.getId().equals(token)) {
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
            }
        } else {
            Service.setError(jsonObject, "no session");
        }

        out.print(jsonObject);
        out.flush();
        out.close();
    }
}
