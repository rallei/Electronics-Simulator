package Circuits.Components;

import Units.Electrical.Properties.Resistance;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Resistor extends Component implements VisualComponentInterface {

    public Resistance resistance;

    // standard connection is top-> bottom
    public ComponentPlacementPoints[] getDefaultConnectionPoints(){

        return new ComponentPlacementPoints[]{
                ComponentPlacementPoints.TOP,
                ComponentPlacementPoints.BOTTOM
        };
    }

    public Resistor(StandardNum _resistance){
        resistance = new Resistance(_resistance);


    }
}
