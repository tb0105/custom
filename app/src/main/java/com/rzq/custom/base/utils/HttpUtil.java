package com.rzq.custom.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.rzq.custom.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    //超时时间
    private final int READ_TIMEOUT = 20;
    private final int CONNECT_TIMEOUT = 10;
    private final int WRITE_TIMEOUT = 20;
    private final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private final MediaType XML = MediaType.parse("text/xml;charset=utf-8");
    private final MediaType IMAGE = MediaType.parse("image/jpg;charset=utf-8");
    private final MediaType VIDEO = MediaType.parse("application/octet-stream;charset=utf-8");
    private final String[] IMAGEES = new String[]{"jpg", "jpeg", "png", "BMP", "JPG", "JPEG", "PNG", "bmp"};
    private final String[] VIDEOES = new String[]{"mp4", "rm", "rmvb", "avi", "3gp", "mov", "MP4", "RM", "RMVB", "AVI", "3GP", "MOV"};
    private Context mContext;
    private String SESSIONID = "";
    private OkHttpClient mHttpClient = new OkHttpClient();
    private Handler hander = new Handler(Looper.getMainLooper());

    public HttpUtil(Context context) {
        this.mContext = context;
        init();
    }

    protected void addNetworkInterceptor(Interceptor interceptor) {
        //初始化
        mHttpClient = mHttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(interceptor)
                .build();
    }

    /**
     * 主动关闭之前请求的同一个接口,通过tag进行判断是否是同一个，而call中存储的时https或者http接口
     *
     * @param tag
     */
    public void cancel(Object tag) {
        try {
            for (Call call : mHttpClient.dispatcher().queuedCalls()) {
                if (call.request().tag().toString().contains(tag.toString())) {
                    call.cancel();
                }
            }
            for (Call call : mHttpClient.dispatcher().runningCalls()) {
                if (call.request().tag().toString().contains(tag.toString())) {
                    call.cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        //初始化
        mHttpClient = mHttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    protected InputStream httpGetstram(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = mHttpClient.newCall(request);
            Response response = call.execute();
            if (response != null) {
                return response.body().byteStream();
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {//如果超时并未超过指定次数，则重新连接
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_timeout));
                    }
                });
            } else if (e instanceof ConnectException) {
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_out));
                    }
                });
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String httpGet(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = mHttpClient.newCall(request);
            Response response = call.execute();
            if (response != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {//如果超时并未超过指定次数，则重新连接
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_timeout));
                    }
                });
            } else if (e instanceof ConnectException) {
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_out));
                    }
                });
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * httppost请求
     *
     * @param url
     * @param post
     * @return
     */
    public Response httpPost(String url, String post) {
        if (isNetworkAvailable(mContext)) {
            if (url == null || url.equals(""))
                return null;
            try {
                post = post.trim();
                RequestBody body = RequestBody.create(JSON, post);
                Request.Builder builder = new Request.Builder()
                        .url(url)
                        .post(body);
                if (SESSIONID != null && SESSIONID.length() > 0) {
                    builder.addHeader("cookie", SESSIONID);
                }

                Request request = builder.build();
                Response response = mHttpClient.newCall(request).execute();
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof SocketTimeoutException) {//如果超时并未超过指定次数，则重新连接
                    hander.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_timeout));
                        }
                    });

                } else if (e instanceof ConnectException) {
                    hander.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_out));
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        } else {
            Log.d("HttpPost", "请检查网络链接！");
            hander.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show(mContext, "无网络链接");
                }
            });
        }
        return null;
    }

    public Response httpPostFrom(String url, Map<String, String> postmap) {
        try {
            MultipartBody.Builder build = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            for (String key : postmap.keySet()) {
                if (postmap.get(key) != null) {
                    build.addFormDataPart(key, postmap.get(key));
                } else build.addFormDataPart(key, "");
            }
            MultipartBody requestBody = build.build();
            if (isNetworkAvailable(mContext)) {
                Request.Builder builder = new Request.Builder()
                        .url(url)
                        .post(requestBody);
                if (SESSIONID != null) builder.addHeader("cookie", SESSIONID);
                Request request = builder.build();
                Response response = mHttpClient.newCall(request).execute();
                return response;
            } else {
                Log.d("SendinterfiowProperty", "请检查网络链接！");
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, "请检查网络链接！");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SocketTimeoutException) {//如果超时并未超过指定次数，则重新连接
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_timeout));
                    }
                });
            } else if (e instanceof ConnectException) {
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_out));
                    }
                });
            } else {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 下载
     *
     * @param httpUrl
     * @return
     */
    public boolean httpGetstram(String httpUrl, File file) {
        final Request request = new Request.Builder().url(httpUrl).build();
        Response response = null;
        try {
            response = mHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = response.body().byteStream();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int ch = -1;

            while ((ch = is.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, ch);

            }
            fileOutputStream.flush();
            if (fileOutputStream != null) {
                fileOutputStream.close();
                is.close();
            }
            return true;
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {//如果超时并未超过指定次数，则重新连接
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_timeout));
                    }
                });
            } else if (e instanceof ConnectException) {
                hander.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.show(mContext, mContext.getResources().getString(R.string.net_out));
                    }
                });
            } else {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 下载
     *
     * @param httpUrl
     * @return
     */
    public boolean httpGetstram(String httpUrl, File file, OnNextCall<String> onNextCall) {
        final Request request = new Request.Builder().url(httpUrl).build();
        Response response = null;
        long fileSize = 0;
        try {
            response = mHttpClient.newCall(request).execute();
            fileSize = response.body().contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = response.body().byteStream();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int ch = -1;
            long size = 0;
            while ((ch = is.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, ch);
                size += buf.length;
                if (size < fileSize)
                    onNextCall.onNext(new DecimalFormat("0.00").format(size * 100 / fileSize) + "%");
                Log.e("-------", size + "/" + fileSize);
            }
            onNextCall.onNext("over");
            fileOutputStream.flush();
            if (fileOutputStream != null) {
                fileOutputStream.close();
                is.close();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断当前网络类型-1为未知网络0为没有网络连接1网络断开或关闭2为以太网3为WiFi4为2G5为3G6为4G
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            /** 没有任何网络 */
            return 0;
        }
        if (!networkInfo.isConnected()) {
            /** 网络断开或关闭 */
            return 1;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            /** 以太网网络 */
            return 2;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            /** wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接 */
            return 3;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            /** 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭 */
            switch (networkInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    /** 2G网络 */
                    return 4;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    /** 3G网络 */
                    return 5;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    /** 4G网络 */
                    return 6;
            }
        }
        /** 未知网络 */
        return -1;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else { //如果仅仅是用来判断网络连接
//             则可以使用 cm.getActiveNetworkInfo().isAvailable();
            @SuppressLint("MissingPermission") NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getIP(Context context) {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean isnetWork(Context context) {
        switch (checkNetworkAvailable(context)) {
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return true;
            case 4:
                return false;
            default:
                return false;
        }
    }

    // 检测网络
    public static int checkNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return 0;
        } else {
            @SuppressLint("MissingPermission") NetworkInfo datainfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            @SuppressLint("MissingPermission") NetworkInfo wifiinfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (datainfo != null && wifiinfo != null) {
                if (datainfo.isConnected() && wifiinfo.isConnected()) {
                    //移动，wifi已连接
                    return 1;
                } else if (datainfo.isConnected() && !wifiinfo.isConnected()) {
                    //移动已连接
                    return 2;
                } else if (!datainfo.isConnected() && wifiinfo.isConnected()) {
                    //wifi已连接
                    return 3;
                } else if (!datainfo.isConnected() & !wifiinfo.isConnected()) {
                    //移动 WiFi未连接
                    return 4;
                }
            }
        }
        return 0;
    }


}
