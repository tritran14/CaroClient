package com.example.caroclient.model;

public class Room {
    int port;
    int numberPlayers;

    public Room(int port, int numberPlayers) {
        this.port = port;
        this.numberPlayers = numberPlayers;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }
}
