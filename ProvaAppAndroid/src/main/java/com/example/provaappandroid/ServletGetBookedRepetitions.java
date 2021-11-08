package com.example.provaappandroid;

import DAO.DAO;
import DAO.BookedRepetitions;
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

@WebServlet(name = "ServletGetBookedRepetitions", value = "/servlet-get-booked-repetitions")
public class ServletGetBookedRepetitions extends HttpServlet {
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
    JSONArray dbBookedRepetitions = dao.getBookedRepetitions("Active");

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    JSONObject jsonObject = new JSONObject();

    if(dbBookedRepetitions != null) {
      try {
        jsonObject.put("done", true);
        jsonObject.put("results", dbBookedRepetitions);
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
  }
}
