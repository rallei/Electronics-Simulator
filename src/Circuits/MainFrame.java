package Circuits;

import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JTextField textField1;
    private JTextArea outputTextTextArea;
    private JButton clearButton;
    private JButton okButton;
    private JPanel mainPanel;

    public MainFrame(){
        setContentPane(mainPanel);
        setTitle("Hello, World!");
        setSize(350,500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputTextTextArea.setText(textField1.getText());
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputTextTextArea.setText("");
            }
        });
    }
}
