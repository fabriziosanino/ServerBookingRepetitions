package com.example.provaappandroid;

import DAO.DAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletManageRepetitions", value = "/servlet-manage-repetitions")
public class ServletManageRepetitions extends HttpServlet {
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
        String newState = request.getParameter("newState");
        String day = request.getParameter("day");
        String startTime = request.getParameter("startTime");
        int idTeacher = Integer.valueOf(request.getParameter("idTeacher"));
        int idCourse = Integer.valueOf(request.getParameter("idCourse"));
        String account = request.getParameter("account");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if (newState == null) {
            Service.setError(jsonObject, "state not found");
        } else {
            JSONObject json = dao.changeState(newState, day, startTime, idCourse, idTeacher, account);

            try{
                if(json.getBoolean("done")){
                    jsonObject.put("done", true);
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
}