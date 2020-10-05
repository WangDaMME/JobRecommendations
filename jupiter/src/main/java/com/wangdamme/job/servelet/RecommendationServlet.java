package com.wangdamme.job.servelet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangdamme.job.Recommendation.Recommendation;
import com.wangdamme.job.entity.Item;
import com.wangdamme.job.entity.ResultResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RecommendationServlet",urlPatterns = {"/recommendation"})
public class RecommendationServlet extends HttpServlet {
    /*
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       // response.getWriter().write("This is RecommendationServlet");

        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }

        String userId=request.getParameter("user_id");
        double lat =Double.parseDouble(request.getParameter("lat"));
        double lon =Double.parseDouble(request.getParameter("lon"));

        Recommendation recommendation = new Recommendation();
        List<Item> items = recommendation.recommendItems(userId,lat,lon);
        response.getWriter().print(mapper.writeValueAsString(items));

        // 返回的favorite 是 false 新推荐的 是 没点过赞的  而且没有重复的
    }
}
