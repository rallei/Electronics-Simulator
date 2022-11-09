package Circuits.Components;

import Units.Electrical.Properties.Resistance;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Resistor extends Component implements VisualComponentInterface {

    public Resistance resistance;

    // standard connection is top-> bottom
    public int[] getConnectionPoints(){
        return new int[]{0,2};
    }

    public Resistor(StandardNum _resistance){
        resistance = new Resistance(_resistance);


    }
}
