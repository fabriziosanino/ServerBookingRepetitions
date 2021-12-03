package servlets;

import DAO.DAO;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletBookARepetition", value = "/servlet-book-a-repetition")
public class ServletBookARepetition extends HttpServlet {
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
        String startTime = request.getParameter("startTime");
        String IDCourse = request.getParameter("IDCourse");
        String IDTeacher = request.getParameter("IDTeacher");
        String account = request.getParameter("account");

        response.setHeader("Access-Control-Allow-Origin", "*");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        HttpSession session = request.getSession(false);

        if (session != null) {
            if (day == null || startTime == null || IDCourse == null || IDTeacher == null || account == null) {
                Service.setError(jsonObject, "day, startTime, IDCourse, IDTeacher or account not found");
            } else {
                JSONObject json = dao.bookRepetition(account, IDTeacher, IDCourse, day, startTime, "Active");

                try {
                    if (json.getBoolean("done")) {
                        jsonObject.put("done", true);
                        jsonObject.put("results", json.getString("results"));
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
