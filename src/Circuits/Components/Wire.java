package Circuits.Components;

import Units.Electrical.Properties.Resistance;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Wire extends Component implements VisualComponentInterface {

    public Resistance resistance;

    public int[] getConnectionPoints(){
        return new int[]{0};
    }

    public Wire(){
        // default value of 0 Ohms -- an 'ideal' wire
        resistance = new Resistance(new StandardNum(0, Magnitude.NONE));
    }
}
