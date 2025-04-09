package com.tankgame;

import java.awt.*;
import java.io.Serializable;

public class Bullet implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int SPEED = 7;
    private static final int SIZE = 5;

    private double x, y;
    private double angle;
    private int playerId;

    public Bullet(double x, double y, double angle, int playerId) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.playerId = playerId;
    }

    public void update() {
        x += Math.cos(angle) * SPEED;
        y += Math.sin(angle) * SPEED;
    }

    public void draw(Graphics g) {
        g.setColor(playerId == 0 ? Color.BLUE : Color.RED);
        g.fillOval((int)x - SIZE/2, (int)y - SIZE/2, SIZE, SIZE);
    }

    // Геттеры
    public double getX() { return x; }
    public double getY() { return y; }
    public int getPlayerId() { return playerId; }
}