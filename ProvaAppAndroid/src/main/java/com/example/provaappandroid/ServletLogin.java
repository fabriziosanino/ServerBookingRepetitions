package com.example.provaappandroid;

import DAO.DAO;
import DAO.User;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
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

        BufferedReader bufferedReader = request.getReader();
        String postParameters =  bufferedReader.readLine();

        JSONObject json = null;
        String account = null;
        String password = null;
        try {
            json = new JSONObject(postParameters);
            account = json.getString("account");
            password = json.getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        User dbUser = dao.checkLogin(account, Service.encryptMD5(password));

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if(dbUser != null) {
            try {
                jsonObject.put("done", true);
                jsonObject.put("account", dbUser.getAccount());
                jsonObject.put("name", dbUser.getName());
                jsonObject.put("surname", dbUser.getSurname());

                session.setAttribute("account", dbUser.getAccount());
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

        out.print(jsonObject);
        out.flush();

        out.close();
    }
}
