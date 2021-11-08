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
        String account = null;
        String password = null;
        String name = null;
        String surname = null;

        BufferedReader bufferedReader = request.getReader();
        String postParameters =  bufferedReader.readLine();

        JSONObject json = null;
        try {
            json = new JSONObject(postParameters);
            account = json.getString("account");
            password = json.getString("password");
            name = json.getString("name");
            surname = json.getString("surname");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpSession session = request.getSession();

        // TODO: parametri query da rivedere, usare injection sql NON hardcoded
        int result = dao.queryDB("INSERT INTO users VALUES('"+account+"', '"+Service.encryptMD5(password)+"', '"+name+"', '"+surname+"', 'Client')");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        JSONObject jsonObject = new JSONObject();

        if(result == -1){
            try {
                jsonObject.put("done", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject.put("done", true);
                jsonObject.put("account", account);
                jsonObject.put("pwd", password);
                jsonObject.put("role", "Client");
                jsonObject.put("name", name);
                jsonObject.put("surname", surname);

                session.setAttribute("account", account);
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