package com.rzq.custom.socketClient;
import java.nio.charset.Charset;

public class LiteSocketClient {
    public static final class Builder {
        private Client mClient;
        private Protocols mProtocols;

        private int mPingInterval = 0;
        public Builder client(Client client){
            this.mClient = client;
            return this;
        }

        public Builder protocols(Protocols protocols){
            this.mProtocols = protocols;
            return this;
        }


        public Builder pingInterval(int pingInterval){
            this.mPingInterval = pingInterval;
            return this;
        }

        public LiteSocketClient build() {
            return new LiteSocketClient(this.mClient,this.mProtocols,this.mPingInterval);
        }
    }
    private NetworkExecutor mNetworkExecutor;
    public LiteSocketClient(Client client, Protocols protocols, int pingInterval) {
        this.mNetworkExecutor = new NetworkExecutor(client,protocols,pingInterval);
    }

    public Disposable onMessage(MessageHandler handler){
        MessageHandlerWrap packet = new MessageHandlerWrap(handler);
        packet.addTo(mNetworkExecutor.getResponseHandlerList());
        return packet;
    }

    public Disposable onConnect(ConnectHandler connectHandler){
        ConnectHandlerWrap connectHandlerWrap = new ConnectHandlerWrap(connectHandler);
        connectHandlerWrap.addTo(mNetworkExecutor.getConnectHandlerList());
        return connectHandlerWrap;
    }
    public void send(String msg){
        send(msg.getBytes(Charset.defaultCharset()));
    }
    public void send(byte[] data){

        mNetworkExecutor.send(data);

    }
    public void connect(){
        mNetworkExecutor.connect();
    }

    public void disconnect(){
        mNetworkExecutor.disconnect();
    }
}
