package com.example.provaappandroid;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if(type != null) {
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
        //TODO: controllare la sessione prima di fare ogni cosa nella servlet
        HttpSession session = request.getSession(false);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

         if(session != null) {
             String sessionTokenPassed = request.getParameter("sessionToken");
             String sessionToken = session.getId();

             if(sessionTokenPassed != null && sessionTokenPassed.equals(sessionToken)) {
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
