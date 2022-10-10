import Circuits.Circuit;
import Circuits.Components.*;
import Circuits.MainFrame;
import Circuits.Node;
import Units.Electrical.Properties.Current;
import Units.Electrical.Properties.Resistance;
import Units.Electrical.Properties.Voltage;
import Units.Metric.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Scanner;

public class MainInterface {
    public static void main(String[] args) {
        //System.out.print("Program still in development, please check back later! Jar bois.");

        /*
        var V = new Voltage(10, Magnitude.none);
        var R = new Resistance(50, Magnitude.none);
        var I = new Current(Math.GetCurrent(R,V), Magnitude.none);
        System.out.println("With " + V.GetValue() + V.symbol() + " and " + R.GetValue() + R.symbol() + " we get a result of " + I.GetValue() + I.symbol());

        System.out.println("Definition of voltage: " + V.GetDefinition());

         */

        /*
        Circuit circuit = new Circuit();

        VoltageSource vs = new VoltageSource(new Voltage(12,Magnitude.none));
        Resistor r = new Resistor();
        Resistor r2 = new Resistor();

        LinkedList<Component> comps1 = new LinkedList<>();
        comps1.addFirst(vs);

        LinkedList<Component> comps2 = new LinkedList<>();
        comps2.addFirst(r);

        LinkedList<Component> comps3 = new LinkedList<>();
        comps3.addFirst(r);
        comps3.add(r2);

        Node n1 = new Node(comps1);
        Node n2 = new Node(comps2);
        Node n3 = new Node(comps3);


        circuit.AddNode(n1);
        circuit.AddNode(n2);
        circuit.AddNode(n3);
        circuit.BuildCircuit();

        // finally, we test.

        circuit.PrintCircuitInfo();

         */

        //node building test
        System.out.println("This is a test.");

        CircuitBuilder circuitBuilder = new CircuitBuilder();
        //Node n = new Node();
        //n.BuildNode();
        //AddThenDoRunnable(5,10,()-> { System.out.println("Hey there, this is the runnable! As you can see... I'm running! :)");});

    }

    static void AddThenDoRunnable(int a, int b, Runnable r){
        int theTotal = a+b;
        System.out.println(MessageFormat.format("Wow, nice ints you have there! Is that {0} and {1} I see?",a,b));
        System.out.println(MessageFormat.format("Looks like the total is {0}",theTotal));
        System.out.println("Anyway, let's run that Runnable!");
        r.run();
    }
}
