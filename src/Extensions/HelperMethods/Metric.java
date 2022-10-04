package Extensions.HelperMethods;

import Units.Metric.Magnitude;
import Units.Metric.StandardNum;

public class Metric {

    // String doesn't return as an array of chars directly as in python or C#! Source: https://stackoverflow.com/questions/2451650/how-do-i-apply-the-for-each-loop-to-every-character-in-a-string
    public static StandardNum ParseStandardNum(String input) {
        // initialize variables with empty/default values
        String mantissa = "";
        Magnitude exponent = Magnitude.NONE;

        // read characters in the string
        for (char c : input.toCharArray()) {
            // for each character that's a digit, add the digit to the mantissa String
            if(Character.isDigit(c))
                mantissa += c;
            // but when we find a letter, assume that it's the prefix and attempt to parse it
            else {
                exponent = Magnitude.GetMagnitudeBySymbol(c);
                break;
            }
        }

        // return the parsed number and prefix!
        return new StandardNum(Double.parseDouble(mantissa), exponent);
    }

}
