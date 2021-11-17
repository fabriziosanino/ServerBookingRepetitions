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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if (account == null || password == null) {
            Service.setError(jsonObject, "account or password not found");
        } else {
            JSONObject json = dao.checkLogin(account, Service.encryptMD5(password));

            try {
                if (json.getBoolean("done")) {
                    User dbUser = (User) json.get("user");
                    if (!dbUser.getAccount().equals("") && !dbUser.getName().equals("") && !dbUser.getSurname().equals("") && !dbUser.getPwd().equals("") && !dbUser.getRole().equals("")) {

                        HttpSession session = request.getSession();
                        try {
                            jsonObject.put("done", true);
                            jsonObject.put("account", dbUser.getAccount());
                            jsonObject.put("name", dbUser.getName());
                            jsonObject.put("surname", dbUser.getSurname());
                            jsonObject.put("token", session.getId());

                            System.out.println("LOGIN" + session.getId());

                            session.setAttribute("account", dbUser.getAccount());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Service.setError(jsonObject, "user not found");
                    }
                } else {
                    Service.setError(jsonObject, json.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        out.print(jsonObject);
        out.flush();

        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        session.invalidate();

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("done", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        out.println(jsonObject);
        out.flush();

        out.close();
    }
}
