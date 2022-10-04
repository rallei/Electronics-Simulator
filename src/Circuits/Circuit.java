package Circuits;

import Circuits.Components.Component;
import Circuits.Components.Resistor;
import Circuits.Components.VoltageSource;
import Units.Electrical.Properties.Current;
import Units.Electrical.Properties.Resistance;
import Units.Electrical.Properties.Voltage;
import Units.Metric.Magnitude;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Circuit implements CircuitInterface {

    /*
    * logic... what makes a circuit?
    * Voltage source
    * Connection (such as a wire, and we may wish for different wires)
    * Components between each connection
    */

    /*
     * Idea 1:
     * Use 3D array of size n (say, 10x20)
     * Use array as virtual 'breadboard'
     * 
     * Use a class 'CircuitBuilder' which builds a circuit.
     * Components can have 'size' (1x3 for example: connector-component-connector)
     * 
     * This approach can be used to produce a logical representation that mirrors actual representation.
     * 
     * Idea 2:
     * Use Node as circuit. No list. Node creates a linked list itself, but with up/down/left/right rather than simply next/prev.
     * 
     * Idea 3:
     * Create a very simple approach. Node 1: Vs; Node 2: Three resistors (therefore, in parallel); Node 3: ...
     * Pros: Suitable for text output & mathematical modeling
     *       Easy to implement
     *       Can be used later on with pathfinding algorithms (the math will still be good)
     * 
     * Thoughts:
     * Pretty sure we need a pathfinding algorithm for both of these (and perhaps no matter what we do)
     */

    private Node circuit;
    
    public Boolean AddNode(Node n){
        if(circuit != null) {
            //circuit.nextNode = n;

            return true;
        }
        else{
            circuit = n;
        }
        return false;
    }
    
    public Boolean RemoveNode(Node n){
        // implement later
        return false;
    }
    /*
    public void BuildCircuit(){
        
        Node lastNode = circuit.nextNode;
        
        // look for the last node in the circuit
        while(lastNode.nextNode != null) {
            lastNode = lastNode.nextNode;
        }
        
        // then connect it to the first node to complete the circuit!
        lastNode.nextNode = circuit.previousNode;
    }
    
    public void PrintCircuitInfo(){
        // initialize starting values...
        double r = 0;
        double v = 0;


        Node startNode = circuit;
        Node currentNode = startNode;
        int counter = 0;
        // then take a walk around the circuit to total everything up
        while(currentNode != null && currentNode != startNode | counter == 0){
            // implementing foreach loop, documentation used: (https://www.geeksforgeeks.org/for-each-loop-in-java/)
            for (Component c : currentNode.components)
            {
                // implementing switch block, documentation used: (https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html, https://stackoverflow.com/questions/5579309/is-it-possible-to-use-the-instanceof-operator-in-a-switch-statement)

                if(c instanceof Resistor){
                    Resistor resistor = (Resistor) c;
                    r += resistor.resistance.GetValue();

                } else if (c instanceof VoltageSource) {
                    VoltageSource voltageSource = (VoltageSource) c;
                    v += voltageSource.GetVoltage();

                }
            }
            counter++;
            currentNode = currentNode.nextNode;
            //after simple checks (that the nodes work and all), we can create groupings to determine V, R, I, accounting for whether components are in series or parallel


            //Voltage V = new Voltage(v, Magnitude.none);
            //Resistance R = new Resistance(r, Magnitude.none);
            //Current I = new Current(0,Magnitude.none);
        }


        System.out.println("Total voltage IN: " + v);
        System.out.println("Total resistance values within circuit: " + r);
    }
    */
}
