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
        mantissa = 0;
        exponent = Magnitude.NONE;
    }

    private void Recalculate(StandardNum number){

        int amountChange = 0;
        // if our mantissa is too large...
        while(number.mantissa % 1000 > 0 && number.exponent.GetPower() + amountChange % 3 > 0){
            amountChange++;
            number.mantissa /= 10; // divide it by ten and add one to the power
        }
        if(amountChange != 0 && (number.mantissa < 1 || number.mantissa < 1000 && number.exponent.GetPower() + amountChange % 3 > 0))
            System.out.println("I don't think recalculating the mantissa really worked as we expected, sammy!...");
        // if our mantissa has no digits to the left of the decimal, OR has digits to the left, but our exponent isn't a power of 3
        while(number.mantissa < 1 || number.mantissa < 1000 && number.exponent.GetPower() + amountChange % 3 > 0)
        {
            amountChange--;
            number.mantissa *= 10;
        }
        //TODO: after much troubleshooting, i have realized that this method causes our exponent to become null, which means it is not properly calculating a new mantissa with an exponent as a power of 3 -- when we call GetMagnitudeByPower, it returns null because it doesn't find a magnitude with a matching power of 3

        number.exponent = Magnitude.GetMagnitudeByPower(number.exponent.GetPower() + amountChange);
    }

    //add another standard number to this one
    public void Add(StandardNum number){
        int DifferenceOfMagnitude = number.exponent.GetPower() - exponent.GetPower();
        mantissa += number.mantissa * Math.pow(10, DifferenceOfMagnitude);

        //Recalculate(this);
    }

    public StandardNum GetReciprocal(){
        // there may be some numerical data loss with this approach, converting to double
        StandardNum reciprocal = new StandardNum(1 / GetValue(),Magnitude.NONE);

        //Recalculate(reciprocal); // and recalculate the standard number :)

        return reciprocal; // then return!
    }

    public String ToString(){
        return Double.toString(mantissa) + exponent.GetSymbol();
    }

    //TODO: methods for addition, substraction, division, multiplication can all be handled by class StandardNum (out with the old quantity system)
}
