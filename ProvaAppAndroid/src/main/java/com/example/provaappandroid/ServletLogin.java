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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "ServletLogin", value = "/servlet-login")
public class ServletLogin extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User dbUser = dao.getUser("SELECT * FROM utente WHERE Email = '" + email + "'");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if(dbUser != null) {
            if (Service.checkMD5(dbUser.getPassword(), password)) {
                try {
                    session.setAttribute("email", dbUser.getEmail());

                    jsonObject.put("done", true);
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
