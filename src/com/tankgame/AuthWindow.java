package com.tankgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserManager userManager;

    public AuthWindow() {
        userManager = new UserManager();
        setTitle("Tank War - Авторизация");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Логин:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Пароль:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Войти");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        panel.add(loginButton);

        JButton registerButton = new JButton("Регистрация");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        panel.add(registerButton);

        add(panel);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля");
            return;
        }

        User user = userManager.loginUser(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Добро пожаловать, " + username + "!\nПобеды: " + user.getWins() + "\nПоражения: " + user.getLosses());
            dispose();
            GameWindow gameWindow = new GameWindow(user);
            gameWindow.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Неверный логин или пароль");
        }
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля");
            return;
        }

        if (userManager.registerUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Регистрация успешна");
        } else {
            JOptionPane.showMessageDialog(this, "Пользователь уже существует");
        }
    }
}