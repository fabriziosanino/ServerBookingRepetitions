package servlets;

import DAO.DAO;
import org.json.*;
import service.Service;

import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "ServletRegistration", value = "/servlet-registration")
public class ServletRegistration extends HttpServlet {
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String role = request.getParameter("role");


        response.setHeader("Access-Control-Allow-Origin", "*");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonObject = new JSONObject();

        if(account == null || password == null || name == null || surname == null){
            Service.setError(jsonObject, "account, password, name or surname not found");
        } else {
            JSONObject json = dao.insertClientUser(account, Service.encryptMD5(password), name, surname, role);

            try {
                if(json.getBoolean("done")) {
                    if(json.getInt("inserted") == -1)
                        Service.setError(jsonObject, "registration failed");
                    else {
                        HttpSession session = request.getSession();

                        jsonObject.put("done", true);
                        jsonObject.put("account", account);
                        jsonObject.put("pwd", password);
                        jsonObject.put("role", role);
                        jsonObject.put("name", name);
                        jsonObject.put("surname", surname);
                        jsonObject.put("token", session.getId());

                        session.setAttribute("account", account);
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

    public void destroy() {
    }
}