package com.rzq.custom.socketClient;

import android.os.Handler;
import android.os.Looper;


public class Schedulers {



    Handler mResponseHandler = new Handler(Looper.getMainLooper());

    public void messageReceive(final MessageHandler response, final byte[] data) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                response.receive(data);
            }
        };

        mResponseHandler.post(runnable);
    }

    public void connectSuccess(final ConnectHandler connectHandler){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                connectHandler.connectSuccess();
            }
        };

        mResponseHandler.post(runnable);
    }

    public void connectFail(final ConnectHandler connectHandler){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                connectHandler.connectFail();
            }
        };

        mResponseHandler.post(runnable);
    }

    public void disconnect(final ConnectHandler connectHandler){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                connectHandler.disconnect();
            }
        };

        mResponseHandler.post(runnable);
    }

    public void run(Runnable runnable,long delayed){
        mResponseHandler.postDelayed(runnable,delayed);
    }
}
