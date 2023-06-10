package org.example.excel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.swing.ImageIcon;


public class TabbedTable extends JFrame implements ActionListener {
    // 교과서 497p에 있는 JTabbedPane 사용. 인기메뉴/ 면류/ 밥류 등등..
    private BufferedReader in = null;
    private BufferedWriter out = null;
    //소켓
    private Socket socket = null;
    private Receiver receiver = null; // JTextArea를 상속받고 Runnable 인터페이스를 구현한 클래스로서 받은 정보를 담는 객체
    private JTextField sender = null; // JTextField 객체로서 보내는 정보를 담는 객체

    private JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);
    private JTabbedPane text = new JTabbedPane(JTabbedPane.RIGHT);
    private JTextArea basketText = new JTextArea();
    private JTextArea billText = new JTextArea();
    private JTextArea last = new JTextArea();

    // 인기메뉴에 해당하는 Panel
    private JPanel popular = new JPanel();
    private JPanel drink = new JPanel();
    private JPanel[][] p,c;
    private JButton[][] plus, minus, basket;
    private JButton getbasket,canclebasket,total;
    private JLabel[][] lWon,lmenu, count1;
    private org.example.excel.orderEX orderEX;
    private int number;
    int totalsum =0;
    public TabbedTable(int number) {
        this.number = number;
        // 타이틀 제목에 테이블 번호를 입력함
        setTitle("Table" + "명지 주문 시스템 테이블 번호 " + number);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        receiver = new Receiver(); // 서버에서 받은 메시지를 출력할 컴퍼넌트

        String output = null;
        sender = new JTextField("고객요청사항: ");
        sender.addActionListener(this);
        //오른쪽 펜에 붙일 Area
        text.add(new JScrollPane(receiver),"주문내역"); // 스크롤바를 위해  ScrollPane 이용
        text.add(sender,"고객요청사항");

        //버튼 개수와 그리드 레이아웃 크기 설정
        int numCols = 3;
        int numRows = 3;
        int size =10;
        final int[] count = {0};
        //GridLayout 으로 설정. 3,3 5는 픽셀단위
        popular.setLayout(new GridLayout(numRows, size, 5, 5));
        drink.setLayout(new GridLayout(numRows, size, 5, 5));

        //버튼 배열 생성
        p = new JPanel[numRows][numCols];
        count1 = new JLabel[numRows][numCols];
        lWon = new JLabel[numRows][numCols];
        lmenu = new JLabel[numRows][numCols];
        plus = new JButton[numRows][numCols];
        minus = new JButton[numRows][numCols];
        basket = new JButton[numRows][numCols];
        getbasket = new JButton("주문하기");
        canclebasket = new JButton("취소");
        total = new JButton("총 금액 표시");

        //순서가 바뀌는 것을 방지하기 위해서 linkedHashMap을 사용
        LinkedHashMap<String, String> price = new LinkedHashMap<>();
        price.put("10000","제육덮밥");
        price.put("9000", "고추장찌개");
        price.put("11000","돌솥비빔밥");
        price.put("7000","순두부찌개");
        price.put("6000","오므라이스");
        price.put("4000","라면");
        price.put("4500","떡만두국");
        price.put("6500","짜장면");
        price.put("25000","해물찜");

        //키의 값을 이용해서 사진을 가져오기 위해서 Generic으로 설정
        LinkedHashMap<String, String> imagePaths = new LinkedHashMap<>();
        imagePaths.put("10000", "c:/제육덮밥.png");
        imagePaths.put("9000", "c:/고추장찌개.png");
        imagePaths.put("11000", "c:/돌솥비빔밥.png");
        imagePaths.put("7000", "c:/순두부찌개.png");
        imagePaths.put("6000", "c:/오므라이스.png");
        imagePaths.put("4000", "c:/라면.png");
        imagePaths.put("4500", "c:/떡국.png");
        imagePaths.put("6500", "c:/짜장면.png");
        imagePaths.put("25000", "c:/해물찜.png");

        LinkedHashMap<String, String> coffe = new LinkedHashMap<>();
        coffe.put("1200","아이스 아메리카노");
        coffe.put("1000", "아메리카노");
        coffe.put("3000","카푸치노");
        coffe.put("2500","자몽에이드");
        coffe.put("2000","레몬에이드");
        coffe.put("4600","딸기스무디");
        coffe.put("4800","녹차라떼");
        coffe.put("4200","초코라떼");
        coffe.put("5500","소주");
        coffe.put("5000","맥주");

        LinkedHashMap<String, String> image = new LinkedHashMap<>();
        image.put("1200","c:/아이스 아메리카노.jpg");
        image.put("1000", "c:/아메리카노.jpg");
        image.put("3000","c:/카푸치노.jpg");
        image.put("2500","c:/자몽에이드.jpg");
        image.put("2000","c:/레몬에이드.jpg");
        image.put("4600","c:/딸기스무디.jpg");
        image.put("4800","c:/녹차라떼.jpg");
        image.put("4200","c:/초코라떼.jpg");
        image.put("5500","c:/소주.jpg");
        image.put("5000","c:/맥주.jpg");
        //교과서 414p 에 있는 Iterator 를 사용함.
        Set<String> keys = price.keySet();
        Iterator<String> it = keys.iterator();



        for(int i=0; i<numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                p[i][j] = new JPanel();

                String key = it.next();
                //사진을 가져오기 위해서 key로 가져오고, 크기를 조절하기 위해서 다음과 같이 진행함
                String imagePath = imagePaths.get(key);
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image img = imageIcon.getImage();
                Image sizeImage = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                ImageIcon changeIcon = new ImageIcon(sizeImage);
                JLabel imageLabel = new JLabel(changeIcon);

                String imagePath2 = image.get(key);
                ImageIcon imageIcon2 = new ImageIcon(imagePath2);
                Image img2 = imageIcon2.getImage();
                Image sizeImage2 = img2.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                ImageIcon changeIcon2 = new ImageIcon(sizeImage2);
                JLabel imageLabel2 = new JLabel(changeIcon2);

                // 값을 넣기 위해서 Iterator를 사용한 것.
                count1[i][j] = new JLabel("1");
                lWon[i][j] = new JLabel(key );
                lmenu[i][j] = new JLabel(price.get(key));
                plus[i][j] = new JButton("+");
                minus[i][j] = new JButton("-");
                basket[i][j] = new JButton("장바구니");
                popular.add(p[i][j]);
                p[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                p[i][j].setLayout(new FlowLayout(FlowLayout.CENTER));
                p[i][j].add(plus[i][j]);
                p[i][j].add(count1[i][j]);
                p[i][j].add(minus[i][j]);
                p[i][j].add(basket[i][j]);
                p[i][j].add(lWon[i][j]);
                p[i][j].add(lmenu[i][j]);
                p[i][j].add(imageLabel);
            }
        }
        // + 누르면 숫자 감소
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                final int row = i; // Capture the value of i in a final variable
                final int col = j; // Capture the value of j in a final variable

                plus[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String count = count1[row][col].getText();
                        int menucount = Integer.parseInt(count);
                        menucount++;
                        count1[row][col].setText(String.valueOf(menucount));

                    }
                });
            }
        }
        // -  누르면 숫자 감소
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                final int row = i;
                final int col = j;

                minus[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String count = count1[row][col].getText();
                        int menucount = Integer.parseInt(count);

                        if(menucount == 0 )
                            menucount =0;
                        else
                            menucount--;
                        count1[row][col].setText(String.valueOf(menucount));

                    }
                });
            }
        }
        // 개수가 0 이하이면 버튼의 리스너가 동작 안하게 하고 싶은데 안됨.
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                final int row = i; // Capture the value of i in a final variable
                final int col = j; // Capture the value of j in a final variable
                String count2 = count1[row][col].getText();
                int menucount = Integer.parseInt(count2);
                if (menucount == 0)
                    basket[i][j].setEnabled(false);
                else {
                    basket[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String menu = lmenu[row][col].getText(); // Use the captured values
                            String won = lWon[row][col].getText();
                            int count = Integer.parseInt(count1[row][col].getText());

                            int price = Integer.parseInt(won);
                            int sum = price * count;
                            totalsum += sum;

                            String output = String.format("%s  %s원  %d 개\n", menu, won, count);

                            basketText.append(output);


                        }
                    });
                }
            }
        }
        //장바구니
        JPanel buy = new JPanel();
        basketText.setText("");
        buy.add(new JScrollPane(basketText));
        buy.add(getbasket);
        buy.add(canclebasket);
        buy.add(total);

        //주문하기 버튼 리스너
        getbasket.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String call = basketText.getText(); // 텍스트 필드에 사용자가 입력한 문자열
                try {
                    out.write(call); // 문자열 전송
                    ;
                    receiver.append(call);
                    billText.append(call);


                    out.flush();


                    basketText.setText("");
                    //총 가격을 나타내는 곳
                    last.setText("총 가격 : " + totalsum );


                } catch (IOException e1) {
                    handleError(e1.getMessage());
                }
            }
        });
        //장바구니에 있는 취소 버튼 구현
        canclebasket.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                basketText.setText("");
            }
        });

        try {
            setupConnection();
        } catch (IOException e) {
            handleError(e.getMessage());
        }

        Thread th = new Thread(receiver); // 상대로부터 메시지 수신을 위한 스레드 생성
        th.start();

        //계산서
        JPanel bill = new JPanel();
        bill.add(new JScrollPane(billText));
        bill.add(new JScrollPane(last));
        billText.setText("테이블 번호: " + number +" 계산서 \n\n");
        last.setText("총 가격: " + totalsum);


        // private JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT);
        // BorderLayout의 왼쪽에 추가.
        JScrollPane popularScroll = new JScrollPane(popular);
        popularScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  // 수직 스크롤
        JScrollPane coffeScroll = new JScrollPane(drink);
        coffeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  // 수직 스크롤
        popular.setPreferredSize(new Dimension(1000, 600)); // 원하는 크기로 설정
        pane.addTab("인기 메뉴", popularScroll);
        pane.addTab("음료",coffeScroll);
        pane.add("장바구니", buy);
        pane.add("계산서", bill);
        add(pane, BorderLayout.WEST);
        add(text, BorderLayout.EAST);


        setSize(1450, 600);
        setVisible(true);
    }
    //Receiver로, 문자열을 받는 곳
    private class Receiver extends JTextArea implements Runnable {
        @Override
        public void run() {
            String msg = null;
            while (true) {
                try {
                    msg = in.readLine(); // 상대로부터 한 행의 문자열 받기
                } catch (IOException e) {
                    handleError(e.getMessage());
                }
                this.append("\n  클라이언트 : " + msg); // 받은 문자열을 JTextArea에 출력
                int pos = this.getText().length();
                this.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동
            }
        }
    }
    private static void handleError(String string) {
        System.out.println(string);
        System.exit(1);
    }
    //고객 요청사항 있는 곳
    public void actionPerformed(ActionEvent e) { // JTextField에 <Enter> 키 처리
        if (e.getSource() == sender) {
            String msg = sender.getText(); // 텍스트 필드에 사용자가 입력한 문자열
            try {
                if (msg.startsWith("고객요청사항:")) {
                    msg = msg.substring(8); // "고객요청사항:" 부분을 제거하여 전송
                }

                out.write("고객요청사항: " + msg + "\n"); // 문자열 전송
                out.flush();

                int pos = receiver.getText().length();
                receiver.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동
                sender.setText("고객요청사항: ");

            } catch (IOException e1) {
                handleError(e1.getMessage());
            }
        }
        //주문하기 버튼
        else if (e.getSource() == getbasket) {
            String call = basketText.getText();

            try {
                out.write("테이블 번호:" + number+ "\n"+call+"\n"); // 문자열 전송
                out.flush();


                sender.setText(null); // 입력창의 문자열 지움
            } catch (IOException e1) {
                handleError(e1.getMessage());
            }
        }
    }
    //소켓과 연결이 되면 실행되는 메소드
    private void setupConnection() throws IOException {
        socket = new Socket("localhost", 9999); // 클라이언트 소켓 생성
        // System.out.println("연결됨");
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(number);
        dataOutputStream.flush();
        receiver.append("주문내역"+"\n");
        int pos = receiver.getText().length();
        receiver.setCaretPosition(pos); // caret 포지션을 가장 마지막으로 이동

        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 클라이언트로부터의 입력 스트림
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 클라이언트로의 출력 스트림
    }
    public static void main(String[] args) {
        int tableNumber=1;
        new TabbedTable(tableNumber);
    }
}