package Units.Electrical.Quantities;
import Extensions.HelperMethods.Formatting;
import Units.Electrical.QuantityInterface;
import Units.Electrical.SIUnit;
import Units.Metric.*;

// p. 24, Java 11 in a nutshell, doc comment example
/**
 * Base class for all electrical quantities
 * @implNote doc comments can contain <tt>HTML</tt> code, which is extracted by <tt>javadoc</tt>, processed, and used to create online documentation.
 * @version 1
 *
 * @author Sammy Chandler
 */
public abstract class Quantity implements QuantityInterface {

    /* these two values will likely be initialized automatically as the user enters text
       such as:
       13kHz, (value = 13, magnitude = kilo)
       .207pf, (value = .207, magnitude = pico)
       17800Mhz, (value = 17800, magnitude = mega) [obviously, not in engineering notation...
                                                    not sure if I want it to auto-convert    ]

     */
    StandardNum quantity;

    double Mantissa() {
        return quantity.mantissa;
    }

    Magnitude Exponent() {
        return quantity.exponent;
    }

    /**
     * @return returns the symbol that represents the quantity (suh as W for energy/work)
     */
    @Override
    public char symbol() {
        return 0;
    }

    /**
     * @return returns the symbol of the SI_Unit (such as J for the quantity of energy which is itself represented as W)
     */
    @Override
    public SIUnit SI_Unit() {
        return null;
    }

    /**
     * @return returns the current value of the quantity
     */
    public double GetValue() {
        // returns the full quantity. TODO: decide whether to explicitly multiply out quantities like this or rewrite math methods to perform simpler (less error prone) calculations; e.g. divide 10kV by 7MO: mantissa = 10/7, exp = 10x(3 - 6) [3 for kilo, from which we subtract 6 for mega]
        return quantity.GetValue();
    }


    protected abstract String definition();

    /**
     * @return returns a detailed definition of the given quantity as a string (this is largely for the benefit of the learner)
     */
    public String GetDefinition() {
        return Formatting.BeautifyString(definition());
    }

    /**
     * @param initialValue     initialization value of the quantity
     * @param initialMagnitude initialization of the magnitude (power of ten, that is, 10^initialMagnitude) value of the quantity
     */
    public Quantity(double initialValue, Magnitude initialMagnitude) {
        quantity = new StandardNum(initialValue, initialMagnitude);
    }

    public Quantity(StandardNum value) {
        quantity = value;
    }

    public Quantity() {
        //empty constructor to make jackson presumably happy
    }
}


