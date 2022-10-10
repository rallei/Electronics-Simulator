package Circuits.Components;

import javax.swing.*;
import java.awt.*;

public interface VisualComponentInterface {
    /*
     * what do our components do?
     * take in some quantity of voltage or current... current is based on the voltage supplied divided by total resistance for instance.
     * doing something with that quantity (such as generating heat, light, etc.) Do(x) where X may be specific to that circuit?
     *
     */

    //public ComponentType componentType = null;

    public ComponentPlacementPoints[] getDefaultConnectionPoints();
}
