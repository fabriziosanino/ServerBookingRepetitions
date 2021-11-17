package com.example.provaappandroid;

import DAO.DAO;
import org.json.*;
import service.Service;

import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "ServletRegistration", value = "/servlet-registration")
public class ServletRegistration extends HttpServlet {

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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if(account == null || password == null || name == null || surname == null){
            Service.setError(jsonObject, "account, password, name or surname not found");
        } else {
            JSONObject json = dao.insertClientUser(account, Service.encryptMD5(password), name, surname, "Client");

            try {
                if(json.getBoolean("done")) {
                    if(json.getInt("inserted") == -1)
                        Service.setError(jsonObject, "registration failed");
                    else {
                        HttpSession session = request.getSession();

                        jsonObject.put("done", true);
                        jsonObject.put("account", account);
                        jsonObject.put("pwd", password);
                        jsonObject.put("role", "Client");
                        jsonObject.put("name", name);
                        jsonObject.put("surname", surname);
                        jsonObject.put("token", session.getId());

                        session.setAttribute("account", account);
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

    public void destroy() {
    }
}