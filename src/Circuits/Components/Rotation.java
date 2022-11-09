package Circuits.Components;

public enum Rotation {

    TOP(0),
    RIGHT(1),
    BOT(2),
    LEFT(3);

    private int rotation;
    Rotation (int rotation){
        this.rotation = rotation;
    }

    public static Rotation GetDirection (int rotation){
        switch(rotation){
            case 0: return TOP;
            case 1: return RIGHT;
            case 2: return BOT;
            case 3: return LEFT;
        }
        return null;
    }


    public int GetValue(){
        return this.rotation;
    }
}
