package com.example.caroclient.model;

public class AIScore {
    int val,x,y;

    public AIScore(int val, int x, int y) {
        this.val = val;
        this.x = x;
        this.y = y;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
