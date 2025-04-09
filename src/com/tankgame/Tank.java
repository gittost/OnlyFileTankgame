package com.tankgame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tank implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 40;
    private static final int BARREL_LENGTH = 30;
    private static final double ROTATION_SPEED = 0.05;
    private static final double MOVE_SPEED = 2;

    private double x, y;
    private double angle;
    private Color color;
    private int playerId;

    private boolean movingForward;
    private boolean movingBackward;
    private boolean rotatingLeft;
    private boolean rotatingRight;

    private List<Bullet> bullets;

    public Tank(double x, double y, Color color, int playerId) {
        this.x = x;
        this.y = y;
        this.angle = 0;
        this.color = color;
        this.playerId = playerId;
        this.bullets = new ArrayList<>();
    }

    public void update() {
        // Поворот
        if (rotatingLeft) angle -= ROTATION_SPEED;
        if (rotatingRight) angle += ROTATION_SPEED;

        // Движение
        if (movingForward) {
            x += Math.cos(angle) * MOVE_SPEED;
            y += Math.sin(angle) * MOVE_SPEED;
        }
        if (movingBackward) {
            x -= Math.cos(angle) * MOVE_SPEED;
            y -= Math.sin(angle) * MOVE_SPEED;
        }

        // Обновление снарядов
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            // Удаляем снаряды, вышедшие за пределы экрана
            if (bullet.getX() < 0 || bullet.getX() > 800 ||
                    bullet.getY() < 0 || bullet.getY() > 600) {
                bullets.remove(i);
            }
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Сохраняем текущую трансформацию
        AffineTransform oldTransform = g2d.getTransform();

        // Поворачиваем графику вокруг центра танка
        g2d.translate(x, y);
        g2d.rotate(angle);

        // Рисуем корпус танка
        g2d.setColor(color);
        g2d.fillRect(-SIZE/2, -SIZE/2, SIZE, SIZE);

        // Рисуем пушку
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, -5, BARREL_LENGTH, 10);

        // Восстанавливаем трансформацию
        g2d.setTransform(oldTransform);

        // Рисуем снаряды
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }

    public void fire() {
        double bulletX = x + Math.cos(angle) * (BARREL_LENGTH + 5);
        double bulletY = y + Math.sin(angle) * (BARREL_LENGTH + 5);
        bullets.add(new Bullet(bulletX, bulletY, angle, playerId));
    }

    public boolean checkCollision(Bullet bullet) {
        // Проверяем, что снаряд принадлежит другому игроку
        if (bullet.getPlayerId() == playerId) return false;

        // Простая проверка столкновения
        double dx = x - bullet.getX();
        double dy = y - bullet.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < SIZE / 2;
    }

    // Геттеры и сеттеры
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public List<Bullet> getBullets() { return bullets; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setAngle(double angle) { this.angle = angle; }
    public void setBullets(List<Bullet> bullets) { this.bullets = bullets; }

    public void setMovingForward(boolean movingForward) { this.movingForward = movingForward; }
    public void setMovingBackward(boolean movingBackward) { this.movingBackward = movingBackward; }
    public void setRotatingLeft(boolean rotatingLeft) { this.rotatingLeft = rotatingLeft; }
    public void setRotatingRight(boolean rotatingRight) { this.rotatingRight = rotatingRight; }

    public int getPlayerId() {
        return 0;
    }
}