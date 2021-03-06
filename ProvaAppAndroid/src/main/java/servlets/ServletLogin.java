package servlets;

import DAO.DAO;
import DAO.User;
import org.json.JSONException;
import org.json.JSONObject;
import service.Service;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ServletLogin", value = "/servlet-login")
public class ServletLogin extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        response.setHeader("Access-Control-Allow-Origin", "*");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if (account == null || password == null) {
            Service.setError(jsonObject, "account or password not found");
        } else {
            JSONObject json = dao.checkLogin(account, Service.encryptMD5(password));

            try {
                if (json.getBoolean("done")) {
                    User dbUser = (User) json.get("user");
                    if (!dbUser.getAccount().equals("") && !dbUser.getName().equals("") && !dbUser.getSurname().equals("") && !dbUser.getPwd().equals("") && !dbUser.getRole().equals("")) {

                        HttpSession session = request.getSession();
                        try {
                            jsonObject.put("done", true);
                            jsonObject.put("account", dbUser.getAccount());
                            jsonObject.put("name", dbUser.getName());
                            jsonObject.put("surname", dbUser.getSurname());
                            jsonObject.put("role", dbUser.getRole());
                            jsonObject.put("token", session.getId());

                            session.setAttribute("account", dbUser.getAccount());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Service.setError(jsonObject, "user not found");
                    }
                } else {
                    Service.setError(jsonObject, json.getString("error"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        out.print(jsonObject);
        out.flush();

        out.close();
    }
}
