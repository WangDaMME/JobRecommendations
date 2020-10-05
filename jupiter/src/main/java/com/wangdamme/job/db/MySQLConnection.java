package com.wangdamme.job.db;

import com.wangdamme.job.entity.Item;
import com.wangdamme.job.entity.ResultResponse;

import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


//用来连接数据库
public class MySQLConnection {

    private Connection conn;

    public MySQLConnection() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();  // 用reflection 方法 initiate JDBC instance (singleton方法创建的) -- 就是这么用的
            conn = DriverManager.getConnection(MySQLDBUtil.URL);  // 连接到远端aws mysql 数据库

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {

        if (conn != null)  // conn 不为空， 连接着呢
        {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // 把 Item 存到 items（5） 表里  && 把 items 中的keywords 存到 Keywords(2) 表里
    public void saveItem(Item item)
    {
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }

        String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?)";  //?：通配符,  items 中 5 个col(items_id, name, address, image_url,url)
        try {
            // prepared stament vs createstatement 主要区别是 这里 prestament 可以sql预处理，并且处理通配符

            PreparedStatement statement = conn.prepareStatement(sql);

            // 1-1 对应上 statement.setString(//paramIndex,  //String);

            statement.setString(1, item.getId());
            statement.setString(2, item.getTitle());  // name
            statement.setString(3, item.getLocation());
            statement.setString(4, item.getCompanyLogo());
            statement.setString(5, item.getUrl());
            statement.executeUpdate();

            sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";  // items_id, keywords,
            statement = conn.prepareStatement(sql);
            statement.setString(1, item.getId());
            for (String keyword : item.getKeywords()) {
                statement.setString(2, keyword);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    //    对于 不同的users， 存到 history(2)表 (ie. 即 favorite的item)
    public void setFavoriteItems(String userId, Item item) {
        // good practice 先 sanity check
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }

        // step1. 先 call saveItems() 把 items 存到 items 表 && keywords表
        saveItem(item);
        // step2. 对于 不同的users， 存到 history(2)表 (ie. 即 favorite的item)
        String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, item.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unsetFavoriteItems(String userId, String itemId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
        //   删除不同 相应用户 不喜欢的 id
        String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, itemId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Set<Item> getFavoriteItems(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return new HashSet<>();
        }
        Set<Item> favoriteItems = new HashSet<>();
        Set<String> favoriteItemIds = getFavoriteItemIds(userId);

        String sql = "SELECT * FROM items WHERE item_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            for (String itemId : favoriteItemIds) {
                statement.setString(1, itemId);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    favoriteItems.add(new Item.Builder()
                            .id(rs.getString("item_id"))
                            .title(rs.getString("name"))
                            .location(rs.getString("address"))
                            .companyLogo(rs.getString("image_url"))
                            .url(rs.getString("url"))
                            .keywords(getKeywords(itemId))
                            .favorite(true)
                            .build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }

    // 为getFavoriteItems 服务
    // 从items 表 拿到 user_id 的 item_id
    public Set<String> getFavoriteItemIds(String userId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return new HashSet<>();
            // return Collections.emptySet();// global 全局的
        }

        Set<String> favoriteItems = new HashSet<>();

        try {
            String sql = "SELECT item_id FROM history WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                favoriteItems.add(itemId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return favoriteItems;
    }

    // 为getFavoriteItems 服务
    // 从keywords 表 拿到 item_id的keyword
    public Set<String> getKeywords(String itemId) {
        if (conn == null) {
            System.err.println("DB connection failed");
            return null;
            //            return new HashSet<>();
        }
        Set<String> keywords = new HashSet<>();
        String sql = "SELECT keyword from keywords WHERE item_id = ? ";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, itemId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String keyword = rs.getString("keyword");
                keywords.add(keyword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keywords;
    }

    //Authentication
    // 支持Auth: 从users表中 根据user_id拿 firstname lastname
    public String getFullName(String usedId)
    {
        if(conn==null)
        {
            System.err.println("DB connection failed");
            return "";
        }

        String name="";
        String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";

        try{
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1,usedId);
            ResultSet rs =statement.executeQuery();

            if(rs.next())
            {
                name = rs.getString("first_name")+ " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return name;
    }


    public boolean verifyLogin(String userId, String password){
        if(conn==null)
        {
            System.err.println("DB connection failed");
            return false;
        }
        String sql =  "SELECT user_id FROM users WHERE user_id = ? AND password = ?";

        try{
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // 支持Auth, Register
    // 填一个users 的表

    public boolean addUser(String userId, String password, String firstname, String lastname)
    {
        if(conn==null)
        {
            System.err.println("DB connection failed");
            return false;
        }

        String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";

        try {
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, userId);
        statement.setString(2, password);
        statement.setString(3, firstname);
        statement.setString(4, lastname);

        return statement.executeUpdate() == 1;  // 看会改多少行row
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
