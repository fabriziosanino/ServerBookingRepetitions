package com.example.provaappandroid;

import DAO.*;
import org.json.JSONException;
import org.json.JSONObject;

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
        HttpSession session = request.getSession();

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if(!session.isNew()) {
            if(session.getAttribute("email") != null) {
                String email = session.getAttribute("email").toString();
                User dbUser = dao.getUser("SELECT * FROM utente WHERE Email = '" + email +"'");

                try {
                    jsonObject.put("done", true);
                    jsonObject.put("email", email);
                    jsonObject.put("name", dbUser.getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    jsonObject.put("done", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                jsonObject.put("done", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        out.print(jsonObject);
        out.flush();
    }
}
