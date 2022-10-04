package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Impedance extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'Z'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.ohm;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Impedance(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Impedance(StandardNum value) { super(value);}
}
