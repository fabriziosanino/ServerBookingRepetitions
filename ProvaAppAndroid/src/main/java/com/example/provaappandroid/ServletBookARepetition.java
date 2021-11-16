package com.example.provaappandroid;

import DAO.DAO;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletBookARepetition", value = "/servlet-book-a-repetition")
public class ServletBookARepetition extends HttpServlet {
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
    String day = request.getParameter("day");
    String startTime = request.getParameter("startTime");
    String IDCourse = request.getParameter("IDCourse");
    String IDTeacher = request.getParameter("IDTeacher");
    String account = request.getParameter("account");

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    JSONObject jsonObject = new JSONObject();

    if (day == null || startTime == null || IDCourse == null || IDTeacher == null || account == null) {
      Service.setError(jsonObject, "Missing Parameter. Retry.");
    } else {
      JSONObject json = dao.bookRepetition(account, IDTeacher, IDCourse, day, startTime, "Active");

      try {
        if(json.getBoolean("done")) {
          jsonObject.put("done", true);
          jsonObject.put("results", json.getString("results"));
        } else {
          Service.setError(jsonObject, json.getString("error"));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }

      out.print(jsonObject);
      out.flush();
      out.close();
    }
  }
}
