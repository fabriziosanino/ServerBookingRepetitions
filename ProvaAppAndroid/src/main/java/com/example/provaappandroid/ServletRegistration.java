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
        String nome = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        HttpSession session = request.getSession();

        password = Service.encryptMD5(password);

        int result = dao.queryDB("INSERT INTO utente VALUES(NULL, '" + nome + "', '" + email + "', '" + password + "')");

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
                jsonObject.put("name", nome);
                jsonObject.put("email", email);

                session.setAttribute("name", nome);
                session.setAttribute("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        out.print(jsonObject);
        out.flush();
    }

    public void destroy() {
    }
}