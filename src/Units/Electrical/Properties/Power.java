package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Power extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'P'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.watt;
    }

    @Override
    protected String definition() {
        return null;
    }

    public Power(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Power(StandardNum value) { super(value);}
}
