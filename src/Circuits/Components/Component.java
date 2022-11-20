package Circuits.Components;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Extensions.HelperMethods.Misc.*;

public abstract class Component implements VisualComponentInterface{

    public static class ComponentData{
        public String someData = "data";
        //public ComponentConnections connections = new ComponentConnections(false, false, false, false);
    }

    public ComponentData data = new ComponentData();

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
    //TODO: rewrite image getting code to ignore rotation and instead use componentconnections
    private BufferedImage GetBufferedImage(){
        //System.out.println("I want to get the image at " + this.getClass().getSimpleName() + ".png");

        //start by grabbing BufferedImage
        BufferedImage i = GetBufferedImageByName(this.getClass().getSimpleName() + ".png");

        return i;
    }

    public BufferedImage GetWhatIfImage(Rotation rotation){
        // imagine yourself + a new addition
        BufferedImage base = GetImage();
        if(base != null)
            return GetMergedImage(base,GetRotatedImage(GetBufferedImage(), (double) rotation.GetValue()));
        else
            return GetRotatedImage(GetBufferedImage(), (double) rotation.GetValue());
    }

    private BufferedImage AddToImage(BufferedImage base, BufferedImage addition){
        if(base == null)
            return addition;

        return GetMergedImage(base,addition);
    }
    public BufferedImage GetImage() {
        BufferedImage componentImage = GetBufferedImage();

        if(mainPivot >= 0)
            return GetRotatedImage(GetBufferedImage(),mainPivot);

        BufferedImage base = null;
        if(connections.top.isConnected)
            base = AddToImage(base,GetRotatedImage(GetBufferedImage(),connections.top.point.GetValue()));

        if(connections.right.isConnected)
            base = AddToImage(base,GetRotatedImage(GetBufferedImage(),connections.right.point.GetValue()));

        if(connections.bot.isConnected)
            base = AddToImage(base,GetRotatedImage(GetBufferedImage(),connections.bot.point.GetValue()));

        if(connections.left.isConnected)
            base = AddToImage(base,GetRotatedImage(GetBufferedImage(),connections.left.point.GetValue()));

        // if all has failed... let it explode
        return base;

        /*
         * how to go about this?
         * we could say.. okay.. 0 and 2 are defaults. subtract that from possibilities, we get 1 and 3 left.
         * but then we still need to calc how many components can fit in those spots. divide by length?
         */
    }

    private int mainPivot = -1; // 0 = top to bot; 2 = bot to top; 1 = right to left; 3 = left to right

    public int Direction(){
        return mainPivot;
    }
    // debugging notes: enum types in java cannot be instantiated (instanced). If i set enum values to true somewhere, i set them true for everywhere.
    public ComponentConnections connections = new ComponentConnections(false, false, false, false);
    public void ClearActiveConnections(){
        connections.Clear();
    }

    public Point position;

    public void PlaceComponent(Rotation rotation, Point position, Runnable onSuccess, Runnable onFail){
        if(!connections.Add(getConnectionPoints(),rotation))
        {
            onFail.run();
            return;
        }

        // TODO: the below code limits to one placed component and sets pivot to the clicked location (e.g. if clicked at top, it's facing top). idk... it's a little bit of a messy way to do this but we are only going to want one component per grid unit (except for wires... for now)
        if(!(this instanceof Wire)) {
            //connections.Clear();
            mainPivot = rotation.GetValue();
        }
        this.position = position;
        onSuccess.run();
    }

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
