package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Reactance extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'X'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.ohm;
    }


    protected String definition() {
        return "";
    }


    public Reactance(double initialValue, Magnitude initialMagnitude){ super(initialValue, initialMagnitude); }
    public Reactance(StandardNum value) { super(value);}
}
