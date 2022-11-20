package Circuits;

import Circuits.Components.Component;
import Circuits.Components.Resistor;
import Units.Metric.StandardNum;

import java.util.*;

import static Extensions.HelperMethods.Metric.ParseStandardNum;
import static Extensions.HelperMethods.Misc.GetAlphabetCode;
import static Extensions.HelperMethods.Misc.GetNumberCode;
import static java.util.Map.entry;

public class Node {

    /* A logical representation of the circuit.
     * Let n = the number of components in the node.
     * n = 1 implies that there is only one path through the node.
     * n > 1 implies that there are multiple components connected in parallel to the previous node (n paths for n components)
     */


    /*
     * Returning to this code 10/2/2022 with new ideas.
     *
     * A node, as defined in electronics has two points, Node.in and Node.out
     * Everything between the two points can be calculated and used as a basis for other circuit calculations.
     *
     * All current that flows into a node also exits the node.
     *
     * A node can also have subnodes, which can also have subnodes ad infinitum (wherein all current flowing into them also flows out)
     *
     *
     */
    Node[] nodeIn; //node connection IN; list of everything connected to the input of thisssssssssssss
    Node[] nodeOut; //node connection OUT

    // now, the components. they will link one to the other, connected between nodeIn and nodeOut
    /*
     * Want: use a letter-number system to designate/access components.
     * A = row 0, B = row 1, C = row 2, etc.
     * 0 = column 0, 1 = column 1, 2 = column 2, etc.
     *
     * Idea is A0, A1, A2 are all located in [0][0-2] (row, column)
     * This allows look-ahead for component connections.
     *
     * Say I want to add a component. It will be designated K0 where K = number of rows in column 0 to alphabet (so if there are 5 rows, A B C D E are in use, we designate it F)
     * Now I can look in column 2, [A-E][1] to see components in column 2. I can then offer to link the OUT terminal of the newly added component to any/all of the components
     * in rows A-E, column 1
     */
    int arraySize = 20;

    Component[][] components = new Component[arraySize][]; // for now, let's limit it to 20 rows w/ 20 columns of components. Seems enough for now. TODO: Dynamically size this array or use a data structure that better accomplishes this task

    // direct initialization of a map https://stackoverflow.com/questions/6802483/how-to-directly-initialize-a-hashmap-in-a-literal-way
    private Map<String,Runnable> addComponentMap = Map.ofEntries(
            entry("R",()->{
                AddComponentChain(new Resistor(null));
            }),
            entry("V",()->{

            }),
            entry("F",()->{

            })
    );
    private Map<String,Runnable> mainMenuMap = Map.ofEntries(
            entry("A",()->{
                String componentSelect = "(R)esistor | (V)oltage Source"; // TODO: See about changing this to use reflection later
                System.out.println("Add which component? " + componentSelect);
                //DoWithInput(addComponentMap, "Q");
                //AddComponentChain();
            }),
            entry("C",()->{
                System.out.println("I see that you'd like to check the list of components.");
                System.out.println("There are currently " + NumRowsInUse() + " component paths.");
            }),
            entry("F",()->{
                System.out.println("Node finalizing not yet implemented!");
            })
    );

    void AddComponentChain(Component C){

        Scanner scanner = new Scanner(System.in);
        int index = 0; //index of the component in its chain. so if we made a new component, it will be Designation0 (e.g. A0, B0, etc. but as we add more components in the chain, they will be A1, A2, etc.)


        if(C instanceof Resistor){
            String EnterComponentValue = "Please enter a component value in standard form (e.g. 1kV, 3.75MO, 373pA)";
            System.out.println(EnterComponentValue);
            String compValue = scanner.next();
            System.out.println(compValue);

            StandardNum s = ParseStandardNum(compValue);
            Resistor r = new Resistor(s);

            r.SetID(GetDesignation(), index);
            // finally, we add it.
            System.out.println("Adding Resistor with value: " + s.mantissa + s.exponent.GetSymbol() + r.resistance.SI_Unit() + " to component network. Designation: " + r.GetID());
            System.out.println(GetNumberCode(r.GetIDVariable()));
            Set(r);

            System.out.println("Component now exists at " + components[GetNumberCode(r.GetIDVariable())][r.GetIDSubscript()].GetID());
        }
    }
    /*
     * Helper methods we need for accessing our arrays:
     * components.length (gives us number of rows)
     * components.LookAhead() (gives us a list of components in the next column for EVERY ROW, given a starting column)
     *
     */

    int NumRowsInUse(){
        // loop through array, look for null values
        for(int i = 0; i < components.length; i++){
            if(components[i] == null)
                return i;
        }

        return -1;
    }

    char GetDesignation(){
        return GetAlphabetCode(NumRowsInUse());
    }

    /*
    void AddRows(){
        for(int i = 0; i < components.length; i++) {
            if (components[i] == null) {
                components[i] = new Component[arraySize]; //add columns to this row
            }
        }
    }
    */

    void AddColumnsToRow(int row){
        if(components[row] == null)
            components[row] = new Component[arraySize];
    }
    void Set(Component c){
        // set new entry into our array
        AddColumnsToRow(GetNumberCode(c.GetIDVariable())); // add columns if we need them
        components[GetNumberCode(c.GetIDVariable())][c.GetIDSubscript()] = c;
    }

    // getting numeric input with scanner https://beginnersbook.com/2017/09/java-program-to-read-integer-value-from-the-standard-input/
    public void BuildNode(){

        // list w/ kvp? can't use int for kvp.

        String MainMenuText = "(A)dd new component, (C)heck current components, (F)inish and build node";

        // main menu
        System.out.println(MainMenuText);

        DoWithInput(mainMenuMap,"Q");

        /*
        int counter = 0;
        String nextInput = " ";
        while(!nextInput.equals("F")) {
            if(nextInput.equals("A")){
                System.out.println(ComponentSelect);
                String compSelect = scanner.next();
                System.out.println(EnterComponentValue);
                String compValue = scanner.next();
                System.out.println(compValue);
                int index = 0; //index of the component in its chain. so if we made a new component, it will be Designation0 (e.g. A0, B0, etc. but as we add more components in the chain, they will be A1, A2, etc.)

                switch(compSelect) {
                    case "R":

                        StandardNum s = ParseStandardNum(compValue);
                        Resistor r = new Resistor(s);

                        r.SetID(GetDesignation(),index);
                        // finally, we add it.
                        System.out.println("Adding Resistor with value: " + s.mantissa + s.exponent.GetSymbol() + r.resistance.SI_Unit() + " to component network. Designation: " + r.GetID());
                        System.out.println(GetNumberCode(r.GetIDVariable()));
                        Set(r);

                        System.out.println("Component now exists at " + components[GetNumberCode(r.GetIDVariable())][r.GetIDSubscript()].GetID());
                        break;

                    default: System.out.println("Invalid entry detected. This is likely because of a typo, or because the component you wish to add has not been implemented yet.");
                }
                //now is when we would loop to add more components one after the other

            } else if (nextInput.equals("C")) {
                System.out.println("I see that you'd like to check the list of components.");
                System.out.println("There are currently " + counter + " component paths.");
            }
            nextInput = scanner.next();
        }
    */
        System.out.println("Finished building node.");

    }

    // Found documentation about runnables at www.stackoverflow.com/questions/443708/callback-functions-in-java
    void DoWithInput(Map<String,Runnable> inputsAndActions, String escapeKey)
    {
        Scanner scanner = new Scanner(System.in);
        String nextInput="";

        while(!nextInput.equals(escapeKey)){
            // ugly, temporary way that we'll use to get our values (Runnables). Literally comparing the values of two reference type(our input vs each key) to see if they match and then using the key that has the pointer data we need to get our Runnable! I know how fricked up this is, man!
            for(String key : inputsAndActions.keySet()){
                if(key.equals(nextInput)){
                    System.out.println("found key");
                    inputsAndActions.get(key).run();
                    //Runnable nextAction = inputsAndActions.get(key);
                    //System.out.println(nextAction);
                    //if(nextAction != null) nextAction.run();
                }

            }

            nextInput = scanner.next();
        }

    }
}
