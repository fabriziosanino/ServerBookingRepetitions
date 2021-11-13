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
import java.util.ArrayList;

@WebServlet(name = "ServletGetFreeRepetitions", value = "/servlet-get-free-repetitions")
public class ServletGetFreeRepetitions extends HttpServlet {
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

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    JSONObject jsonObject = new JSONObject();

    if (day == null) {
      Service.setError(jsonObject, "day not found");
    } else {
      JSONArray dbFreeRepetitions = dao.getFreeRepetitions(day, "Active");

      if (dbFreeRepetitions != null) {
        try {
          jsonObject.put("done", true);
          jsonObject.put("results", dbFreeRepetitions);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } else {
        Service.setError(jsonObject, "free repetitions are empty");
      }

      out.print(jsonObject);
      out.flush();
      out.close();
    }
  }
}
