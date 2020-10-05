package com.wangdamme.job.db;

public class MySQLDBUtil {
    private static final String INSTANCE = "laiproject-instance.cbflz8m8jkgq.us-east-2.rds.amazonaws.com";
    private static final String PORT_NUM = "3306";
    public static final String DB_NAME = "laiproject";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "asdASD123"; // 把 passcode 放到 production 不能通过passcode access 是 ssh key acces
    public static final String URL = "jdbc:mysql://"
            + INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
            + "?user=" + USERNAME + "&password=" + PASSWORD
            + "&autoReconnect=true&serverTimezone=UTC";
}
