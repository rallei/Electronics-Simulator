package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Capacitance extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'C'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.farad;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Capacitance(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Capacitance(StandardNum value) { super(value);}
}
