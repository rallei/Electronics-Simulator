package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Resistance extends Quantity {
    @Override
    public char symbol(){ return 'R'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.ohm;
    }

    protected String definition() {
        return "Resistance is caused by the collision of electrons with other atoms. As electrons collide with atoms, they lose some of their energy, which restricts their movement. More collisions make for more restriction of movement." +
                "\nResistance is a measurement of this restriction.";
    }

    public Resistance(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Resistance(StandardNum value) { super(value);}

    public Resistance(){

    }
}
