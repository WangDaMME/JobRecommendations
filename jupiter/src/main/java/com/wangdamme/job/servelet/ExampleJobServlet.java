package com.wangdamme.job.servelet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangdamme.job.entity.ExampleCoordinates;
import com.wangdamme.job.entity.ExampleJob;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ExampleJobServlet", urlPatterns = "/example_job")
public class ExampleJobServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    // do get 访问这个网页时候， 在这个网页response 写
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        response.setContentType("application/json");
        //jackson 用objectmapper
        ObjectMapper mapper = new ObjectMapper();
        ExampleCoordinates coordinates = new ExampleCoordinates(37.485130, -122.148316);
        ExampleJob job = new ExampleJob("Software Engineer", 123456, "Aug 1, 2020", false, coordinates); //传进preset coordinates

        // Mapper.writeValueAsString 把 job object中的 fields写成字符串 字典key-value pair形式
        response.getWriter().print(mapper.writeValueAsString(job));
    }
}
