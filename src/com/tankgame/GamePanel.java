package com.tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int DELAY = 25;

    private Timer timer;
    private Tank playerTank;
    private Tank enemyTank;
    private User user;
    private NetworkManager networkManager;
    private boolean isHost;

    public GamePanel(User user) {
        this.user = user;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new TankKeyListener());

        // Показываем диалог выбора режима
        if (JOptionPane.showConfirmDialog(this,
                "Хотите быть хостом? (Нет - подключиться к существующему хосту)",
                "Выбор режима",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            isHost = true;

            // Показываем сообщение о ожидании подключения
            JOptionPane.showMessageDialog(this,
                    "Ожидание подключения второго игрока...\n" +
                            "Запустите второй экземпляр игры и подключитесь как клиент",
                    "Ожидание игрока",
                    JOptionPane.INFORMATION_MESSAGE);

            networkManager = new NetworkManager(true, null, this);
        } else {
            isHost = false;
            String hostAddress = JOptionPane.showInputDialog(this,
                    "Введите IP адрес хоста (например, localhost)");
            if (hostAddress == null || hostAddress.trim().isEmpty()) {
                hostAddress = "localhost";
            }
            networkManager = new NetworkManager(false, hostAddress.trim(), this);
        }

        // Создаем танки
        if (isHost) {
            playerTank = new Tank(100, 300, Color.BLUE, 0);
            enemyTank = new Tank(700, 300, Color.RED, 1);
        } else {
            playerTank = new Tank(700, 300, Color.RED, 1);
            enemyTank = new Tank(100, 300, Color.BLUE, 0);
        }

        // Таймер запускаем только после подключения
        if (!isHost) {
            startGame();
        }
    }

    public void startGame() {
        timer = new Timer(DELAY, this);
        timer.start();
        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Рисуем танки
        playerTank.draw(g);
        enemyTank.draw(g);

        // Рисуем снаряды
        for (Bullet bullet : playerTank.getBullets()) {
            bullet.draw(g);
        }

        for (Bullet bullet : enemyTank.getBullets()) {
            bullet.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isHost && !networkManager.isConnected()) {
            return; // Не обновляем игру, пока не подключится клиент
        }

        playerTank.update();
        enemyTank.update();

        checkBulletCollisions();
        networkManager.sendTankData(playerTank);
        repaint();
    }

    private void checkBulletCollisions() {
        // Проверяем попадания снарядов игрока в противника
        for (Bullet bullet : playerTank.getBullets()) {
            if (enemyTank.checkCollision(bullet)) {
                endGame(true); // Игрок победил
                return;
            }
        }

        // Проверяем попадания снарядов противника в игрока
        for (Bullet bullet : enemyTank.getBullets()) {
            if (playerTank.checkCollision(bullet)) {
                endGame(false); // Игрок проиграл
                return;
            }
        }
    }

    private void endGame(boolean playerWon) {
        timer.stop();

        UserManager userManager = new UserManager();
        userManager.updateUserStats(user.getUsername(), playerWon);

        String message = playerWon ? "Вы победили!" : "Вы проиграли!";
        JOptionPane.showMessageDialog(this, message + "\nПобеды: " + user.getWins() + "\nПоражения: " + user.getLosses());

        System.exit(0);
    }

    public void updateEnemyTank(Tank tank) {
        if (tank != null && enemyTank != null) {
            enemyTank.setX(tank.getX());
            enemyTank.setY(tank.getY());
            enemyTank.setAngle(tank.getAngle());
            enemyTank.setBullets(tank.getBullets());
        }
    }

    private class TankKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    playerTank.setMovingForward(true);
                    break;
                case KeyEvent.VK_S:
                    playerTank.setMovingBackward(true);
                    break;
                case KeyEvent.VK_A:
                    playerTank.setRotatingLeft(true);
                    break;
                case KeyEvent.VK_D:
                    playerTank.setRotatingRight(true);
                    break;
                case KeyEvent.VK_SPACE:
                    playerTank.fire();
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    playerTank.setMovingForward(false);
                    break;
                case KeyEvent.VK_S:
                    playerTank.setMovingBackward(false);
                    break;
                case KeyEvent.VK_A:
                    playerTank.setRotatingLeft(false);
                    break;
                case KeyEvent.VK_D:
                    playerTank.setRotatingRight(false);
                    break;
            }
        }
    }
}