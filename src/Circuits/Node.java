package Circuits;

import Circuits.Components.Breadboard.Breadboard.Path;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

import java.awt.*;
import java.util.ArrayList;

public class Node {

    public Point startJunction;
    public Point endJunction;
    public ArrayList<Path> paths;

    public Node(Point startJunction, ArrayList<Path> paths){
        this.startJunction = startJunction;
        this.paths = paths;
        endJunction = paths.get(0).endPoint();
    }

    public int GetNumResistors(){

        int numResistors = 0;

        for(Path path : paths){
            numResistors += path.GetNumResistors();
        }

        return numResistors;
    }

    public int GetNumNodes(){
        int numNodes = 0;

        for(Path path : paths){
            numNodes += path.GetNumNodes();
        }
        return numNodes;
    }

    public StandardNum GetResistance(){

        StandardNum returnValue = new StandardNum();
        //System.out.println("Exponent of standard number we're adding to: " + returnValue.exponent);

        for(Path path : paths){
            // if num paths > 1, then the resistances are in parallel, so add the reciprocal of resistance to the running total for each path

            //System.out.println("Standard Number before reciprocal: " + path.GetResistance().mantissa + "E" + path.GetResistance().exponent);
            StandardNum reciprocal = path.GetResistance().GetReciprocal();
            //System.out.println("Standard Number after reciprocal: " + reciprocal.mantissa + "E" + reciprocal.exponent);
            //System.out.println("Exponent of standard number we're adding to: " + returnValue.exponent);
            returnValue.Add(reciprocal);
        }

        return returnValue.GetReciprocal();
    }
}
