package com.example.caroclient.model;

public class Type {
    int type;
    String name;

    public Type(int type) {
        this.type = type;
        switch (type){
            case 0:
                this.name="O";
                break;
            case 1:
                this.name="X";
                break;
            default:
                this.name="blank";
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
