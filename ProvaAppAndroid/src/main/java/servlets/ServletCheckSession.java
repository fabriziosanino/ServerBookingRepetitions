package servlets;

import DAO.*;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletCheckSession", value = "/servlet-check-session")
public class ServletCheckSession extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        response.addHeader("Access-Control-Allow-Origin", "*");

        if (type != null) {
            if (type.equals("check_connection_server")) {
                try {
                    jsonObject.put("done", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (type.equals("check_connection_db")) {
                if (dao.checkConnession()) {
                    try {
                        jsonObject.put("done", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Service.setError(jsonObject, "no_db_connection");
                }
            }
        } else {
            Service.setError(jsonObject, "type not found");
        }

        out.print(jsonObject);
        out.flush();

        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        response.addHeader("Access-Control-Allow-Origin", "*");

        if (session != null) {
            String sessionTokenPassed = request.getParameter("sessionToken");
            String sessionToken = session.getId();

            if (sessionTokenPassed != null && sessionTokenPassed.equals(sessionToken)) {
                if (session.getAttribute("account") != null) {
                    String account = session.getAttribute("account").toString();
                    JSONObject json = dao.checkSession(account);

                    try {
                        if (json.getBoolean("done")) {
                            User u = (User) json.get("result");
                            jsonObject.put("done", true);
                            jsonObject.put("account", u.getAccount());
                            jsonObject.put("name", u.getName());
                            jsonObject.put("surname", u.getSurname());
                            jsonObject.put("role", u.getRole());
                            jsonObject.put("token", sessionToken);
                        } else {
                            Service.setError(jsonObject, json.getString("error"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Service.setError(jsonObject, "no session");
                }
            } else {
                Service.setError(jsonObject, "no session");
            }
        } else {
            // no session
            Service.setError(jsonObject, "no session");
        }

        out.print(jsonObject);
        out.flush();

        out.close();
    }
}
