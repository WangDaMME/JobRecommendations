package com.wangdamme.job.db;

//需要Conection , drivamanager , sqlexception 来 connect msql using the jdbc drivemanager
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

// 去 initialize table
public class MySQLTableCreation {

    public static void main(String[] args) {

        // 4 steps for seeding 创建 种子数据
        try {

            // Step 1 Connect to MySQL.
            System.out.println("Connecting to " + MySQLDBUtil.URL);

            Class.forName("com.mysql.cj.jdbc.Driver").newInstance(); // 用reflection 方法 initiate JDBC instance (singleton方法创建的) -- 就是这么用的
            Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);

            if (conn == null) {
                return;
            }

            // Step 2 Drop tables in case they exist. (就是delete 目的 是个clean start)
            Statement statement =conn.createStatement();
            String sql = "DROP TABLE IF EXISTS keywords"; // sql语言
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS history";
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS items";
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS users";
            statement.executeUpdate(sql);

            // Step 3 Create new tables   // item table column列 varchar 255 宽度 可变的，intialize 一个size 之后是可变的
            sql = "CREATE TABLE items ("
                    + "item_id VARCHAR(255) NOT NULL,"  // 不可以填null
                    + "name VARCHAR(255),"
                    + "address VARCHAR(255),"
                    + "image_url VARCHAR(255),"
                    + "url VARCHAR(255),"
                    + "PRIMARY KEY (item_id)"
                    + ")";
            statement.executeUpdate(sql);  //创建的database 让 database执行



            sql = "CREATE TABLE users ("
                    + "user_id VARCHAR(255) NOT NULL,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "first_name VARCHAR(255),"
                    + "last_name VARCHAR(255),"
                    + "PRIMARY KEY (user_id)"
                    + ")";
            statement.executeUpdate(sql);


            sql = "CREATE TABLE keywords ("
                    + "item_id VARCHAR(255) NOT NULL,"
                    + "keyword VARCHAR(255) NOT NULL,"
                    + "PRIMARY KEY (item_id, keyword),"
                    + "FOREIGN KEY (item_id) REFERENCES items(item_id)"
                    + ")";
            statement.executeUpdate(sql);  // "+" : concatenation  CREATE TABLE keywords (item_id VARCHAR(255) NOT NULL,keyword VARCHAR(255) NOT NULL,PRIMARY KEY (item_id, keyword),FOREIGN KEY (item_id) REFERENCES items(item_id))


            // 表示favorite
            sql = "CREATE TABLE history ("
                    + "user_id VARCHAR(255) NOT NULL,"
                    + "item_id VARCHAR(255) NOT NULL,"
                    + "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "PRIMARY KEY (user_id, item_id),"
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id),"
                    + "FOREIGN KEY (item_id) REFERENCES items(item_id)"
                    + ")";
            statement.executeUpdate(sql);

            // Step 4: insert fake user 1111/3229c1097c00d497a0fd282d586be050



            sql = "INSERT INTO users VALUES('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
            statement.executeUpdate(sql);

            conn.close();
            System.out.println("Import done successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
