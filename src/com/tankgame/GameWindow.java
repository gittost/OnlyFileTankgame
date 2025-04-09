package com.tankgame;

import javax.swing.*;

public class GameWindow extends JFrame {
    public GameWindow(User user) {
        setTitle("Tank War - Игра");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel(user);
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
    }
}