package com.rzq.custom.socketClient;


import android.os.CountDownTimer;
import android.util.Log;


import com.rzq.custom.cahtscreen.UserInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkExecutor {

    private final static String TAG = "NetworkExecutor";
    Lock connectlock = new ReentrantLock();
    private boolean isconnecting = false;
    private BlockingQueue<RequestPacket> mRequestQueue = new LinkedBlockingDeque<>();

    private List<MessageHandlerWrap> mMessageHandlerWrapList = new ArrayList<>();

    private List<ConnectHandlerWrap> mConnectHandlerList = new ArrayList<>();

    private Client mClient;

    private Protocols mProtocols;

    private int mPingInterval;

    private Thread sendThread;

    private Thread receiveThread;

    private Thread pingThread;

    private boolean isForceStop = false;

    private static Schedulers mSchedulers = new Schedulers();


    public NetworkExecutor(Client mClient, Protocols mProtocols, int mPingInterval) {
        this.mClient = mClient;
        this.mProtocols = mProtocols;
        this.mPingInterval = mPingInterval;
    }

    public void connect() {
        Log.d("DEBUG", "cONNECT isconnecting=" + isconnecting);
        if (mClient.isDisconnected() && !isconnecting) {
            isForceStop = false;
            Log.d("DEBUG", "创建连接线程");
            new Thread(connectRunnable, "ConnectThread").start();
        }
    }

    public void disconnect() {
        if (mClient.isConnected()) {
            isForceStop = true;
            mClient.disconnect();
            connectHandler(3);
        }
    }


    public void send(byte[] data) {

        RequestPacket packet = new RequestPacket(data);
        try {
            getRequestQueue().put(packet);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!mClient.isConnected()) {
            mSchedulers.run(new Runnable() {
                @Override
                public void run() {
                    tryConnect();
                }
            }, 1000);
        }

    }

    private void disconnectForError() {
        if (!isForceStop) {
            mSchedulers.run(new Runnable() {
                @Override
                public void run() {
                    tryConnect();
                }
            }, 1000 * 10);
        }
        disconnect();
    }


    private void tryConnect() {

        final CountDownTimer countDownTimer = new CountDownTimer(60000, 3000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.w(TAG, "====重连====");
                if (!mClient.isConnected()) {
                    connect();
                } else {
                    this.cancel();
                }
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    public List<MessageHandlerWrap> getResponseHandlerList() {
        return mMessageHandlerWrapList;
    }

    public BlockingQueue<RequestPacket> getRequestQueue() {
        return mRequestQueue;
    }

    public List<ConnectHandlerWrap> getConnectHandlerList() {
        return mConnectHandlerList;
    }

    public void connectHandler(int status) {
        for (int i = getConnectHandlerList().size() - 1; i >= 0; i--) {
            ConnectHandlerWrap connectHandler = getConnectHandlerList().get(i);
            if (!connectHandler.isDisposed()) {

                switch (status) {
                    case 1:
                        mSchedulers.connectSuccess(connectHandler);
                        break;
                    case 2:
                        mSchedulers.connectFail(connectHandler);
                        break;
                    case 3:
                        mSchedulers.disconnect(connectHandler);
                        break;
                }
            }
        }
    }

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("DEBUG", "进入连接线程");
            try {
                connectlock.lock();
                Log.d("DEBUG", "进入连接线程 A");
                isconnecting = true;
                if (!mClient.isConnected()) {
                    Log.d("DEBUG", "开始连接");
                    mClient.connect();
                    Log.d("DEBUG", "连接完毕");
                    if (mClient.isConnected()) {
                        receiveThread = new Thread(receiveRunnable, "ReceiveThread");
                        receiveThread.start();
                        sendThread = new Thread(sendRunnable, "SendThread");
                        sendThread.start();
                        pingThread = new Thread(pingRunnable, "PingThread");

                        if (mPingInterval > 0) {
                            pingThread.start();
                        }
                        connectHandler(1);
                    } else {
                        connectHandler(2);
                    }
                }
            } catch (Exception e) {

            } finally {
                Log.d("DEBUG", "释放连接");
                isconnecting = false;
                connectlock.unlock();
            }

        }
    };
    /**
     * 接收数据线程
     */
    private Runnable receiveRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, Thread.currentThread().getName() + ": =======开启接收数据线程========");

            Socket socket = mClient.getSocket();
            while (mClient.isConnected()) {
                if (!socket.isClosed() && !socket.isInputShutdown()) {
                    try {
                        InputStream in = socket.getInputStream();
                        receiveData(in);
                    } catch (IOException e) {
                        Log.w(TAG, "=======断开连接========");
                        disconnectForError();
                    }
                }
            }
            Log.i(TAG, Thread.currentThread().getName() + ": =======退出接收数据线程========");
            sendThread.interrupt();//退出发送线程
            pingThread.interrupt();//退出心跳线程
        }
    };

    private void receiveData(InputStream in) throws IOException {

        byte[] buf = mProtocols.unpack(in);
        if (buf != null) {
            Log.d(TAG, Thread.currentThread().getName() + ": =======接收数据转发给" + mMessageHandlerWrapList.size() + "个监听器=======:数据大小:" + buf.length);
            for (int i = mMessageHandlerWrapList.size() - 1; i >= 0; i--) {
                MessageHandlerWrap messageHandlerWrap = mMessageHandlerWrapList.get(i);
                if (!messageHandlerWrap.isDisposed()) {
                    mSchedulers.messageReceive(messageHandlerWrap, buf);
                }
            }
        }
    }

    /**
     * 发送数据线程
     */
    private Runnable sendRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, Thread.currentThread().getName() + ": =======开启发送数据线程========");
            DataOutputStream dataOutputStream = null;
            try {
                Socket socket = mClient.getSocket();
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (!Thread.currentThread().isInterrupted()) {
                    RequestPacket packet = mRequestQueue.take();
                    byte[] data = mProtocols.pack(packet.getData());
                    dataOutputStream.write(data);
                    dataOutputStream.flush();
                    Log.d(TAG, Thread.currentThread().getName() + ": =======发送数据========:数据大小:" + data.length);

                }


            } catch (IOException e) {
                e.printStackTrace();
                disconnectForError();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.flush();
                        //            dataOutputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            Log.i(TAG, Thread.currentThread().getName() + ": =======退出发送数据线程========");
        }
    };
    /**
     * 发送心跳包线程
     */
    private Runnable pingRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, Thread.currentThread().getName() + ": =======开启心跳线程========");

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String pingdata = "Ping 15823138883";
                    RequestPacket packet = new RequestPacket(pingdata.getBytes());
                    Log.d("DEBUG", Thread.currentThread().getName() + ": =======发送心跳包========:数据:" + packet.GetString());
                    getRequestQueue().put(packet);
                    Thread.sleep(mPingInterval * 1000);
                }
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }
            Log.i(TAG, Thread.currentThread().getName() + ": =======退出心跳线程========");
        }
    };

}
