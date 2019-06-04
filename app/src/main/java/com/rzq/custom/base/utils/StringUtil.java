package com.rzq.custom.base.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Scanner;

public class StringUtil {
    public static boolean isnotempty(String spmc) {
        if (spmc == null)
            return false;
        else if (spmc.equals(""))
            return false;
        else if (spmc.equals("null"))
            return false;
        return true;
    }

    public static Object jsonHas(JSONObject spJson, String spmc) {
        try {
            if (spJson.has(spmc)) {
                return spJson.get(spmc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Spanned getColorString(String start, String stop, String colorstr, String color) {
        return Html.fromHtml(String.format(start + "<font color=\"" + color + "\">" + colorstr + "</font>" + stop));
    }

    public static String initstr0(String spmc) {
        if (spmc == null)
            return "0";
        else if (spmc.equals(""))
            return "0";
        else if (spmc.equals("null"))
            return "0";
        return spmc;
    }

    public static String initstr(String spmc) {
        if (spmc == null)
            return "";
        else if (spmc.equals(""))
            return "";
        else if (spmc.equals("null"))
            return "";
        return spmc;
    }

    public static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '+' || ch == '-') {
                if (i != 0) {
                    return false;
                }
            } else if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    /**
     * 邮箱格式验证
     *
     * @param phone
     * @return
     */
    public static boolean CheckEml(String phone) {
        if (!StringUtil.isnotempty(phone)) {
            return false;
        }
        Scanner sc = new Scanner(phone);
        String email = sc.next();
        if (email.contains("@") && email.contains(".")) {
            if (email.lastIndexOf(".") > email.lastIndexOf("@")) {
                return true;
            }
        }
        return false;
    }

    public static String forMat1(String coinCount_num) {
        try {
            return new DecimalFormat("0.0").format(coinCount_num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String forMat2(String coinCount_num) {
        try {
            return new DecimalFormat("0.00").format(coinCount_num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String forMat2(BigDecimal coinCount_num) {
        try {
            return new DecimalFormat("0.00").format(coinCount_num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String forMat3(String coinCount_num) {
        try {
            return new DecimalFormat("0.000").format(coinCount_num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String forMat4(String coinCount_num) {
        try {
            return new DecimalFormat("0.0000").format(coinCount_num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String urlEnCode(String msg) {
        try {
            return Uri.encode(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String initPrice(String s) {
        try {
            if (!isnotempty(s))
                return "";
            return new BigDecimal(s).setScale(2, RoundingMode.HALF_UP) + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String initJPrice(String s, String s1) {
        try {
            if (!isnotempty(s))
                return "";

            return new BigDecimal(s).subtract(new BigDecimal(s1)).setScale(2, RoundingMode.HALF_UP) + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String initDPrice(String amount, String price) {
        try {
            if (!isnotempty(amount))
                return "";

            return new BigDecimal(amount).divide(new BigDecimal(price), 10, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_DOWN) + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String initCPrice(String p, String p1) {
        try {
            if (!isnotempty(p))
                return "";

            return new BigDecimal(p).multiply(new BigDecimal(p1))
                    .setScale(2, RoundingMode.HALF_DOWN) + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }
}
