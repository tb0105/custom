package com.rzq.custom.cahtscreen;

public class UserInfo {
    private static int userId=2;
    private static String username="客服2";

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        UserInfo.userId = userId;
    }

    public static String  getUsername() {
        return username;
    }

    public static void setUsername(String  username) {
        UserInfo.username = username;
    }
}
