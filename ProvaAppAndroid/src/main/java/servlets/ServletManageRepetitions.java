package servlets;

import DAO.DAO;
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
        response.setHeader("Access-Control-Allow-Origin", "*");

        String newState = request.getParameter("newState");
        String IDRepetition = request.getParameter("IDRepetition");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        HttpSession session = request.getSession(false);

        if(session != null) {
            if (newState == null) {
                Service.setError(jsonObject, "state not found");
            } else {
                JSONObject json = dao.changeState(IDRepetition, newState);

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
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
