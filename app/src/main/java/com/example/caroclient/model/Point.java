package com.example.caroclient.model;

import java.util.Objects;

public class Point {
    public int x,y;

    public static Point INVALID=new Point(-1,-1);

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    public int caroDistance(Point point){
        if(this.x==point.x) return Math.abs(this.y-point.y)+1;
        else if(this.y==point.y) return Math.abs(this.x-point.x)+1;
        return Math.abs(point.x-this.x)+1;
    }

    public Point copy(){
        return new Point(this.x,this.y);
    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }

}
