package Units.Electrical;

public enum SIUnit {


    coulomb("C"),
    farad("F"),
    siemens("S"),
    ampere("A"),
    joule("J"),
    hertz("Hz"),
    henry("H"),
    watt("W"),
    ohm("Ω"),
    volt("V"),
    second("s"),
    ;

    private String _suffix;

    public String suffix(){
        return _suffix;
    }

    SIUnit(String symbol) {
        _suffix = symbol;
    }
}
