package com.wangdamme.job.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangdamme.job.entity.Item;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.text.html.parser.Entity;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class GitHubClient {

    private static final String URL_TEMPLATE ="https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";

    private static final String DEFAULT_KEYWORD ="developer"; // developer

    public List<Item> search (double lat, double lon, String keyword)
    {
        // sanity check
        if (keyword==null)
        {
            keyword=DEFAULT_KEYWORD;
        }

        // da wang --> da20%wang or da+wang

        // encode 需要 handle异常
        try {
            keyword = URLEncoder.encode(keyword,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(URL_TEMPLATE,keyword, lat, lon); // 填充 通配符%s


        CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler<List<Item>> responseHandler = response -> {   //相当于callback repsonse回来 得到response 做自己rocessing
            // 先做 下面的try catch 再 回来 做第70行 httpclient.execute 然后 得到了45行response
            if(response.getStatusLine().getStatusCode()!=200)
            {
                return Collections.emptyList();// return empty List
            }
            HttpEntity entity = response.getEntity();
            if (entity==null)
            {
                return  Collections.emptyList();
            }

            ObjectMapper mapper= new ObjectMapper();
            InputStream inputStream = entity.getContent();
            //  Item[] items = mapper.readValue(inputStream, Item[].class); // mapper 把response 拿回来是array 没有[] treat成一个item

            List<Item> items = Arrays.asList(mapper.readValue(entity.getContent(), Item[].class));
            extractKeywords(items);

            System.out.println(items);
            return items;
            //return Arrays.asList(items);

        }; // lambda function
        /*
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                return null;
            }
        }; // lambda function

         */

        try {
            return httpClient.execute(new HttpGet(url),responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(); // 2 options Colleciotns.emptyList();
        // Colleciotns.emptyList(); 返回全局的 并且 final  immutable 不支持往里写  -- 安全 外面往里
    }


    private void extractKeywords(List<Item> items)
    {
        MonkeyLearnClient monkeyLearnClient = new MonkeyLearnClient();
        /* //就是 for循环
        // 就是改成pipline map后做各种操作
        List<String> descriptions = items.stream()
                .map(Item::getDescription)
                .collect(Collectors.toList());  // java8 的 string 可以做map  stream 给下游提供structure .collect转换成list

         */

        List<String> descriptions = new ArrayList<>();
        for (Item item : items)
        {
            descriptions.add(item.getDescription());
        }


        List<Set<String>> keywordList = monkeyLearnClient.extract(descriptions);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setKeywords(keywordList.get(i));
        }



    }
}
