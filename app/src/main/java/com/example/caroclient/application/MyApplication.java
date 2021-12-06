package com.example.caroclient.application;

import android.app.Application;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MyApplication extends Application {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Socket socketLobby;
    private DataInputStream inLobby;
    private DataOutputStream outLobby;

    private String IP="";

    public Socket getSocket() {
        return socket;
    }

    public void closeSocket() throws IOException {
        in.close();
        out.close();
        socket.close();
        Log.e("AAA1", "closeSocket in application");
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.in=new DataInputStream(this.socket.getInputStream());
        this.out=new DataOutputStream(this.socket.getOutputStream());
    }

    public String getIP() {
        return IP;
    }

    public void setNewSocket(String ip, int port) throws IOException {
        this.socket=new Socket(ip,port);
        this.in=new DataInputStream(this.socket.getInputStream());
        this.out=new DataOutputStream(this.socket.getOutputStream());
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public Socket getSocketLobby() {
        return socketLobby;
    }

    public void closeSocketLobby() throws IOException {
        inLobby.close();
        outLobby.close();
        socketLobby.close();
        Log.e("AAA1", "closeSocket Lobby in application");
    }

    public void setSocketLobby(Socket socket) throws IOException {
        this.socketLobby = socket;
        this.inLobby=new DataInputStream(this.socketLobby.getInputStream());
        this.outLobby=new DataOutputStream(this.socketLobby.getOutputStream());
    }

    public void setNewSocketLobby(String ip,int port) throws IOException {
        this.IP=ip;
        this.socketLobby=new Socket(ip,port);
        this.inLobby=new DataInputStream(this.socketLobby.getInputStream());
        this.outLobby=new DataOutputStream(this.socketLobby.getOutputStream());
    }

    public DataInputStream getInLobby() {
        return inLobby;
    }

    public DataOutputStream getOutLobby() {
        return outLobby;
    }

    @Override
    public void onTerminate() {
        try {
            in.close();
            out.close();
            socket.close();

            inLobby.close();
            outLobby.close();
            socketLobby.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onTerminate();
    }
}
