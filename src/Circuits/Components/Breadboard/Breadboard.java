package Circuits.Components.Breadboard;

import Circuits.Components.Component;
import Circuits.Components.*;
import Circuits.Node;
import Extensions.HelperMethods.Pathfinding;
import Extensions.ReschedulableTimer;
import Units.Metric.Magnitude;
import Units.Metric.StandardNum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.processing.SupportedSourceVersion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static Extensions.Constants.*;

public class Breadboard {


    private JPanel panel;
    private int rotation = 0;
    private int height = 0;
    private int width = 0;

    private BreadboardGridUnit[][] mainGrid;
    private int[][] visitedArray;

    private static Component selectedComponent;

    public static void SetSelectedComponent(Component c) {
        selectedComponent = c;
    }

    public static Component GetSelectedComponent() {
        // make new instance of selected comp
        //TODO: improve this from a rote, crappy switch block when I get a better idea
        if (selectedComponent instanceof Wire) return new Wire();
        if (selectedComponent instanceof VoltageSource) return new VoltageSource(GetComponentValue());
        if (selectedComponent instanceof Resistor) return new Resistor(new StandardNum(100, Magnitude.NONE));
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

        for (int i = 0; i < width; i++) {
            for (int k = 0; k < height; k++) {
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

        public ClassedComponent(){
            wireData = null;
            voltageData = null;
            resistorData = null;
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
        visitedArray = initializeVisitedArray(); // reset the visited array
        SolveCircuit(startPoint, new LinkedList<>());
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
            gridUnit.position = new Point(x,y); // set position here...
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


    int[][] initializeVisitedArray() {
        int[][] newVisitedArray = new int[mainGrid.length][mainGrid[0].length];
        for (int i = 0; i < newVisitedArray.length; i++)
            for (int j = 0; j < newVisitedArray[i].length; j++) {
                if (mainGrid[i][j].component != null) {
                    newVisitedArray[i][j] = UNVISITED;
                } else {
                    newVisitedArray[i][j] = INACCESSIBLE;

                }
            }
        return newVisitedArray;
    }

    int[][] EmptyVisitedClone(){
        int[][] emptyVisitedClone = new int[mainGrid.length][mainGrid[0].length];
        for (int i = 0; i < emptyVisitedClone.length; i++)
            for (int j = 0; j < emptyVisitedClone[i].length; j++) {
                if (mainGrid[i][j].component != null) {
                    emptyVisitedClone[i][j] = UNVISITED;
                } else {
                    emptyVisitedClone[i][j] = INACCESSIBLE;

                }
            }
        return emptyVisitedClone;
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

    public void SolveCircuit(Point position, LinkedList<Object> OrderedList) {
        /* It may be that this is simply a refinement of other ideas I have had, but I will still give this method its own space.
         *
         */

        // intended to store objects of type component and type node, from which we can grab values such as resistance, etc.
        // the list represents a complete ordering of the circuit

        //LinkedList<Object> OrderedList = new LinkedList<>();

        visitedArray[position.x][position.y] = VISITED;

        Point nextPosition = NextInPath(position,visitedArray);
        System.out.println(nextPosition);

        if(nextPosition == null){
            StandardNum totalResistance = new StandardNum();
            int numNodes = 0;
            int numResistors = 0;
            for(Object obj: OrderedList){
                if(obj instanceof Node) {
                    Node n = (Node)obj;
                    System.out.println("We have a node spanning " + n.startJunction + " to " + n.endJunction + " with resistance: " + n.GetResistance().ToString());
                    totalResistance.Add(n.GetResistance());
                    numNodes++;
                    numNodes += n.GetNumNodes();
                    numResistors += n.GetNumResistors();
                    System.out.println(n.GetNumResistors());
                }
                else if (obj instanceof Resistor){
                    Resistor r = (Resistor)obj;
                    System.out.println("We have a " + r.getClass().getSimpleName() + " at " + r.position + " with resistance: " + r.resistance.GetQuantity().ToString());
                    totalResistance.Add(r.resistance.GetQuantity());
                    numResistors++;
                }
                else if (obj instanceof Component) {
                    Component c = (Component)obj;
                    System.out.println("We have a " + c.getClass().getSimpleName() + " at " + c.position);
                }
            }
            UpdateStatus("Number of nodes: " + numNodes + " | Number of resistors: " + numResistors + " | Total resistance: " + totalResistance.ToString());
            return; // we're done, so let's pack up and head home
        }

        Component nextComponent = GetComponentAtPoint(nextPosition);

        if (IsJunction(nextComponent)) {

            Node nextNode = GetNode(nextPosition,position, null);

            if (nextNode != null) {
                OrderedList.add(nextNode);
                nextPosition = nextNode.endJunction; // so I'm not sure if I actually coded this to work properly...
            } else {
                System.out.println("I hit a junction on the main path, but it wasn't a node... I'm not sure what to do!");
                return;
            }
        } else {
            OrderedList.add(nextComponent);
        }



        SolveCircuit(nextPosition, OrderedList);
    }


    public Node GetNode(Point position, Point lastPosition, Set<Point> exclusions) {

        /*

         */

        ArrayList<Point> pathStartPoints = ReachablePoints(position,visitedArray);
        // for each path, we have to travel until we reach one junction, then we test them all
        // a temporary data structure for paths, perhaps?

        ArrayList<Path> paths = new ArrayList<>();

        for(int i = 0; i < pathStartPoints.size(); i++){
            paths.add(new Path(GetComponentAtPoint(position),GetComponentAtPoint(pathStartPoints.get(i))));
        }

        // we need to implement finding the junction common to all paths

        Point endingJunction = GetEndOrCommonJunction(position,lastPosition,paths,exclusions);

        visitedArray[position.x][position.y] = VISITED;
        //visitedArray[endingJunction.x][endingJunction.y] = VISITED;

        if(exclusions == null)
            exclusions = new HashSet<Point>();

        exclusions.add(position);
        exclusions.add(endingJunction);


        //leaving this here even though it will essentially do nothing
        if(paths.size() <= 1)
            return null; // because a node has multiple paths out

        System.out.println("Trying to find node spanning " + position + " to " + endingJunction + " which has " + paths.size() + " paths.");
        int counter = 0;
        while(!PathsConverged(paths,endingJunction)){// && counter < 500){
            //pick the path which has not yet seen a junction, else pick the path with the fewest steps taken so far
            Path pathToTravel = null;
            Point pointTravelledTo = null;

            pathToTravel = NextJunctionPath(paths, endingJunction);
            if(IsJunction(GetComponentAtPoint(pathToTravel.endPoint()))) {
                System.out.println("Node start: " + position + " and starting junction found at " + pathToTravel.endPoint());
                pathToTravel.Add(GetNode(pathToTravel.endPoint(), pathToTravel.startPoint(),exclusions));
            }


            //TODO: see if we *start* at a junction -- the first step on a branch can most certainly be a junction/beginning of a new node!
            /*
            while(pathToTravel == null){

                // if it fails getnode, it means we should do it last, because it just connects to this node outlet

                pathToTravel = NextJunctionPath(paths, endingJunction, attempts);

                if(pathToTravel == null){
                    System.out.println("I've been to at least one junction on each path, but no ending junctions are nodes, which confuses me!.. Time for analysis?");
                    return null;
                }

                //int[][] visitedCopy = visitedArray.clone();
                System.out.println("I must search for a node at " + pathToTravel.endPoint());
                Node returnValue = GetNode(pointTravelledTo, pathToTravel.endPoint());

                if(returnValue != null) {
                    //visitedArray = visitedCopy; // might want to directly copy values in, but let's try it this way for now
                    pathToTravel.nodesAlong++;
                    pathToTravel.Add(returnValue);
                }
                else{
                    // what do we do if we failed GetNode? head to another path which won't, i guess!
                    // we use an attempt system. this will make us try getNode on everything
                    attempts++;
                }
            }
            */
            //if(toTravel == null)
            //    toTravel = LeastStepPath(paths);

            // travel along the path
            // question -- can't we just use recursion once we see that all paths don't converge?
            // let Pn = path subscript n
            // if P1.endpoint = J1, P2.endpoint = J2, P3.endpoint = J2, we can GetNode on P1 to see the node in the path
            // and jump across it, continuing along that path and looking for the next junction after it (which may be J2)


            Point previousPoint = pathToTravel.endPoint();

            visitedArray[previousPoint.x][previousPoint.y] = VISITED;

            pointTravelledTo = Travel(pathToTravel,visitedArray);
            /*

             */
            pathToTravel.Add(GetComponentAtPoint(pointTravelledTo));

            // if it hits a junction which is not the end junction, it IS a node, for certain
            if(IsJunction(GetComponentAtPoint(pointTravelledTo))) {
                if(!pointTravelledTo.equals(endingJunction))
                {
                    System.out.println("Ending Junction: " + endingJunction + " | I sense a new node at " + pointTravelledTo);
                    pathToTravel.Add(GetNode(pointTravelledTo,previousPoint,exclusions));
                }
                else{
                    pathToTravel.junctionsAlong++;
                }
                //System.out.println("Adding junction to path at position: " + pointTravelledTo);
                //System.out.println("Our new end point is " + pathToTravel.endPoint());
            }
            counter++;
        }

        System.out.println("Paths have converged -- node found, spanning " + position + " to " + paths.get(0).endPoint() + ".");
        return new Node(position,paths);
    }

    Point Travel(Path path, int[][] visitedArray){

        Point endPoint = path.endPoint();

        if(endPoint == null)
            endPoint = path.startPoint();

        //if the code breaks, i moved markVisited AFTER this but i think that's okay

        //PrintArray(visitedArray);

        Point pointTravelledTo = NextInPath(endPoint,visitedArray);

        if(pointTravelledTo == null){
            Point p1 = new Point(endPoint.x - 1,endPoint.y);
            Point p2 = new Point(endPoint.x,endPoint.y-1);
            Point p3 = new Point(endPoint.x + 1,endPoint.y);
            Point p4 = new Point(endPoint.x,endPoint.y+1);
            System.out.println("Cannot travel, printing VISITED status of surrounding points: \n\nLeft: " + Visited(p1,visitedArray) + " Up: " + Visited(p2,visitedArray) + " Right: " + Visited(p3,visitedArray) + " Down: " + Visited(p4,visitedArray));
        }

        System.out.println("End point position: " + endPoint + " | Next position: " + pointTravelledTo);

        path.steps++;

        return pointTravelledTo;
    }

    /*
    Point Travel(Path path){
        Point endPoint = path.endPoint();

        if(endPoint == null)
            endPoint = path.startPoint();

        //if the code breaks, i moved markVisited AFTER this but i think that's okay

        Point nextPosition = NextInPath(endPoint);

        //System.out.println("End point position: " + endPoint + " | Next position: " + nextPosition);

        path.steps++;

        return nextPosition;
    }
*/
    public Path UnseenJunctionPath(ArrayList<Path> paths){

        for(Path p : paths)
            if(p.junctionsAlong - p.nodesAlong == 0)
                    return p;

        return null;
    }

    public Path NextJunctionPath(ArrayList<Path> paths, Point endingJunction){

        int counter = 0;
        for(Path p : paths) {
            if (!p.endPoint().equals(endingJunction)) {
                System.out.println("Next junction path is: " + counter);
                return p;
            }
            counter++;
        }

        return null;
    }
  /*
    public Path NextJunctionPath(ArrayList<Path> paths, Point endingJunction, int attempts){

        for(Path p : paths)
            if(attempts == 0 )
                return p;
            else
                attempts--;

        return null;
    }
*/
    public Path LeastJunctionPath(ArrayList<Path> paths){

        Path withFewestJunctions = null;

        for(Path p : paths)
            if(withFewestJunctions == null || p.junctionsAlong < withFewestJunctions.junctionsAlong)
                withFewestJunctions = p;

        return withFewestJunctions;
    }

    public Path LeastStepPath(ArrayList<Path> paths){

        Path withFewestSteps = null;

        for(Path p : paths)
            if(withFewestSteps == null || p.steps < withFewestSteps.steps)
                withFewestSteps = p;

        return withFewestSteps;
    }

    Point GetEndOrCommonJunction(Point nodeStartPosition, Point beforeNodeStart, ArrayList<Path> paths, Set<Point> exclusions){

        System.out.println("Seeking end or common junction... ");

        //int[][] visitedCopy = visitedArray.clone();

        Point endOrCommonJunction = null;

        // now we must add the magic juju
        //Point junctionToSeek = paths.get(0).startPoint();

        /*
        while(junctionToSeek == null || !IsJunction(GetComponentAtPoint(junctionToSeek))){
            junctionToSeek = TravelAndMarkVisited(paths.get(0),visitedCopy);
        }

         */
        //System.out.println("Junction to seek is " + junctionToSeek + " paths: " + paths.size());
        //Point junctionToSeek = GetFirstJunction(paths.get(0), visitedCopy); // here seems like a prime candidate for my backtracking algorithm... we need to adapt it

        int currentPathIndex = 0; // we checked the first path, of course

        Set<Set<Point>> loopPaths = new HashSet<>();
        //ArrayList<ArrayList<Point>> loopPaths = new ArrayList<>();

        while(currentPathIndex < paths.size()){

            // pick the next path...

            Path toCheck = paths.get(currentPathIndex);

            int[][] visitedClone = EmptyVisitedClone();
            visitedClone[nodeStartPosition.x][nodeStartPosition.y] = INACCESSIBLE;
            //visitedClone[beforeNodeStart.x][beforeNodeStart.y] = VISITED;
            Boolean Verbose = false;
            Pathfinding pathFinder = new Pathfinding(visitedClone, mainGrid, Verbose);

            // search *backwards*
            Point pointToSeekFrom = beforeNodeStart;//paths.get(currentPathIndex).endPoint();//(paths.get(currentPathIndex).endPoint() == null) ? paths.get(currentPathIndex).startPoint() : paths.get(currentPathIndex).endPoint();
            //TODO: pass in breadboardgrid[][] to pathFinder as well, because it needs to use wire connections to determine if travel is possible (not just is the tile adjacent and unvisited)

            if(Verbose) System.out.println("Before node start: " + beforeNodeStart + " | Node start: " + nodeStartPosition);
            if(Verbose) System.out.println("Point to seek from: " + pointToSeekFrom + ", Point to seek: " + paths.get(currentPathIndex).endPoint() + ".");
            loopPaths.add(pathFinder.Solve(pointToSeekFrom,paths.get(currentPathIndex).endPoint(), 0, new Stack<>(), new HashSet<>()));


            currentPathIndex++;

        }


        return GetCommonLoopPoint(loopPaths, exclusions);

    }

    Point GetCommonLoopPoint(Set<Set<Point>> loopPaths, Set<Point> exclusions){
        // OKAY, we just need to find a point common to all lists, and then we HAVE IT
        Set<Point> result = null;

        int counter = 0;
        for(Set<Point> loopPath : loopPaths){

            if(result == null) {
                result = loopPath;
                //result.addAll(loopPath);
            }
            else {
                //System.out.println("Iteration: " + counter + " Result null? " + (result == null) + " loop path null? " + (loopPath == null));
                //System.out.println("Set " + counter + ": ");
                //for(Point p : loopPath)
                //    System.out.print(p);

                result.retainAll(loopPath);
            }

            counter++;
        }

        if(exclusions != null)
            result.removeAll(exclusions);

        // print to see what our results are, even though i expect we need to trim the path list down to junctions
        for(Point p : result)
        {
            System.out.println("Common junction between " + loopPaths.size() + " paths: (" + p.x + "," + p.y + ")");
        }

        // TODO: okay, what we *actually* want is the first junction we saw that was shared
        if(result.size() > 1)
        {
            System.out.println("Hey, I found more than one common end junction... going to return the 'first' result, this may cause issues (if the first result is not the first junction seen that was shared), idk how hashsets work entirely");
            Point pointToReturn = result.iterator().next();

            System.out.println("Returning " + pointToReturn + "...");
            return pointToReturn;
        }
        else {
            // there should only be one result
            return result.iterator().next();
        }
    }



    public class Path{
        int junctionsAlong = 0;
        int nodesAlong = 0;
        int steps = 0; // testing variable to help GetNode choose the path with the least steps
        public Object startObject(){
            return OrderedList.get(0);
        }
        public Object endObject(){
            return OrderedList.get(OrderedList.size() - 1);
        }

        public Point startPoint(){
            if(startObject() instanceof Component) {
                return ((Component)startObject()).position;
            }
            else
            if(startObject() instanceof Node){
                System.out.println("Chief, I don't know what to do with a node right now.");
            }

            return null;
        }

        public Point endPoint(){

            if(OrderedList.size() <= 1)
                return null; // with only one object in the path, there is no real end point because start will == end

            if(endObject() instanceof Component) {
                return ((Component)endObject()).position;
            }
            else
            if(endObject() instanceof Node){
                System.out.println("Chief, I may not know what to do with a node right now.");
                return ((Node)endObject()).endJunction;
            }

            return null;
        }

        public void Add(Object objectInPath){
            OrderedList.add(objectInPath);
        }

        public int GetNumResistors(){
            int numResistors = 0;
            for(Object obj : OrderedList){
                if (obj instanceof Resistor){
                    numResistors++;
                } else if (obj instanceof Node) {
                    numResistors += ((Node)obj).GetNumResistors();
                }
            }
            return numResistors;
        }

        public int GetNumNodes(){
            int numNodes = 0;
            for(Object obj : OrderedList){
                if(obj instanceof Node){
                    numNodes++;
                    numNodes += ((Node)obj).GetNumNodes();
                }
            }
            return numNodes;
        }

        public StandardNum GetResistance(){
            StandardNum returnValue = new StandardNum();

            for(Object obj : OrderedList) {
                if (obj instanceof Resistor) {
                    //System.out.println(((Resistor)obj).resistance.GetQuantity().ToString());
                    returnValue.Add(((Resistor) obj).resistance.GetQuantity());
                } else
                if (obj instanceof Node) {
                    returnValue.Add(((Node) obj).GetResistance());
                }
            }

            //System.out.println("Returning path resistance of " + returnValue.ToString());
            //System.out.println(returnValue.exponent);
            return returnValue;
        }

        private ArrayList<Object> OrderedList = new ArrayList<>();

        public Path(Component startComponent, Component firstStepComponent){
            OrderedList.add(startComponent);
            OrderedList.add(firstStepComponent);
        }
    }

    public Boolean PathsConverged(ArrayList<Path> paths, Point commonEndPoint){

        //Point commonEndPoint = paths.get(0).endPoint();

        for(Path p : paths){
            //System.out.println("Common end point: " + commonEndPoint + " | evaluated end point: " + p.endPoint());
            if(p.endPoint() == null || !p.endPoint().equals(commonEndPoint)) {
                return false;
            }
        }

        return true;
    }

    public Point NextInPath(Point fromPosition, int[][] visited) {
        // code to determine what comes next in the path
        // get all points within reach of the current position
        ArrayList<Point> reachablePoints = ReachablePoints(fromPosition, visited);

        //reachablePoints.get(0)
        if(reachablePoints.size() > 0)
            return reachablePoints.get(0);
        else
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
            //PrintArray(visited);
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
            //if(Visited(p,visitedArray) && CanReach(currentPosition,p))
              //  System.out.println("You know, I would love to go to (" + p.x + "," + p.y + ")... but that stuff is visited, my mans.");
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


    /*
    class Node{
        //class may be moved in future, being used here to test new pathfinding idea
        Component startComponent; // junction where the node begins
        Component endComponent;   // junction where the node ends (NumBranchesOut = NumBranchesIn)

        // some kind of way to store what's in between start and end...
        // in C# I'd say a list of Object, where it can be comp-> comp -> comp -> Node -> comp, comp... etc.
        // but there can be more than one path, really up to three (one in, three out)
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
*/
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

        // encapsulation + accessor for pathfinding (and any other future script)
        public Component GetCurrentComponent(){ return component; }
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
                UpdateStatus("Successfully placed component at " + position + "!");
                SetImage(component.GetImage());

                // temporary check: if resistor, see if it has an associated resistance value
                if(component instanceof Resistor){
                    System.out.println("Added component of type Resistor with resistance value of " + ((Resistor)component).resistance.GetQuantity().ToString());
                }
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
