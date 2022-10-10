package Circuits.Components.Breadboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Circuits.Components.*;
import Circuits.Components.Component;
import Extensions.HelperMethods.Misc;
import Extensions.ReschedulableTimer;
import Extensions.RotatedIcon;
import Units.Metric.StandardNum;

import static Extensions.Constants.NUM_DIRECTIONS;
import static Extensions.HelperMethods.Misc.GetMergedImage;

public class Breadboard {


    private JPanel panel;
    private int rotation = 0;
    private int height = 0;
    private int width = 0;

    private static Component selectedComponent;
    public static void SetSelectedComponent(Component c){
        selectedComponent = c;
    }
    public static Component GetSelectedComponent(){
        // make new instance of selected comp
        //TODO: improve this from a rote, crappy switch block when I get a better idea
        if(selectedComponent instanceof Wire) return new Wire();
        if(selectedComponent instanceof VoltageSource) return new VoltageSource(GetComponentValue());
        if(selectedComponent instanceof Resistor) return new Resistor(GetComponentValue());
        return null;
    }
    private static StandardNum GetComponentValue(){
        // for now... default?
        return null;
    }


    private String status = "";
    private long statusKeepAlive = 2000;
    ReschedulableTimer timer = new ReschedulableTimer();

    public void UpdateStatus(String newStatus){
        status = newStatus;
        statusArea.setText(status);


        if(timer.isRunning){
            timer.reschedule(statusKeepAlive);
        }
        else
            timer.schedule(()-> {
                UpdateStatus("");
            }, statusKeepAlive);

    }

    public JTextArea statusArea;

    public void IncrementRotation(){
        rotation = (rotation + 1) % NUM_DIRECTIONS;

        System.out.println(rotation);
    }

    public void DecrementRotation(){
        if(rotation == 0)
            rotation = rotation + NUM_DIRECTIONS;

        rotation = (rotation - 1) % NUM_DIRECTIONS;

        System.out.println(rotation);
    }

    public void createGrid(JPanel panel, int width, int height){
        this.panel = panel;
        this.height = height;
        this.width = width;


        GridLayout g = new GridLayout(height, width);


        panel.setLayout(g);

        for(int i = 0; i < height; i++){
            for(int k = 0; k < width; k++) {
                BreadboardGridUnit gridUnit = new BreadboardGridUnit();
                gridUnit.addMouseListener(new ClickListener(gridUnit));
                panel.add(gridUnit);
            }
        }
    }

    public void getGrid(){
        int counter = 0;

        for(java.awt.Component c : panel.getComponents()){
            BreadboardGridUnit[][] breadboardGridUnits = new BreadboardGridUnit[width][height];
        // just need to put it in our grid!
            BreadboardGridUnit gridUnit = (BreadboardGridUnit)c;
            int x = counter % width;
            int y = counter / width;
            breadboardGridUnits[x][y] = gridUnit;
            if(gridUnit.component != null){
                System.out.println("Found component of type " + gridUnit.component.toString() + " at coordinate (" + x + ", " + y + ").");

            }
            counter++;
        }
        System.out.println(counter);
    }

    public class BreadboardGridUnit extends JPanel {


        private static int size = 20;
        private Color backgroundColor;

        private Circuits.Components.Component component;
        private JLabel imageLabel;

        private BufferedImage currentImage;

        public void StopImagining(){
            if(component == null){
                currentImage = null;
                SetImage(null);
            }
            else{
                ImageIcon img = new ImageIcon(component.GetImage());
                SetImage(img);
            }
        }

        public void ImagineComponent(Circuits.Components.Component c, int rotation){
            if(c == null)
                return;
            if(component != null){
                // if we already have an extant component, we should just ask it to imagine itself + rotation
                    SetImage(new ImageIcon(component.GetWhatIfImage(rotation))); // hack just to see how we go
            }
            else{
                SetImage(new ImageIcon(c.GetWhatIfImage(rotation)));
            }
        }



        public void SetComponent(Circuits.Components.Component c, int rotation){
            if(c == null) {
                SetImage(null);
                component = null;
                return;
            }

            if(component == null)
                component = c;

            if(component != null){
                component.PlaceComponent(rotation, () -> {
                    UpdateStatus("Successfully placed component!");
                    currentImage = component.GetImage();
                    ImageIcon icon = new ImageIcon(currentImage);
                    SetImage(icon);
                }, () -> {
                    UpdateStatus("Failed to place component.");
                });
            }
            else {
                c.PlaceComponent(rotation, () -> {
                    UpdateStatus("Successfully placed component!");
                    currentImage = c.GetImage();
                    ImageIcon icon = new ImageIcon(currentImage);
                    SetImage(icon);
                }, () -> {
                    UpdateStatus("Failed to place component.");
                });
            }
        }

        public Circuits.Components.Component GetComponent(){
            return component;
        }

        private void SetImage(ImageIcon i){
            if(i == null) {
                imageLabel.setIcon(null);
                return;
            }
            i.setImage(i.getImage().getScaledInstance(50,50, Image.SCALE_AREA_AVERAGING));
            imageLabel.setIcon(i);
        }

        // links used for learning! https://stackoverflow.com/questions/4871051/how-to-get-the-current-working-directory-in-java
        public BreadboardGridUnit(){
            this.setBorder(BorderFactory.createLineBorder(new Color(150,150,150)));
            this.backgroundColor = Color.lightGray;
            FlowLayout layout = new FlowLayout();
            layout.setHgap(0);
            layout.setVgap(0);
            super.setLayout(layout);
            imageLabel = new JLabel();
            imageLabel.setMinimumSize(new Dimension(50,50));

            super.add(imageLabel);
        }


        public Color getBackgroundColor() {
            return backgroundColor;
        }
        public void setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(getBackgroundColor());
            g.fillRect(0, 0, getWidth(), getHeight());

            //g.drawImage(image,0,0,this);
        }
    }



    public class ClickListener extends MouseAdapter {
        private BreadboardGridUnit gridUnit;
        private MotionListener motionListener;

        private class MotionListener extends MouseMotionAdapter{

            @Override
            public void mouseMoved(MouseEvent event){
                // mouse moving over tile, begin/maintain what-if imagery
                //UpdateStatus("I'm sensing a thing at " + event.getPoint().x + ", " + event.getPoint().y);
                gridUnit.ImagineComponent(GetSelectedComponent(),GetRotationByPoint(event.getX(),event.getY()));
            }

        }

        public ClickListener(BreadboardGridUnit _gridUnit){
            gridUnit = _gridUnit;
            motionListener = new MotionListener();
            gridUnit.addMouseMotionListener(motionListener);
        }

        @Override
        public void mouseExited(MouseEvent event){
            // mouse no longer over tile, stop what-if imagery
            gridUnit.StopImagining();
        }

        private int GetRotationByPoint(int x, int y){
            double pointSum = x + y;
            double gridSum = (gridUnit.getHeight() + gridUnit.getWidth()) / 2;

            Boolean sumA = (pointSum > gridSum);

            if(x > y)
                if(sumA)
                    return 1;
                else
                    return 0;
            else {
                if(sumA)
                    return 2;
                else
                    return 3;
            }
        }

        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                // get the quadrant (in triangles, 0 = top, 1 = right, 2 = bot, 3 = left) of the mouse location relative to the grid unit

                String positionMessage = "";
                Point clickPoint = event.getPoint();

                int rotation = GetRotationByPoint(clickPoint.x,clickPoint.y);

                UpdateStatus(positionMessage);

                gridUnit.SetComponent(GetSelectedComponent(), rotation);
                //System.out.println("I want to set the grid unit component to " + selectedComponent);
            }
        }
    }

    public Breadboard(JTextArea statusArea){
        this.statusArea = statusArea;
    }
}
