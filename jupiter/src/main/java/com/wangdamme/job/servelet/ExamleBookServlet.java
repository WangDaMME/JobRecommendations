package com.wangdamme.job.servelet;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ExamleBookServlet", urlPatterns ={"/example_book"} )  // urlPattern: 让用户知道 去哪里访问 // eg. http://localhost:8081/jupiter/example_book?keyword=harry%20potter&category=children
public class ExamleBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //Postman 会发一个 post请求

        // newJsonObject(request.getRTeader()) 拿到request 别人发来的请求信息
        JSONObject jsonRequest = new JSONObject(IOUtils.toString(request.getReader()));  // 网络上传输的string  转换成json object
        String title = jsonRequest.getString("title");
        String author = jsonRequest.getString("author");
        String date = jsonRequest.getString("date");
        float price = jsonRequest.getFloat("price");
        price+=1;
        String currency = jsonRequest.getString("currency");
        int pages = jsonRequest.getInt("pages");
        String series = jsonRequest.getString("series");
        String language = jsonRequest.getString("language");
        String isbn = jsonRequest.getString("isbn");

        // do databse set
        System.out.println("Title is: " + title);
        System.out.println("Author is: " + author);
        System.out.println("Date is: " + date);
        System.out.println("Price is changed: " + price);
        System.out.println("Currency is: " + currency);
        System.out.println("Pages is: " + pages);
        System.out.println("Series is: " + series);
        System.out.println("Language is: " + language);
        System.out.println("ISBN is: " + isbn);

        JSONObject jsonResponse = new JSONObject();// post 也是个 json
        jsonResponse.put("statusadsdadsa", "ok");
        response.getWriter().print(jsonResponse);



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 当发送请求时，要处理的

        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");

        System.out.println("Keyword is: " + keyword);
        System.out.println("Category is: " + category);


        // 很多 logic 存到response了

        // To Do: database querries
        // Servelet 给的 response  返回 在页面上写response 信息
        //response.getWriter().print("Do Get...");

        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        json.put("title", "---Harry Potter and Ugly Woman"); // field 相当于 map .put
        json.put("author", "JK Rowling");
        json.put("date", "October 1, 1998");
        json.put("price", 11.99);
        json.put("currency", "USD");
        json.put("pages", 309);
        json.put("series", "Harry Potter");
        json.put("language", "en_US");
        json.put("isbn", "0590353403");
        response.getWriter().print(json);  // print json object
    }
}
