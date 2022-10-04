package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Charge extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'Q'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.coulomb;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Charge(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Charge(StandardNum value) { super(value);}
}
