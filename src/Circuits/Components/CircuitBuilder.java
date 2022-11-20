package Circuits.Components;

import Circuits.Components.Breadboard.Breadboard;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CircuitBuilder extends JFrame {

    private JPanel mainPanel;
    private JButton addResistor;
    private JButton addVoltageSource;
    private JButton addWire;
    private JButton analyzeCircuit;
    private JButton removeComponent;
    private JPanel ComponentSelectPanel;
    private JScrollPane BreadboardJScroll;
    private JScrollPane ComponentSelectJScroll;
    private JPanel BreadboardInteractivePanel; // holds our grid
    private JButton rotateLeftButton;
    private JButton rotateRightButton;
    private JTextArea outputTextGoesHereTextArea;
    private JButton saveCircuit;
    private JButton loadCircuit;

    private Breadboard breadboard = new Breadboard(outputTextGoesHereTextArea);

    /* Sources Referenced:
     * https://stackoverflow.com/questions/30356545/java-creating-a-jframe-using-gridlayout-with-mouse-interactive-jpanels
     * geeksforgeeks.org/event-handling-in-java/
     */
    public CircuitBuilder(){
        setContentPane(mainPanel);
        setTitle("Hello, World!");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        breadboard.createGrid(BreadboardInteractivePanel,15,10);

        BreadboardInteractivePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println("Click registered at " + e.getPoint());
            }
        });


        Dimension windowSize = new Dimension(800,500);
        setSize(windowSize);
        ComponentSelectJScroll.setSize(new Dimension(windowSize.width / 20,windowSize.height));
        BreadboardJScroll.setSize(new Dimension(windowSize.width / 80, windowSize.height));
        addResistor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Breadboard.SetSelectedComponent(new Resistor(new StandardNum(100, Magnitude.NONE)));
            }
        });

        addVoltageSource.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Breadboard.SetSelectedComponent(new VoltageSource(new StandardNum(10,Magnitude.NONE)));
            }
        });
        removeComponent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Breadboard.SetSelectedComponent(null);
            }
        });
        addWire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Breadboard.SetSelectedComponent(new Wire());
            }
        });
        analyzeCircuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                breadboard.AnalyzeCircuit();
            }
        });
        saveCircuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                breadboard.SaveCircuit();
            }
        });
        loadCircuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                breadboard.LoadCircuit();
            }
        });
    }
}
