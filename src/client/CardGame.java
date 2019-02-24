package client;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CardGame {
    private static final String SERVER_HOST = "localhost";
    // порт
    private static final int SERVER_PORT = 3443;
    // клиентский сокет
    private Socket clientSocket;
    // входящее сообщение
    private Scanner inMessage;
    // исходящее сообщение
    private PrintWriter outMessage;

    private String clientName = "";
    // получаем имя клиента
    public String getClientName() {
        return this.clientName;
    }

    private JTextField jtfName;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;
    private JRadioButton radioButton4;
    private JTextArea textArea;
    private JButton sendButton;
    private JPanel jpanel;
    private JTextField jlNumberOfClients;

    public CardGame(JFrame frame) {

        try {
            // подключаемся к серверу
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setBounds(600, 300, 600, 500);
        ButtonGroup group = new ButtonGroup();
        group.add(radioButton1);
        group.add(radioButton2);
        group.add(radioButton3);
        group.add(radioButton4);

        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioButton4.isSelected()){
                    clientName = jtfName.getText();
                    sendMsg(clientName +" -4 карты");
                }

                if (radioButton3.isSelected()){
                    clientName = jtfName.getText();
                    sendMsg(clientName +" -3 карты");
                }

                if (radioButton2.isSelected()){
                    clientName = jtfName.getText();
                    sendMsg(clientName +" -2 карты");
                }

                if (radioButton1.isSelected()){
                    clientName = jtfName.getText();
                    sendMsg(clientName +" -1 карты");

                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // бесконечный цикл
                    while (true) {
                        // если есть входящее сообщение
                        if (inMessage.hasNext()) {
                            // считываем его
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Количество игроков в игре = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                // выводим сообщение
                                textArea.append(inMes);
                                // добавляем строку перехода
                                textArea.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // здесь проверяем, что имя клиента непустое и не равно значению по умолчанию
                    if (!clientName.isEmpty() && clientName != "Введите ваше имя: ") {
                        outMessage.println(clientName + " вышел из чата!");
                    } else {
                        outMessage.println("Участник вышел из чата, так и не представившись!");
                    }
                    // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
                    outMessage.println("##session##end##");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException exc) {

                }
            }
        });


    }


    // отправка сообщения
    public void sendMsg(String st) {

        // отправляем сообщение
        outMessage.println(st);
        outMessage.flush();
    }

    public static void main(String []args){

        JFrame frame = new JFrame("Карточная игрушка");

        frame.setContentPane(new CardGame(frame).jpanel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


    }
}
