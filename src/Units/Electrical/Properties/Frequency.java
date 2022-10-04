package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Frequency extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'f'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.hertz;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Frequency(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Frequency(StandardNum value) { super(value);}
}
