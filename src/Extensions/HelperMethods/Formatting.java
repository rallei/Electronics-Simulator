package Extensions.HelperMethods;

import Units.Metric.Magnitude;

public class Formatting {

    public static String BeautifyString(String s){
        /* Cut long pieces of text with multiple sentences in something more readable.
         * So, maybe chop every three sentences OR N number of chars, whichever comes first and without breaking sentence structure.
         *
         * It won't properly distinguish logical separations on its own, but we can use newline/carriage return ourselves to separate
         * things in a logical manner
         */

        String returnString = s;
        return returnString;
    }
}
