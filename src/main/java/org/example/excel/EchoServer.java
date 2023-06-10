package org.example.excel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EchoServer extends JFrame {
    private List<Socket> clientSockets;
    private List<Receiver> receivers;
    private ServerSocket listener;
    private JTextArea messageArea;
    private JTextArea tableArea[];
    private JTabbedPane tabbedPane;
    int number;
    private int tableNumber;
    public EchoServer() {
        setTitle("메뉴 주문 창");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel receivePanel = new JPanel();
        JPanel tablePanel = new JPanel();
        receivePanel.setLayout(new BorderLayout());
        tablePanel.setLayout(new FlowLayout());

        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        messageArea = new JTextArea();

        tableArea = new JTextArea[5];
        for (int i = 0; i < tableArea.length; i++) {
            tableArea[i] = new JTextArea();
            tableArea[i].setText("테이블 번호 " + (i+1) + ":\n");
            tablePanel.add(new JScrollPane(tableArea[i]));
        }

        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        receivePanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("서버 통신", receivePanel);
        tabbedPane.addTab("테이블 주문 현황",tablePanel);

        getContentPane().add(tabbedPane);
        setSize(1450, 600);
        setVisible(true);

        clientSockets = new ArrayList<>();
        receivers = new ArrayList<>();

        try {
            setupConnection();
        } catch (IOException e) {
            handleError(e.getMessage());
        }
    }

    private void setupConnection() throws IOException {
        listener = new ServerSocket(9999);
        number = tableNumber;
      
        while (true) {
            Socket socket = listener.accept();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            tableNumber = dataInputStream.readInt();
            clientSockets.add(socket);

            Receiver receiver = new Receiver(socket, tableNumber);
            receivers.add(receiver);

            Thread thread = new Thread(receiver);
            thread.start();


        }
    }

    private void handleError(String errorMessage) {
        System.out.println(errorMessage);
        System.exit(1);
    }

    private class Receiver implements Runnable {
        private final int tableNumber;
        private Socket socket;
        private BufferedReader in;
        private BufferedWriter out;


        public Receiver(Socket socket, int tableNumber) {
            this.socket = socket;
            this.tableNumber = tableNumber;
            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                handleError("오류가 발생했습니다");
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // 메뉴 주문을 서버 창과 messageArea에 출력
                    System.out.println("메뉴 주문: " + message);
                    final String finalMessage = message;
                    SwingUtilities.invokeLater(() -> {
                        EchoServer.this.messageArea.append("테이블번호: "+tableNumber+" " + finalMessage + "\n");

                        switch (tableNumber) {
                            case 1:
                                tableArea[0].append("메뉴 주문: " + finalMessage + "\n");
                                break;
                            case 2:
                                tableArea[1].append("메뉴 주문: " + finalMessage + "\n");
                                break;
                            case 3:
                                tableArea[2].append("메뉴 주문: " + finalMessage + "\n");
                                break;
                            case 4:
                                tableArea[3].append("메뉴 주문: " + finalMessage + "\n");
                                break;
                            case 5:
                                tableArea[4].append("메뉴 주문: " + finalMessage + "\n");
                                break;
                            default:
                                System.out.println("잘못된 테이블 번호입니다: " + tableNumber);
                                break;
                        }
                    });
                }
            } catch (IOException e) {
                handleError("오류가 발생했습니다");
            }
        }
    }

    public static void main(String[] args) {
        new EchoServer();
    }
}
