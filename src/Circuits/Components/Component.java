package Circuits.Components;

import Extensions.RotatedIcon;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import static Extensions.Constants.NUM_DIRECTIONS;
import static Extensions.HelperMethods.Misc.*;

public abstract class Component implements VisualComponentInterface{

    /*      next steps:
    *
    *   build the image in Component class
    *   --> use GetBufferedImage to get image by name
    *   --> do some kind of math w/ component size (occupies (0,2) or say, just (0) or just(1))
    *   --> use result to build image and send that to breadboard grid unit to display
    *
    *
    *
    * */

    private BufferedImage GetBufferedImage(){
        //System.out.println("I want to get the image at " + this.getClass().getSimpleName() + ".png");

        //start by grabbing BufferedImage
        BufferedImage i = GetBufferedImageByName(this.getClass().getSimpleName() + ".png");

        return i;
    }

    public BufferedImage GetWhatIfImage(int rotation){
        // imagine yourself + a new addition
        BufferedImage base = GetImage();
        if(base != null)
            return GetMergedImage(base,GetRotatedImage(GetBufferedImage(),Double.valueOf(rotation)));
        else
            return GetRotatedImage(GetBufferedImage(),Double.valueOf(rotation));
    }

    public BufferedImage GetImage() {
        BufferedImage componentImage = GetBufferedImage();
        ComponentPlacementPoints[] defaultPoints = getDefaultConnectionPoints();
        //int maxComponents = activeConnectionPoints.length / defaultPoints.length; // 2 for a resistor, 4 for a wire.
        if(this.getClass().equals(Wire.class)){
            // then for each connection point, add & merge
            BufferedImage base = null;
            for(int i = 0; i < 4; i++){
                if(CheckIsPlaced(defaultPoints,i)){
                    if(base == null)
                        base = GetRotatedImage(componentImage, Double.valueOf(i));
                    else{
                        // add to what already exists
                        base = GetMergedImage(base,GetRotatedImage(componentImage,Double.valueOf(i)));
                    }
                }
            }
            return base;
        }
        else{
            // just return the image with its rotation
            for(int i = 0; i < 4; i++){
                if(CheckIsPlaced(defaultPoints,i)){
                    return GetRotatedImage(componentImage, Double.valueOf(i));
                }
            }
        }

        // if all has failed... let it explode
        return null;

        /*
         * how to go about this?
         * we could say.. okay.. 0 and 2 are defaults. subtract that from possibilities, we get 1 and 3 left.
         * but then we still need to calc how many components can fit in those spots. divide by length?
         */
    }

    /*
     * thoughts for component use in our GUI:
     * wires can connect IN at any of four points. there is no point to make a distinction between IN and OUT.
     * a 1k Ohm resistor whose IN terminal is at the bottom and whose OUT terminal is at the top will function the same if those are reversed
     * that resistor will have 1k Ohms of resistance regardless of the direction of the current flowing through it.
     *
     * it makes more sense (and simplifies things) to say that the resistor simply exists from the bottom to top
     * (or left to right side) of the tile in the grid layout (breadboard) where it exists.
     *
     */

    //public int rotation = 0; // a number between 0-3 representing the rotation of the whole component

    private Boolean[] activeConnectionPoints = new Boolean[]{ false, false, false, false }; // points at which the component 'exists'

    public void ClearActiveConnections(){
        for(Boolean connectionPoint: activeConnectionPoints)
            connectionPoint = false;
    }

    public void PlaceComponent(int rotation, Runnable onSuccess, Runnable onFail){
        //sets that the component exists at specified rotation
        SetPlacementPoints(getDefaultConnectionPoints(),rotation, onSuccess, onFail);
    }

    public void SetPlacementPoints(ComponentPlacementPoints[] points, int rotation, Runnable onSuccess, Runnable onFail){
        if(!CheckCanPlace(points,rotation))
        {
            onFail.run();
            return;
        }
        for(ComponentPlacementPoints point: points) {
            System.out.println("I'm in ur kitchen, settin ur stuff to true");
            activeConnectionPoints[(point.direction + rotation) % NUM_DIRECTIONS] = true;
        }
        onSuccess.run();
    }

    public Boolean CheckCanPlace(ComponentPlacementPoints[] points, int rotation){
        for(ComponentPlacementPoints point: points) {
            if(activeConnectionPoints[(point.direction + rotation) % NUM_DIRECTIONS] == true) {
                return false;
            }
        }
        return true;
    }

    public Boolean CheckIsPlaced(ComponentPlacementPoints[] points, int rotation){
        for(ComponentPlacementPoints point: points) {
            if(activeConnectionPoints[(point.direction + rotation) % NUM_DIRECTIONS] == false) {

                return false;
            }
        }
        return true;
    }

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
