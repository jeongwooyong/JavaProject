package org.example.excel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class orderEX extends Thread {
    private static int num;

    public void run() {
        //스레드가 진행되면 TabbedTable아
        new TabbedTable(num);
    }
    //스레드의 테이블 카운트를 지정하는 메소드
    public void setCount(int num) {
        this.num =num;
    }
    //스레드의 테이블 카운트를 가져오는 메소드
    public static int getCount(){
        return num;
    }
}

public class Table extends JFrame implements ActionListener {
    private orderEX th1;
    private orderEX th2;
    private orderEX th3;
    private orderEX th4;
    private orderEX th5;
    private int num;
    //각 메소드의 스레드 지정

    public Table() {
        setTitle("Table Setting");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 40));

        JButton tb1 = new JButton("Table 1");
        JButton tb2 = new JButton("Table 2");
        JButton tb3 = new JButton("Table 3");
        JButton tb4 = new JButton("Table 4");
        JButton tb5 = new JButton("Table 5");
        //버튼이 눌렸을때 실행되는 이벤트리스너
        tb1.addActionListener(this);
        tb2.addActionListener(this);
        tb3.addActionListener(this);
        tb4.addActionListener(this);
        tb5.addActionListener(this);

        contentPane.add(tb1);
        contentPane.add(tb2);
        contentPane.add(tb3);
        contentPane.add(tb4);
        contentPane.add(tb5);

        setSize(350, 200);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Table();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        //각 테이블의 카운트를 지정하고 스레드를 진행시킴
        if (button.getText().equals("Table 1")) {
            if (th1 == null) {
                th1 = new orderEX();
                th1.setCount(1);
                th1.start();
            }
        } else if (button.getText().equals("Table 2")) {
            if (th2 == null) {
                th2 = new orderEX();
                th2.setCount(2);
                th2.start();
            }
        } else if (button.getText().equals("Table 3")) {
            if (th3 == null) {
                th3 = new orderEX();
                th3.setCount(3);
                th3.start();
            }
        } else if (button.getText().equals("Table 4")) {
            if (th4 == null) {
                th4 = new orderEX();
                th4.setCount(4);
                th4.start();
            }
        } else if (button.getText().equals("Table 5")) {
            if (th5 == null) {
                th5 = new orderEX();
                th5.setCount(5);
                th5.start();
            }
        }
    }
}

