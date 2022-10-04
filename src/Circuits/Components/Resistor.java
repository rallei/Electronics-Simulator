package Circuits.Components;

import Units.Electrical.Properties.Resistance;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Resistor extends Component {


    public Resistance resistance;

    public Resistor(StandardNum _resistance){
        // default value of 100 Ohms for now
        resistance = new Resistance(_resistance);
    }
}
