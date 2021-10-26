package com.example.provaappandroid;

import DAO.DAO;
import org.json.*;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        DAO.registerDriver();
        message = "Hello World!";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nome = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        int result = DAO.queryDB("INSERT INTO utente VALUES(NULL, '" + nome + "', '" + email + "', '" + password + "')");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        JSONObject jsonObject = new JSONObject();

        if(result == -1){
            try {
                jsonObject.put("done", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject.put("done", true);
                jsonObject.put("name", nome);
                jsonObject.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        out.print(jsonObject);
        out.flush();
    }

    public void destroy() {
    }
}