package Circuits.Components.Breadboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import Circuits.Circuit;
import Circuits.Components.*;
import Circuits.Components.Component;
import Extensions.ReschedulableTimer;
import Units.Metric.StandardNum;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

public class Breadboard {


    private JPanel panel;
    private int rotation = 0;
    private int height = 0;
    private int width = 0;

    private BreadboardGridUnit[][] mainGrid;

    private static Component selectedComponent;

    public static void SetSelectedComponent(Component c) {
        selectedComponent = c;
    }

    public static Component GetSelectedComponent() {
        // make new instance of selected comp
        //TODO: improve this from a rote, crappy switch block when I get a better idea
        if (selectedComponent instanceof Wire) return new Wire();
        if (selectedComponent instanceof VoltageSource) return new VoltageSource(GetComponentValue());
        if (selectedComponent instanceof Resistor) return new Resistor(GetComponentValue());
        return null;
    }

    private static StandardNum GetComponentValue() {
        // for now... default?
        return null;
    }

    //region:Status Area
    private String status = "";
    private long statusKeepAlive = 2000;
    ReschedulableTimer timer = new ReschedulableTimer();

    public void UpdateStatus(String newStatus) {
        status = newStatus;
        statusArea.setText(status);


        if (timer.isRunning) {
            timer.reschedule(statusKeepAlive);
        } else
            timer.schedule(() -> {
                UpdateStatus("");
            }, statusKeepAlive);

    }

    public JTextArea statusArea;

    //endregion
    public void createGrid(JPanel panel, int width, int height) {
        this.panel = panel;
        this.height = height;
        this.width = width;


        GridLayout g = new GridLayout(height, width);


        panel.setLayout(g);

        for (int i = 0; i < height; i++) {
            for (int k = 0; k < width; k++) {
                BreadboardGridUnit gridUnit = new BreadboardGridUnit(new Point(i, k));
                gridUnit.addMouseListener(new ClickListener(gridUnit));
                panel.add(gridUnit);
            }
        }

        mainGrid = getGrid(); // it's kinda dumb to do it this way but we can fix this later
    }

    public static class ClassedComponent{

        //ugly and hacked together, i'll admit. in C# I would be saving and loading with reflection, which I know Java has but...
        public Wire wireData;
        public VoltageSource voltageData;
        public Resistor resistorData;
        public String componentType;

        public ClassedComponent(){
            wireData = null;
            voltageData = null;
            resistorData = null;
            componentType = null;
        }

    }

    String saveFileName = "circuit.save";
    public void SaveCircuit(){
        // probably use json or something to save our grid matrix to file
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {

            ClassedComponent[][] data = new ClassedComponent[mainGrid.length][mainGrid[0].length];// Map<Component,String>[mainGrid.length][mainGrid[0].length];
            for(int i = 0; i < mainGrid.length; i++)
                for(int k = 0; k < mainGrid[i].length; k++) {
                    data[i][k] = new ClassedComponent();
                    var unit = mainGrid[i][k];
                    if(unit.component instanceof Wire){
                        data[i][k].wireData = (Wire)unit.component;
                    } else
                    if(unit.component instanceof VoltageSource) {
                        data[i][k].voltageData = (VoltageSource)unit.component;
                    } else
                    if(unit.component instanceof Resistor){
                        data[i][k].resistorData = (Resistor)unit.component;
                    }
                }


            String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            /*
            for(int i = 0; i < mainGrid.length; i++)
                for(int k = 0; k < mainGrid[i].length; k++)
                    if (mainGrid[i][k].component != null)
                        System.out.println("Component at (" + i + "," + k + ")");

             */
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter(saveFileName));
                writer.write(jsonResult);
                writer.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void LoadCircuit(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(saveFileName));
            String json = reader.lines().collect(Collectors.joining()); // this is lovely
            reader.close();
            try{
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);


                TypeReference<ClassedComponent[][]> typeRef = new TypeReference<>() {
                };

                ClassedComponent[][] data = objectMapper.readValue(json,typeRef);

                for(int i = 0; i < mainGrid.length; i++)
                    for(int k = 0; k < mainGrid[i].length; k++)
                    {
                        //System.out.println(data[i][k] instanceof Wire);
                        if(data[i][k].wireData != null)
                            mainGrid[i][k].RestoreFromSave(data[i][k].wireData);
                        else
                        if(data[i][k].voltageData != null)
                            mainGrid[i][k].RestoreFromSave(data[i][k].voltageData);
                        else
                        if(data[i][k].resistorData != null)
                            mainGrid[i][k].RestoreFromSave(data[i][k].resistorData);
                    }
            }
            catch (JsonProcessingException e){
                e.printStackTrace();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //region:Circuit Analysis
    public void AnalyzeCircuit() {
        BreadboardGridUnit[][] grid = getGrid();
        Point startPoint = FindStartPoint(grid);
        //System.out.println(startPoint);


        //grid[startPoint.x][startPoint.y].component.connections.PrintActiveConnections();
        //GetNode(GetComponentAtPoint(startPoint),null,0,null);
        //FindPath(grid, startPoint, null,Rotation.TOP, new Stack<>());
        //SolveCircuit(null, startPoint, initializeVisitedArray(), new LinkedList<Point>());
    }

    public BreadboardGridUnit[][] getGrid() {
        int counter = 0;
        BreadboardGridUnit[][] breadboardGridUnits = new BreadboardGridUnit[width][height];

        for (java.awt.Component c : panel.getComponents()) {
            // just need to put it in our grid!
            BreadboardGridUnit gridUnit = (BreadboardGridUnit) c;
            int x = counter % width;
            int y = counter / width;
            breadboardGridUnits[x][y] = gridUnit;
            if (gridUnit.component != null) {
                //System.out.println("Found component of type " + gridUnit.component.toString() + " at coordinate (" + x + ", " + y + ").");

            }
            counter++;
        }
        System.out.println(counter);
        return breadboardGridUnits;
    }

    public Point FindStartPoint(BreadboardGridUnit[][] matrix) {
        // iterate over array and find voltage source, return that as start
        for (int i = 0; i < matrix.length; i++)
            for (int k = 0; k < matrix[i].length; k++)
                if (matrix[i][k].component instanceof VoltageSource) {
                    return new Point(i, k);
                }
        return null;
    }


    private Boolean AlreadyVisited(Point point, Stack<Point> points) {
        return points.contains(point);
    }

    private Boolean IsJunction(ComponentConnections connections) {
        int counter = 0;
        int junctionThreshold = 3;
        for (ComponentConnections.ConnectionPoint point : connections.points) {
            if (point.isConnected)
                counter++;
        }
        return counter >= junctionThreshold;
    }

    //overloading the method for readability

    private Boolean IsJunction(Component component) {
        return IsJunction(component.connections);
    }

    /*
     excuse me while I think!
     we want to follow the connections from each component
     when we get to a junction, we want to record things in between until we get to another junction
     then, if a-> and b-> c, a->c; if c->d, a->d
     */

    // some constants for visited array initialization and checking
    final int unvisited = -1;
    final int inaccessible = -2;

    int[][] initializeVisitedArray() {
        int[][] visitedArray = new int[mainGrid.length][mainGrid[0].length];
        for (int i = 0; i < visitedArray.length; i++)
            for (int j = 0; j < visitedArray[i].length; j++) {
                if (mainGrid[i][j].component != null) {
                    visitedArray[i][j] = unvisited;
                } else {
                    visitedArray[i][j] = inaccessible;

                }
            }
        return visitedArray;
    }

    void PrintArray(int[][] array) {

        for (int i = 0; i < array[0].length; i++) {

            System.out.println(); // spacing line
            for (int j = 0; j < array.length; j++) {

                // feeling lazy and just want to see this work, brute force time
                String spacing = "";
                if (array[j][i] < 0)
                    spacing = "  ";
                else if (array[j][i] >= 0 && array[j][i] < 10) {
                    spacing = "  ";
                } else if (array[j][i] >= 10 && array[j][i] < 100) {
                    spacing = " ";
                } else {
                    spacing = "";
                }

                if (array[j][i] < 0)
                    System.out.print(spacing + "X");
                else
                    System.out.print(spacing + array[j][i]);
            }
        }
    }

    public void SolveCircuit(Component component) {
        /* It may be that this is simply a refinement of other ideas I have had, but I will still give this method its own space.
         *
         */

        // intended to store objects of type component and type node, from which we can grab values such as resistance, etc.
        // the list represents a complete ordering of the circuit
        LinkedList<Object> OrderedList = new LinkedList<>();

        Component nextComponent = NextInPath(null,null);

        if (IsJunction(nextComponent)) {

            Node nextNode = GetNode();

            if (nextNode != null) {
                OrderedList.add(nextNode);
                // set nextComponent to the endpoint of the node we found
                nextComponent = nextNode.endComponent;
            } else {
                System.out.println("I hit a junction on the main path, but it wasn't a node... I'm not sure what to do!");
                return;
            }
        } else {
            OrderedList.add(nextComponent);
        }

        //SolveCircuit(NextInPath(nextComponent));
    }

    public Component NextInPath(Point fromPosition, int[][] visited) {
        // code to determine what comes next in the path
        // get all points within reach of the current position
        ArrayList<Point> reachablePoints = ReachablePoints(fromPosition, visited);

        //reachablePoints.get(0)
        return GetComponentAtPoint(reachablePoints.get(0));
    }


    public Node GetNode() {


        return null;
    }

    public void SolveCircuit2(Point lastPosition, Point currentPosition, int[][]visited, Queue<Point> queue){
        /*
         * Here's to another idea to test..!
         *
         * so here's the idea this time. grab a list of all junctions, then test pairs..
         * if you can loop from one to the other and back again, it's a node
         * for nodes with >2 branches, we need either need memory to travel only to places we haven't been or we call findpath
         * on all branches at a junction; if they all converge on the same junction on the other side, it's a node
         */

        if(currentPosition == null){
            System.out.println("Must be finished..!");
            PrintArray(visited);
            return;
        }

        if(lastPosition == null)
            lastPosition = currentPosition;

        // get all points within reach of the current position
        ArrayList<Point> reachablePoints = ReachablePoints(currentPosition, visited);


        Point nextPoint = null;

        // if we can travel to multiple locations from this one...
        if(reachablePoints.size() > 1){

            // set next point to an adjacent available tile
            nextPoint = reachablePoints.get(0);

            // if the turnQueue is NOT empty and the next item in the queue is NOT where we just came from...
            // note: it will still get stuck if it's bouncing around from >2 junctions with this setup
            if(!queue.isEmpty() && queue.peek() != lastPosition){
                nextPoint = queue.remove(); // head back to site of last turn
            }

            // add this position to the queue regardless, because it had multiple paths and we need to evaluate them all
            if(lastPosition != currentPosition)
                queue.add(currentPosition);

        }
        else if(reachablePoints.size() == 1){
            nextPoint = reachablePoints.get(0);
        }
        else{
            if(!queue.isEmpty())
                nextPoint = queue.remove();
            else
                System.out.println("I'm not sure what to do chief..! I have no turns to go to, and no free tiles to travel to!");
        }
        // step count
        int nextCount = visited[lastPosition.x][lastPosition.y] + 1;
        int currentCount = visited[currentPosition.x][currentPosition.y];
        if(currentCount < 0 || currentCount > nextCount)
            visited[currentPosition.x][currentPosition.y] = nextCount;
        System.out.println("Setting " + currentPosition.x + "," + currentPosition.y + " to " + nextCount);

        // set last to current
        lastPosition = currentPosition;
        //SolveCircuit(lastPosition,nextPoint,visited,queue);

    }

    ArrayList<Point> ReachablePoints (Point currentPosition, int[][] visitedArray){
        ArrayList<Point> reachables = new ArrayList<>();

        Point[] adjacentPoints = new Point[]{
                new Point(currentPosition.x - 1, currentPosition.y),
                new Point(currentPosition.x + 1, currentPosition.y),
                new Point(currentPosition.x, currentPosition.y - 1),
                new Point(currentPosition.x, currentPosition.y + 1),
        };

        for(Point p : adjacentPoints){
            if(!Visited(p,visitedArray) && CanReach(currentPosition,p))
                reachables.add(p);
        }

        return reachables;
    }

    Boolean Visited(Point p, int[][] visitedArray){
        if(visitedArray[p.x][p.y] >= 0)
            return true;

        return false;
    }

    Boolean CanReach(Point currentPosition, Point toReach){
        // need to check if both components are connected to one another
        Point difference = new Point(currentPosition.x - toReach.x, currentPosition.y - toReach.y);
        // if it's -1, 0 then check toReach.left connected to currentPosition.right
        if(difference.x < 0 && GetComponentAtPoint(currentPosition).connections.right.isConnected && GetComponentAtPoint(toReach).connections.left.isConnected)
            return true;
        else if (difference.x > 0 && GetComponentAtPoint(currentPosition).connections.left.isConnected && GetComponentAtPoint(toReach).connections.right.isConnected)
            return true;
        else if (difference.y < 0 && GetComponentAtPoint(currentPosition).connections.bot.isConnected && GetComponentAtPoint(toReach).connections.top.isConnected)
            return true;
        else if (difference.y > 0 && GetComponentAtPoint(currentPosition).connections.top.isConnected && GetComponentAtPoint(toReach).connections.bot.isConnected)
            return true;

        return false; // if all other checks failed, these two points are not connected
    }


    class Node{
        //class may be moved in future, being used here to test new pathfinding idea
        Component startComponent; // junction where the node begins
        Component endComponent;   // junction where the node ends (NumBranchesOut = NumBranchesIn)

        // some kind of way to store what's in between start and end...
        // in C# I'd say a list of Object, where it can be comp-> comp -> comp -> Node -> comp, comp... etc.
        // but there can be more than one path, really up to three (one in, three out)
        ArrayList<Object> pathUp = new ArrayList<>();
        ArrayList<Object> pathRight = new ArrayList<>();
        ArrayList<Object> pathDown = new ArrayList<>();
        ArrayList<Object> pathLeft = new ArrayList<>();

        public void AddPath(Rotation r, ArrayList<Object> path){
            switch(r.GetDirection(r.GetValue())){
                case TOP: pathUp = path;
                case RIGHT: pathRight = path;
                case BOT: pathDown = path;
                case LEFT: pathLeft = path;
            }
        }

        public Boolean PathsEmpty(){
            return (pathUp.isEmpty() && pathRight.isEmpty() && pathDown.isEmpty() && pathLeft.isEmpty());
        }

        public int NumBranchesOut(){
            return startComponent.connections.GetNumConnections() - 1; // all connections, minus 1 which is IN
        }

        public Node(Component startingComponent){
            startComponent = startingComponent;
        }
    }

    /*
    public Node GetNode(Component startComponent, Component currentComponent, int junctionsSeen, Node node){
        // simply going to use a component.

        if(node == null){
            node = new Node(startComponent);
            currentComponent = startComponent;
            junctionsSeen = startComponent.connections.GetNumConnections() - 1; // total connections - 1 (cause 1 goes in)
        }

        for(ComponentConnections.ConnectionPoint point : currentComponent.connections.points){
            //travel in each direction available
            Rotation localPointDirection = point.point.GetDirection(point.point.GetValue());

            // if this branch is not going against the direction of the component
            if(point.isConnected && localPointDirection != GetFlippedRotation(Rotation.GetDirection(currentComponent.Direction()))){
                System.out.println("I want to get the path: " + localPointDirection);
                //get the path along this branch
                node.AddPath(localPointDirection, GetPath(node.startComponent, node.startComponent.connections.GetNumConnections(),localPointDirection, null));
            }
        }

        return node;
    }
    */
    public ArrayList<Object> GetPath(Component currentComponent, int junctionsIn, Rotation r, ArrayList<Object> path){
        // follow along path, can call getNode
        if(path == null) {
            path = new ArrayList<>();
        }

        if(!IsJunction(currentComponent.connections))
            path.add(currentComponent);
        else if(currentComponent.connections.GetNumConnections() - 1 == junctionsIn) {
            // temporary simplifying element: if the num connections in the junction == junctions in, let's just say we finished the path
            //System.out.println("We will want to call GetNode() here..!");
            System.out.println("Reached the end of the path...");
            path.add(currentComponent);
            return path;
        }
        // check for the next point
        Point nextPoint = GetPoint(r,currentComponent.position);
        System.out.println(nextPoint);
        GetPath(GetComponentAtPoint(nextPoint), junctionsIn, r, path);

        return null;
    }

    public void Travel(){
        // given a point, travel to the next?
    }

    public Component GetComponentAtPoint(Point p){
        return mainGrid[p.x][p.y].component;
    }

    public Component GetNext(Component current, Component last){
        System.out.println("I'll return a component later, man!");
        return null;
    }

    public void FindPath(BreadboardGridUnit[][] matrix, Point startPoint, Point curPoint, Rotation ofNext, Stack<Point> junctions){


        if(curPoint == null) {
            ofNext = GetFlippedRotation(ofNext.GetDirection(matrix[startPoint.x][startPoint.y].component.Direction()));
            curPoint = new Point(startPoint.x, startPoint.y);

        } // init code
        else if (curPoint.equals(startPoint)) {
            System.out.print(curPoint + ".");

            while(!junctions.isEmpty())
                System.out.println("Junction at " + junctions.pop());

            System.out.println("\n... and everyone lived happily ever after.");
            return;
        } // finish code
;
        Component current = matrix[curPoint.x][curPoint.y].component;

        if(current instanceof Wire){
            if(!AlreadyVisited(curPoint,junctions)) {
                // can we just call getnode here?
                if(IsJunction(current.connections)) {
                    junctions.add(curPoint);
                    //GetNode(current,null,current.connections.GetNumConnections() -1, null);
                }

                for (ComponentConnections.ConnectionPoint point : current.connections.points) {
                    if (point.isConnected && point.point != ofNext) {

                        System.out.print(curPoint + "-> ");
                        // we could use a stack of junctions, maybe. like... i see A, i add it to my stack, then i see B, i add it, i see C and i add it,
                        // then i find i'm back at A.
                        // then, i take branches between A and B as parallel.

                        FindPath(matrix, startPoint, GetPoint(point.point, curPoint), GetFlippedRotation(point.point), junctions);
                    }
                }
            }
        }
        else {

            System.out.print(curPoint + "-> ");

            // we know that we are looking at a component that connects in one direction to the next in the chain
            FindPath(matrix, startPoint, GetPoint(GetFlippedRotation(ofNext),curPoint),ofNext,junctions);
        }

    }

    public void FindBranches(){

    }

    private Rotation GetFlippedRotation(Rotation rotation){
        return rotation.GetDirection((rotation.GetValue() + 2) % 4);
    }

    private Point GetPoint(Rotation rotation, Point p) {
        switch (rotation) {
            case TOP: return new Point(p.x, p.y - 1);
            case RIGHT: return new Point(p.x + 1, p.y);
            case BOT: return new Point(p.x, p.y + 1);
            case LEFT: return new Point(p.x - 1, p.y);

        }
        System.out.println("Hmm, this isn't right!");
        return p;
    }
    /*
    public void FindBranches (BreadboardGridUnit[][] matrix, Point startPoint, Point curPoint, ComponentConnections lastPoint){
        // polarity will decide if we want to go right or left/top or down with respect to our start pt
        int polarity = 1; // change to -1 to go backwards
        if(curPoint == null)
            curPoint = startPoint;
        else if (curPoint == startPoint) {
            System.out.println("Done!");
        }
        // direction tells us where we came from
        // ex: lastpoint = 2, we have a vert component, so next is top. we would recursively call this method for each
        // direction; in a series circuit, we won't need to worry about these branching paths
        // but if our last is 2, then we go UP to next comp, and last is 2 :)
        System.out.println("Trying to get component at " + curPoint.toString() + ". Gridunit.component is " + matrix[curPoint.x][curPoint.y]);

        ComponentPlacementPoints[] nextPoints = matrix[curPoint.x][curPoint.y].component.GetNextPoints(lastPoint);

        System.out.println(matrix[curPoint.x][curPoint.y]);
        for(ComponentPlacementPoints point : nextPoints){
            if(point.direction == 0) {
                curPoint.y--;
                lastPoint.direction = 2;
            }
            else if (point.direction == 1) {
                curPoint.x++;
                lastPoint.direction = 3;
            } else if (point.direction == 2) {
                curPoint.y--;
                lastPoint.direction = 0;
            } else if (point.direction == 3) {
                curPoint.x--;
                lastPoint.direction = 1;
            }
        }


        FindBranches(matrix,startPoint,curPoint,lastPoint);

    }
    */
    //endregion
    /* next step after we have our grid is to look for the start point
    *
    *
    */
    public class BreadboardGridUnit extends JPanel {

        /*
        @JsonDeserialize(builder = Data.class)
        @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
        public class Data{
            public Circuits.Components.Component component;

            public Data build(){
                return new Data();
            }

            public Data(Data data){
                component = data.component;
            }

            public Data(){

            }
        }
        */

        private static int size = 20;
        private Color backgroundColor;

        private Circuits.Components.Component component;
        private JLabel imageLabel;

        private BufferedImage currentImage;
        private Point position;

        public void StopImagining(){
            if(component == null){
                ClearImage();
            }
            else{
                SetImage(component.GetImage());
            }
        }

        public void ImagineComponent(Circuits.Components.Component c, Rotation rotation){
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

        public void RestoreFromSave(Circuits.Components.Component c){
            component = c;
            SetImage(component.GetImage());
        }

        public void SetComponent(Circuits.Components.Component c, Rotation rotation){
            if(c == null) {
                ClearImage();
                component = null;
                return;
            }

            if(component == null) {
                component = c;
            }

            component.PlaceComponent(rotation, position, () -> {
                UpdateStatus("Successfully placed component!");
                SetImage(component.GetImage());
            }, () -> {
                UpdateStatus("Failed to place component.");
            });
            //component = c;
        }

        public Circuits.Components.Component GetComponent(){
            //System.out.println(component + "?");
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

        private void SetImage(BufferedImage i){
            if(i == null) {
                imageLabel.setIcon(null);
                return;
            }

            currentImage = i;
            ImageIcon icon = new ImageIcon(currentImage);
            icon.setImage(icon.getImage().getScaledInstance(50,50, Image.SCALE_AREA_AVERAGING));
            imageLabel.setIcon(icon);
        }

        private void ClearImage(){
            imageLabel.setIcon(null);
            currentImage = null;
        }

        // links used for learning! https://stackoverflow.com/questions/4871051/how-to-get-the-current-working-directory-in-java
        public BreadboardGridUnit(Point position){
            this.setBorder(BorderFactory.createLineBorder(new Color(150,150,150)));
            this.backgroundColor = Color.lightGray;
            FlowLayout layout = new FlowLayout();
            layout.setHgap(0);
            layout.setVgap(0);
            super.setLayout(layout);
            imageLabel = new JLabel();
            imageLabel.setMinimumSize(new Dimension(50,50));
            this.position = position;
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

        private Rotation GetRotationByPoint(int x, int y){
            double pointSum = x + y;
            double gridSum = (gridUnit.getHeight() + gridUnit.getWidth()) / 2;

            Boolean sumA = (pointSum > gridSum);

            if(x > y)
                if(sumA)
                    return Rotation.RIGHT;
                else
                    return Rotation.TOP;
            else {
                if(sumA)
                    return Rotation.BOT;
                else
                    return Rotation.LEFT;
            }
        }

        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                // get the quadrant (in triangles, 0 = top, 1 = right, 2 = bot, 3 = left) of the mouse location relative to the grid unit

                String positionMessage = "";
                Point clickPoint = event.getPoint();

                Rotation rotation = GetRotationByPoint(clickPoint.x,clickPoint.y);

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
