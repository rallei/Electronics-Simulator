package Units.Electrical.Properties;

import Units.Electrical.Quantities.Quantity;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Voltage extends Quantity implements QuantityInterface {
    @Override
    public char symbol(){ return 'V'; }

    @Override
    public SIUnit SI_Unit() {
        return SIUnit.volt;
    }

    protected String definition() {
        return "A force of attraction exists between a positive and a negative charge. A quantity of energy must be exerted in the form of work to overcome the force and move the charges a given distance apart. All opposite charges have a potential energy because of the separation between them (because opposite forces attract/in neutral state, atoms of a given element have an equivalent number of electrons to protons). " +
                "\nWith this in mind, we define voltage as the difference in potential energy of the charges. Voltage is the 'driving force' in electric circuits and is what establishes current.";
    }

    public Voltage(double initialValue, Magnitude initialMagnitude){
        super(initialValue, initialMagnitude);
    }
    public Voltage(StandardNum value) { super(value);}
}
