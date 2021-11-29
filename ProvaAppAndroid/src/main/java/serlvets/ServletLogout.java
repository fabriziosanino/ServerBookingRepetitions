package serlvets;

import DAO.DAO;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletLogout", value = "/servlet-logout")
public class ServletLogout extends HttpServlet {
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
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();

    session.invalidate();

    response.setHeader("Access-Control-Allow-Origin", "*");

    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    JSONObject jsonObject = new JSONObject();

    try {
      jsonObject.put("done", true);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    out.println(jsonObject);
    out.flush();

    out.close();
  }
}
