package com.example.provaappandroid;

import DAO.DAO;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
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
    String state = null;

    BufferedReader bufferedReader = request.getReader();
    String postParameters =  bufferedReader.readLine();

    JSONObject json = null;
    try {
      json = new JSONObject(postParameters);
      state = json.getString("state");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    JSONObject jsonObject = new JSONObject();

    HttpSession session = request.getSession();
    String account = (String) session.getAttribute("account");

    if(state == null || account == null) {
      try {
        jsonObject.put("done", true);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } else {
      Pair<JSONArray, String> dbValue = dao.getBookedHistoryRepetitions(state, account);

      if (dbValue.getKey() != null) {
        try {
          jsonObject.put("done", true);
          jsonObject.put("results", dbValue.getKey());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      } else {
        try {
          jsonObject.put("done", false);
          jsonObject.put("error", dbValue.getValue());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }

    out.print(jsonObject);
    out.flush();
    out.close();
  }
}
