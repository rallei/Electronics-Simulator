package Circuits;

import Circuits.Components.Component;

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

    Component[] components;

    public Boolean addComponent(Component c){
        return false;
    }

    public Component getComponent(String id){
        // code to search components for a component with a matching ID - if such a thing proves necessary
        return null;
    }
}
