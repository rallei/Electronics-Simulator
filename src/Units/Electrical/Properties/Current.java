package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Current extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'I'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.ampere;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Current(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Current(StandardNum value) { super(value);}
}
