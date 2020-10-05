package com.wangdamme.job.servelet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangdamme.job.db.MySQLConnection;
import com.wangdamme.job.entity.HistoryRequestBody;
import com.wangdamme.job.entity.Item;
import com.wangdamme.job.entity.ResultResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

// 用来handle favorite request

@WebServlet(name = "HistoryServlet",urlPatterns =  {"/history"})
public class HistoryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }

        // 前端 点击按钮 ajax 发来请求 ./history, user_id= xx favorite=xx
        // jackson 转成相应instance
        HistoryRequestBody body = mapper.readValue(request.getReader(), HistoryRequestBody.class);

        MySQLConnection connection = new MySQLConnection();

        // post 调用 把favorite id 和 favorite item里写进 items 这张表
        connection.setFavoriteItems(body.userId, body.favorite);

        connection.close();

        ResultResponse resultResponse = new ResultResponse("SUCCESS");
        mapper.writeValue(response.getWriter(), resultResponse);  //把response写进到 一个writer 里

    }

    // 调用mysqlconneciton 中getFavoriteItems， "SELECT item_id FROM history WHERE user_id = ?";
    // 从history 表 找 该user_id 喜欢的items
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }

        String userId = request.getParameter("user_id");

        MySQLConnection connection = new MySQLConnection();
        Set<Item> items = connection.getFavoriteItems(userId);
        connection.close();
        mapper.writeValue(response.getWriter(), items);
    }

    // 删除一个favorite
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //super.doDelete(req, resp);
        //response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();


        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }


        HistoryRequestBody body = mapper.readValue(request.getReader(), HistoryRequestBody.class); // jackson 的mapping
        MySQLConnection connection = new MySQLConnection();
        connection.unsetFavoriteItems(body.userId, body.favorite.getId());
        connection.close();

        ResultResponse resultResponse = new ResultResponse("SUCCESS");
        mapper.writeValue(response.getWriter(), resultResponse);// repsonse里 写东西 是因为前端把 新 涂空
    }
}
