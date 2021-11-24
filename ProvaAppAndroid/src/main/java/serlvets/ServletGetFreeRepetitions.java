package serlvets;

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
  protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
    response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    super.doOptions(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String day = request.getParameter("day");
    String account = "";
    if(request.getParameter("account") != null)
      account = request.getParameter("account");

    response.setHeader("Access-Control-Allow-Origin", "*");

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    JSONObject jsonObject = new JSONObject();

    if (day == null) {
      Service.setError(jsonObject, "day not found");
    } else {
      JSONObject json = dao.getFreeRepetitions(day, "Active", account);

      try {
        if(json.getBoolean("done")) {
          jsonObject.put("done", true);
          jsonObject.put("results", json.getJSONArray("results"));
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
