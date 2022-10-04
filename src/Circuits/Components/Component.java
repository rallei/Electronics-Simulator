package Circuits.Components;

public class Component implements ComponentInterface {

    Component[] componentIn;
    Component[] componentOut;

    // for example A1, F7, etc.
    char namedVariable;
    int subscript;
    public void SetID(char _namedVariable, int _subscript){
        namedVariable = _namedVariable;
        subscript = _subscript;
    }
    public String GetID(){ return namedVariable + Integer.toString(subscript); }
    public char GetIDVariable(){ return namedVariable; }
    public int GetIDSubscript(){ return subscript; }

}
