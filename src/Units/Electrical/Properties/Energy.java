package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Energy extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'W'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.joule;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Energy(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Energy(StandardNum value) { super(value);}
}
