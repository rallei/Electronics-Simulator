package Circuits.Components;

public enum ComponentPlacementPoints {
    TOP(0),
    RIGHT(1),
    BOTTOM(2),
    LEFT(3);

    public int direction;
    private ComponentPlacementPoints(int _direction){
        direction = _direction;
    }

    public ComponentPlacementPoints GetDirectionByRotation(int rotation){
        switch(rotation){
            case 0: return TOP;
            case 1: return RIGHT;
            case 2: return BOTTOM;
            case 3: return LEFT;

            default: return null;
        }
    }
}
