package com.wangdamme.job.servelet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangdamme.job.db.MySQLConnection;
import com.wangdamme.job.entity.Item;
import com.wangdamme.job.entity.ResultResponse;
import com.wangdamme.job.external.GitHubClient;

import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("user_id");
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = request.getSession(false);

        if(session==null)
        {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return ;
        }


        // postman 会对/search发送GET请求， http://localhost:8081/jupiter/search?lat=37.38&lon=-122.08

        double lat  =Double.parseDouble(request.getParameter("lat"));
        double lon  =Double.parseDouble(request.getParameter("lon"));

        // 让search severlet 知道
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoteritedItemIds = connection.getFavoriteItemIds(userId);
        connection.close();



        GitHubClient client = new GitHubClient(); // 封装 怎么 去github make request
        //String itemString = client.search(lat,lon,null);
        List<Item> items = client.search(lat,lon,null);

        for (Item item: items)
        {
            item.setFavorite(favoteritedItemIds.contains(item.getId()));
        }


        response.setContentType("application/json");
        response.getWriter().println(mapper.writeValueAsString(items));  // print 和 write 没什么区别  print 就是call了write


    }
}
