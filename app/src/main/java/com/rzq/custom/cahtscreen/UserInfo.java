package com.rzq.custom.cahtscreen;

import com.rzq.custom.MyApp;
import com.rzq.custom.ShareUtil;

public class UserInfo {
    private static int userId = 2;
    private static String username = "客服";

    public static int getUserId() {
        try {
            userId = Integer.parseInt(ShareUtil.GetPerfenceInfo(MyApp.instance.getApplicationContext(), "userid"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }

    public static void setUserId(int userId) {
        ShareUtil.SetPerfenceInfo(MyApp.instance.getApplicationContext(), "userid", userId + "");
        UserInfo.userId = userId;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserInfo.username = username;
    }
}
