package com.wangdamme.job.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangdamme.job.entity.ExtractRequestBody;
import com.wangdamme.job.entity.ExtractResponseItem;
import com.wangdamme.job.entity.Extraction;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MonkeyLearnClient {  // post

    private static final String EXTRACT_URL = "https://api.monkeylearn.com/v3/extractors/ex_YCya9nrn/extract/";
    private static final String AUTH_TOKEN = "1dd309e395282f081cc705f887115a5bec427490";


    // 给githubclient 中 extractkeywords 方法调用
    public List<Set<String>> extract(List<String> articles)  // keyword 是个set // 输入为单独一个article  // 每一个article 对应一个 set of keyword, (不需要order)

    {
        // Step 0. Preparation
        ObjectMapper mapper = new ObjectMapper();  //JSON mapping： 因为要把ExtractRequestBody 写成json形式 发送给monkeylearn mapper.writeValueAsString()

        CloseableHttpClient httpclient = HttpClients.createDefault();

        // step 1. 准备mkl extract Post api 请求, setHeader.(Auth)
        HttpPost request = new HttpPost(EXTRACT_URL);                 // 按monkeylearn要求发送post方法 (ghclient 是new HTTPGet(方法))
        request.setHeader("Content-type", "application/json");
        request.setHeader("Authorization", "Token " + AUTH_TOKEN);    //把 Auth 添加 到 request header中
        ExtractRequestBody body = new ExtractRequestBody(articles, 3);

        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(body); //将对象object 序列化 成为json
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }

        try {
            request.setEntity(new StringEntity(jsonBody));
        } catch (UnsupportedEncodingException e) {
            return Collections.emptyList();  // 或者 proper handle
        }

        ResponseHandler<List<Set<String>>> responseHandler = response -> // callback 函数
        {
            // 同样 2 cases: Unsuccess && return 结果 is empty
            if (response.getStatusLine().getStatusCode() != 200) {
                return Collections.emptyList();
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return Collections.emptyList();
            }

            // response items 返回 一个数组 [ List<extractions>, List<extractions>, ..... ]
            // gihub 返回数组形式
            ExtractResponseItem[] results = mapper.readValue(entity.getContent(), ExtractResponseItem[].class); // 声明了class apper 把response 拿回来是array 没有[] treat成一个item
            List<Set<String>> keywordList = new ArrayList<>();

            // look thru 所有result 每一个result 拿出 创建的set,预处理
            for (ExtractResponseItem result : results) // 对应每个artciles: job1, job2
            {
                Set<String> keywords = new HashSet<>();
                for (Extraction extraction : result.extractions) { // extraction 一个属性 是 responseItem.List<Extraction> extractions;
                    keywords.add(extraction.parsedValue); // 只想要parsedvalue
                }
                keywordList.add(keywords);
            }
            return keywordList;   //  List<Set<String>> : {job1:(developer, java, google),  job2:(morganstanly, sql, analyst)} 3 keywords
        };

        try {
            return httpclient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();

    }



    public static void main(String[] args) {
        List<String> articles = Arrays.asList("Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.",
                "Former Auburn University football coach Tommy Tuberville defeated ex-US Attorney General Jeff Sessions in Tuesday nights runoff for the Republican nomination for the U.S. Senate. ",
                "The NEOWISE comet has been delighting skygazers around the world this month – with photographers turning their lenses upward and capturing it above landmarks across the Northern Hemisphere."
        );

        MonkeyLearnClient client = new MonkeyLearnClient();
        List<Set<String>> keywordList =client.extract(articles); // 3 个 string
        System.out.println(articles);
        System.out.println(articles.size());
        System.out.println(keywordList);  //  [ {3 words form sentence1}, {3 words form sentence2}, {3 words form sentence3}]

    }

}
