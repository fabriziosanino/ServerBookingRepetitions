package com.example.provaappandroid;

import DAO.DAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletGetBookedHistoryRepetitions", value = "/servlet-get-booked-history-repetitions")
public class ServletGetBookedHistoryRepetitions extends HttpServlet {
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
        String state = request.getParameter("state");
        String account = request.getParameter("account");
        String token = request.getParameter("sessionToken");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        HttpSession session = request.getSession(false);

        if(session != null && session.getId().equals(token)) {
            if (state == null || account == null) {
                Service.setError(jsonObject, "state or account not found");
            } else {
                JSONObject json = dao.getBookedHistoryRepetitions(state, account);

                try {
                    if (json.getBoolean("done")) {
                        JSONArray dbBookedHistoryRepetitions = json.getJSONArray("results");
                        jsonObject.put("done", true);
                        jsonObject.put("results", dbBookedHistoryRepetitions);
                    } else {
                        Service.setError(jsonObject, json.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Service.setError(jsonObject, "no session");
        }

        out.print(jsonObject);
        out.flush();
        out.close();
    }
}
