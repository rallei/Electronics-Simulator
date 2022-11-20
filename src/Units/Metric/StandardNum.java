package Units.Metric;

public class StandardNum {

    public double mantissa;
    public Magnitude exponent;

    public double GetValue(){
        return mantissa * Math.pow(10, exponent.GetPower());
    }

    public String PrintValue(){
        return Double.toString(mantissa) + exponent.GetSymbol();
    }

    public StandardNum(double _mantissa, Magnitude _exponent){
        mantissa = _mantissa;
        exponent = _exponent;
    }
    public StandardNum(){

    }
    //TODO: methods for addition, substraction, division, multiplication can all be handled by class StandardNum (out with the old quantity system)
}
