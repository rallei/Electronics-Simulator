package Units.Metric;

import java.lang.management.ManagementFactory;

public enum Magnitude {

    FEMTO('f',-15),
    PICO('p',-12),
    NANO('n',-9),
    MICRO('μ',-6),
    MILLI('m',-3),
    NONE(' ',0),
    KILO('k',3),
    MEGA('M',6),
    GIGA('G',9),
    TERA('T',12)
    ;


    private char _prefix;
    private int _powerOfTen;
    Magnitude(char symbol, int power) {
        _prefix = symbol;
        _powerOfTen = power;
    }

    // How to access values in enums! https://stackoverflow.com/questions/35140408/how-to-get-value-from-a-java-enum
    // and an interesting piece about looking up enums by value https://stackoverflow.com/questions/7888560/how-to-get-the-enum-type-by-its-attribute
    public char GetSymbol(){
        return this._prefix;
    }
    public int GetPower(){
        return this._powerOfTen;
    }

    public static Magnitude GetMagnitudeByPower(int power){
        switch(power){
            case 12: return Magnitude.TERA;
            case 9: return Magnitude.GIGA;
            case 6: return Magnitude.MEGA;
            case 3: return Magnitude.KILO;
            case 0: return Magnitude.NONE;
            case -3: return Magnitude.MILLI;
            case -6: return Magnitude.MICRO;
            case -9: return Magnitude.NANO;
            case -12: return Magnitude.PICO;
            case -15: return Magnitude.FEMTO;
        }

        return null; // the disaster case. go on. break my code. >:O
    }

    public static Magnitude GetMagnitudeBySymbol(char symbol){
        switch(symbol){
            case 'f': return Magnitude.FEMTO;
            case 'p': return Magnitude.PICO;
            case 'n': return Magnitude.NANO;
            case 'u': return Magnitude.MICRO;
            case 'm': return Magnitude.MILLI;
            case 'k': return Magnitude.KILO;
            case 'M': return Magnitude.MEGA;
            case 'G': return Magnitude.GIGA;
            case 'T': return Magnitude.TERA;

            default: return Magnitude.NONE;
        }
    }
}
