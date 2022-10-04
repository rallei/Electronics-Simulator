package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Time extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 's'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.second;
    }

    protected String definition() {
        return null;
    }

    public Time(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Time(StandardNum value) { super(value);}
}