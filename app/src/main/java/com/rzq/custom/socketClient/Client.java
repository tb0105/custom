package com.rzq.custom.socketClient;

import java.net.Socket;

public interface Client {


    Socket getSocket();

    void connect();

    void disconnect();

    boolean isConnected();

    boolean isDisconnected();

    boolean isConnecting();
}
