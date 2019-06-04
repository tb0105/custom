package com.rzq.custom.socketClient;

public class RequestPacket {

    private byte[] data;

    public RequestPacket(byte[] data) {
        this.data = data;
    }

    byte[] getData(){
        return this.data;
    }

    String GetString(){return  new String(data);}

}
