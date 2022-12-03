package Circuits.Components;

import Units.Electrical.Properties.Resistance;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Resistor extends Component implements VisualComponentInterface {

    public Resistance resistance;

    @JsonIgnore
    // standard connection is top-> bottom
    public int[] getConnectionPoints(){
        return new int[]{0,2};
    }

    public Resistor(StandardNum _resistance){
        resistance = new Resistance(_resistance);


    }

    public Resistor(){
        resistance = new Resistance(new StandardNum(100, Magnitude.NONE));
    }
}
