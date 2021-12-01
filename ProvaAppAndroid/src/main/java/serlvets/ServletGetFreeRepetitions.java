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
  private DAO dao;

  public void init(ServletConfig config) {
    dao = new DAO(config.getServletContext());
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
      String[] states = new String[2];
      states[0]="Active";
      states[1]="Done";

      JSONObject json = dao.getFreeRepetitions(day, states, account);

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
