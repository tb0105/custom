package com.rzq.custom;

import android.content.Context;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class Defines {
    public static final int COIN_CASH = 0;
    public static final int COIN_PAC = 1; // 量子货币
    public static final int COIN_BTC = 2;//比特币
    public static final int COIN_LTC = 3;//莱特币
    public static final int COIN_ETH = 4; //以太坊
    public static final int COIN_XRP = 5;
    public static final int COIN_BCH = 6;
    public static final int COIN_EOS = 7;
    public static final int COIN_BSV = 8;
    public static final int COIN_DASH = 9;
    public static final int COIN_ADA = 10;
    public static final int COIN_XLM = 11;
    public static final int COIN_XMR = 12;
    public static final int COIN_TETHER = 13;
    public static final int COIN_TRON = 14;
    public static final int COIN_P = 15;
    public static final int COIN_C = 16;
    public static final int COIN_MAX = 15;

    public static final int MARKET_BINANCE = 1;
    public static final int MARKET_OKEX = 2;
    public static final int MARKET_BITFINEX = 3;
    public static final int MARKET_HUOBI = 4;

    public static String serverjson = null;
    public static String serverip = null;
    public static int port1, port2;
    public static final String UpdateUrl = "http://43.227.221.119:3133/update.xml";

    public static String getWsdlURL(Context context) {

        if (true)
//        if(false)//27.159.82.32
        {
            serverip = "27.159.82.32";
            port1 = 3134;
            port2 = 8555;
        } else {
            if (serverip == null) {
                if (serverjson == null) {
                    serverjson = ShareUtil.GetPerfenceInfo(context, "serverjson");
                }

                if (serverjson == null || serverjson.length() == 0) {
                    serverip = "43.227.221.119";
                    port1 = 3133;
                    port2 = 8555;
                } else {
                    List<Map<String, String>> srvlist = JSON.parseObject(serverjson, new TypeReference<List<Map<String, String>>>() {
                    });

                    Random rand = new Random();

                    int position = rand.nextInt(srvlist.size());
                    serverip = srvlist.get(position).get("ip");
                    port1 = Integer.parseInt(srvlist.get(position).get("port1"));
                    port2 = Integer.parseInt(srvlist.get(position).get("port2"));
                }
            }
        }


        return "http://" + serverip + ":" + port1 ;


    }

    public static String getWsdlNameSpace() {
        return "http://cloudauction.org/";
    }

    public static String getAuctionServer(Context context) {

        if (true) {
            serverip = "27.159.82.32";
            port1 = 3134;
            port2 = 8555;
        } else {
            if (serverip == null) {
                if (serverjson == null) {
                    serverjson = ShareUtil.GetPerfenceInfo(context, "serverjson");
                }

                if (serverjson == null || serverjson.length() == 0) {
                    serverip = "43.227.221.119";
                    port1 = 3133;
                    port2 = 8555;
                } else {
                    List<Map<String, String>> srvlist = JSON.parseObject(serverjson, new TypeReference<List<Map<String, String>>>() {
                    });

                    Random rand = new Random();

                    int position = rand.nextInt(srvlist.size());
                    serverip = srvlist.get(position).get("ip");
                    port1 = Integer.parseInt(srvlist.get(position).get("port1"));
                    port2 = Integer.parseInt(srvlist.get(position).get("port2"));
                }

            }
        }

        return serverip;
    }

    public static int getPort2() {
        if (port2 == 0)
            return 8555;
        else
            return port2;
    }

    public static String getMarketName(int c) {
        switch (c) {
            case MARKET_BINANCE:
                return "BINANCE";
            case MARKET_OKEX:
                return "OKEX";
            case MARKET_BITFINEX:
                return "Bitfinex";
            case MARKET_HUOBI:
                return "HuobiPro";
            default:
                return "";
        }
    }

//    public static int getMarketIcon(int c) {
//        switch (c) {
//            case MARKET_BINANCE:
//                return R.drawable.binance;
//            case MARKET_OKEX:
//                return R.drawable.okex;
//            case MARKET_BITFINEX:
//                return R.drawable.bitfinex;
//            case MARKET_HUOBI:
//                return R.drawable.huobi;
//            default:
//                return 0;
//        }
//
//    }


}
