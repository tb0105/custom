package com.rzq.custom.socketClient;

import java.util.List;

public  class ConnectHandlerWrap implements ConnectHandler,Disposable{

    private boolean isDisposed = false;

    private List list;

    private ConnectHandler connectHandler;

    public ConnectHandlerWrap(ConnectHandler connectHandler) {
        this.connectHandler = connectHandler;
    }

    public void addTo(List list){
        this.list = list;
        this.list.add(this);
    }

    @Override
    public void dispose() {
        if(!isDisposed()&&this.list!=null){
            this.isDisposed = true;
            this.list.remove(this);
            this.list = null;
        }
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }

    @Override
    public void connectSuccess() {
        connectHandler.connectSuccess();
    }

    @Override
    public void connectFail() {
        connectHandler.connectFail();
    }

    @Override
    public void disconnect() {
        connectHandler.disconnect();
    }
}
