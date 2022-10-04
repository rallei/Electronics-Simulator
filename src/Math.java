import Units.Electrical.Properties.*;

public class Math {


    // Parallel resistance methods... TODO: clean this up later

    /* case n variable value resistors in parallel:
    Recall that G is conductance, which is the reciprocal of resistance; that is G = 1/R.
    Gt, then, is the sum of the reciprocal of all R in parallel.
    Gt = 1/R1 + 1/R2 + ... + 1/Rn

    Formulas:
    Rt = 1/Gt
    Rt = 1 / G1 + G2 + ... + Gn
     */
    // case two resistors in parallel: Rt = R1*R2/R1+R2

    // case n equal value resistors in parallel: Rt = R/n

    //region Volt Methods

    // V = IR
    /**
     * @param R Resistance, measured in ohms
     * @param I Current, measured in amps
     * @return I*R, because V = I*R; returns the amount of voltage in Volts
     */
    public static double GetVoltage(Resistance R, Current I){
        return I.GetValue() * R.GetValue();
    }


    // V = W/Q -- the potential difference is equal to the energy divided by couloumbs, measured in volts -- the energy available for each coulomb
    /**
     * @param W Energy, measured in joules
     * @param Q Charge, measured in coulombs
     * @return W/Q, because V = W/Q; returns the amount of voltage in Volts
     */
    public static double GetVoltage(Energy W, Charge Q){
        return W.GetValue() / Q.GetValue();
    }

    //endregion
    //region Resistance Methods
    // R = V/I
    /**
     * @param V Voltage, measured in volts
     * @param I Current, measured in amps
     * @return V/I because R = V/I; returns the amount of resistance in Ohms
     */
    public static double GetResistance(Voltage V, Current I){
        return V.GetValue() / I.GetValue();
    }

    //endregion

    //region Conductance Methods
    // G = 1/R

    /**
     *
     * @param R Resistance, measured in Ohms
     * @return 1/R, because G = 1/R; Conductance is the reciprocal of Resistance; returns the amount of conductance in Siemens
     */
    public static double GetConductance(Resistance R){
        return 1/R.GetValue();
    }
    //endregion

    //region Current methods

    //I = V/R
    public static double GetCurrent(Resistance R, Voltage V){
        return V.GetValue() / R.GetValue();
    }

    // I = Q/t -- the current is equal to the number of coulombs flowing through a cross sectional area in material divided by time in seconds
    /**
     * @param Q Charge, measured in coulombs
     * @param t Time, measured in seconds
     * @return Q/t, because I = Q/t; returns the amount of current in Amps
     */
    public static double GetCurrent(Charge Q, Time t){
        return Q.GetValue() / t.GetValue();
    }

    // P = W/t -- the power is equal to the amount of energy divided by time in seconds, measured in watts
    /**
     * @param W Energy, measured in joules
     * @param t Time, measured in seconds
     * @return W/t, because P = W/t; returns the amount of power in Watts
     */
    public static double GetPower(Energy W, Time t){
        return W.GetValue() / t.GetValue();
    }



}
