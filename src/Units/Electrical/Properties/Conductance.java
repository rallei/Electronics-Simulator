package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Conductance extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'G'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.siemens;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Conductance(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Conductance(StandardNum value) { super(value);}
}
