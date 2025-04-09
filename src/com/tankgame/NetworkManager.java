package com.tankgame;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;

public class NetworkManager {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GamePanel gamePanel;
    private boolean isHost;

    public NetworkManager(boolean isHost, String hostAddress, GamePanel gamePanel) {
        this.isHost = isHost;
        this.gamePanel = gamePanel;

        try {
            if (isHost) {
                System.out.println("Запуск сервера на IP: " + InetAddress.getLocalHost().getHostAddress());
                new Thread(() -> {
                    try {
                        ServerSocket serverSocket = new ServerSocket(5555);
                        System.out.println("Сервер слушает на: " +
                                serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());

                        // Блокируемся до подключения клиента
                        socket = serverSocket.accept();
                        System.out.println("Клиент подключен");

                        // Инициализируем потоки
                        out = new ObjectOutputStream(socket.getOutputStream());
                        in = new ObjectInputStream(socket.getInputStream());

                        // Уведомляем GamePanel о подключении клиента
                        SwingUtilities.invokeLater(() -> {
                            gamePanel.startGame();
                            JOptionPane.showMessageDialog(null,
                                    "Игрок подключен! Игра начинается!",
                                    "Подключено",
                                    JOptionPane.INFORMATION_MESSAGE);
                        });

                        // Запускаем поток для получения данных
                        new Thread(this::receiveData).start();

                    } catch (IOException e) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null,
                                    "Ошибка сервера: " + e.getMessage(),
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        });
                    }
                }).start();
            } else {
                // Код для клиента без изменений
                System.out.println("Попытка подключения к " + hostAddress + "...");
                socket = new Socket();
                socket.connect(new InetSocketAddress(hostAddress, 5555), 5000);
                System.out.println("Подключено к серверу");

                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                new Thread(this::receiveData).start();
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Ошибка подключения: " + e.getMessage() +
                                "\nУбедитесь, что сервер запущен и адрес правильный.",
                        "Ошибка подключения",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }

    // Остальные методы без изменений
    public void sendTankData(Tank tank) {
        try {
            if (out != null) {
                // Создаем копию танка без ссылок на несериализуемые объекты
                Tank copy = new Tank(tank.getX(), tank.getY(), null, tank.getPlayerId());
                copy.setAngle(tank.getAngle());
                copy.setBullets(new ArrayList<>(tank.getBullets()));

                out.writeObject(copy);
                out.flush();
                out.reset();
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Ошибка отправки данных: " + e.getMessage(),
                        "Ошибка сети",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }

    private void receiveData() {
        try {
            while (true) {
                Tank receivedTank = (Tank) in.readObject();
                SwingUtilities.invokeLater(() -> {
                    if (gamePanel != null) {
                        gamePanel.updateEnemyTank(receivedTank);
                    }
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Соединение разорвано: " + e.getMessage(),
                        "Ошибка сети",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            });
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    private String getLocalIPs() {
        try {
            StringBuilder ips = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        ips.append(addr.getHostAddress()).append(", ");
                    }
                }
            }
            return ips.toString();
        } catch (SocketException e) {
            return "не удалось определить IP";
        }
    }
}