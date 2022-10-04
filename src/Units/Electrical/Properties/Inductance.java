package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Inductance extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'L'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.henry;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Inductance(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Inductance(StandardNum value) { super(value);}
}
