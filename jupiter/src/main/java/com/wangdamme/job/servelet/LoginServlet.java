package com.wangdamme.job.servelet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangdamme.job.db.MySQLConnection;
import com.wangdamme.job.entity.LoginRequestBody;
import com.wangdamme.job.entity.LoginResponseBody;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        LoginRequestBody body = mapper.readValue(request.getReader(), LoginRequestBody.class);
        MySQLConnection connection = new MySQLConnection();
        LoginResponseBody loginResponseBody;


        // Session 会话层
        if (connection.verifyLogin(body.userId, body.password))
        {
            HttpSession session = request.getSession(); // 创建session
            session.setAttribute("user_id",body.userId); // session 不知道db里的userid
            loginResponseBody = new LoginResponseBody("OK", body.userId, connection.getFullName(body.userId));
        }
        else {
            loginResponseBody = new LoginResponseBody("Login failed, user id and passcode do not exist.", null, null);
            response.setStatus(401);
        }
        connection.close();
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), loginResponseBody);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = request.getSession(false);  // 不要创建新的session
        LoginResponseBody loginResponseBody;

        if(session !=null)
        {
            MySQLConnection connection = new MySQLConnection();
            String userId = session.getAttribute("user_id").toString();
            loginResponseBody = new LoginResponseBody("OK", userId, connection.getFullName(userId));
            connection.close();
        }
        else
        {
            loginResponseBody = new LoginResponseBody("Invalid Session.", null, null);
            response.setStatus(403);
        }
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), loginResponseBody);

    }

}
