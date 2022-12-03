package Extensions.HelperMethods;

import Circuits.Components.Breadboard.Breadboard;
import Circuits.Components.Component;
import Circuits.Components.ComponentConnections;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import static Extensions.Constants.INACCESSIBLE;
import static Extensions.Constants.UNVISITED;
import static Extensions.Constants.VISITED;

public class Pathfinding {

    public boolean Verbose = false;
    public class CircuitMap{
        int[][] map;
        Breadboard.BreadboardGridUnit[][] breadboard;
        public Boolean CanTravel(Point position, Point destination)
        {
            //Console.WriteLine("Checking to see if I can move to " + x + "," + y + "...");

            // You gotta be within the map!
            if (position.x < 0 || position.y < 0)
                return false;
            // In the map, I say!
            if (position.x >= map.length || position.y >= map[position.x].length)
                return false;

            //else if (map[x][y] == UNVISITED)
            else if (Accessible(destination) && CanReach(position,destination) && !Visited(destination,map)) {
                return true;
            }
            else
                return false;
        }

        public void MarkVisited(Point pos){
            map[pos.x][pos.y] = VISITED;
        }

        public Component GetComponentAtPoint(Point p){
            return breadboard[p.x][p.y].GetCurrentComponent();
        }

        public Boolean Accessible(Point p){
            return map[p.x][p.y] != INACCESSIBLE;
        }

        public CircuitMap(int[][] map, Breadboard.BreadboardGridUnit[][] breadboard){
            this.map = map;
            this.breadboard = breadboard;
        }
    }

    public CircuitMap map; // we have to feed this in somehow, but hey... looks like our solution translates easily!

    public static Point CoordValue(int direction){
        switch(direction){
            case 0: return new Point(0,1);
            case 1: return new Point(1,0);
            case 2: return new Point(0,-1);
            case 3: return new Point(-1,0);
        }
        return null;
    }

    static Point Sum(Point a, Point b)
    {
        return new Point(a.x + b.x, a.y + b.y);
    }

    static Point Travel(Point pos, int direction, CircuitMap m)
    {
        Point pointToTravelTo = Sum(pos, CoordValue(direction));
        m.MarkVisited(pointToTravelTo);
        return pointToTravelTo;
    }

    static Boolean CanTravel(Point current, int direction, CircuitMap m)
    {
        Point destination = Sum(current, CoordValue(direction));

        if (m.CanTravel(current, destination))
            return true;
        else
            return false;
    }

    static Boolean NodeIsIsolated(Point node, Stack<Point> turns, CircuitMap m)
    {
        // an isolated node is just one we can't travel in any direction from
        for (int i = 0; i < 4; i++)
        {
            // it's isolated if for every direction we either can't travel, or we can, but have been there
            if (CanTravel(node, i, m) && !AlreadyBeenThere(node, i,turns))
                return false;
        }

        return true;
    }

    static Boolean AlreadyBeenThere(Point node, int direction, Stack<Point> turns)
    {
        return turns.contains(Sum(node, CoordValue(direction)));
    }

    static Boolean ArrivedAtGoal(Point pos, Point goal)
    {
        if (pos.x == goal.x && pos.y == goal.y)
            return true;
        else
            return false;
    }

    <T> Boolean NotADuplicateEntry(Set<T> list, T toAdd){

        if(!list.contains(toAdd))
            return true;
        else
            return false;
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

    private Boolean IsJunction(Component component) {
        return IsJunction(component.connections);
    }


    public HashSet<Point> Solve(Point pos, Point goal, int direction, Stack<Point> turns, HashSet<Point> junctionsSeen) {
         direction = direction % 4;

         if(Verbose) System.out.println("Position: (" + pos.x + ", " + pos.y + ")");

        // initialize left here...
        int left = (direction + 3) % 4; // to avoid indexing errors here, i just used more addition cause numbers are COOL! :)
        int right = (direction + 1) % 4;

         if(!pos.equals(goal) && IsJunction(map.GetComponentAtPoint(pos)) && NotADuplicateEntry(junctionsSeen,pos)) {
             //System.out.println("seen junction " + pos);
             junctionsSeen.add(pos);
         }

         // Rule 0
         if (ArrivedAtGoal(pos, goal)) {
             //System.out.println("Printing route: ");
             // pop everything off the stack to show the route

             while (!turns.isEmpty()) {
                 Point c = turns.pop();
                 //System.out.println("( " + c.x + "," + c.y + ")"); // we can work on this more later
             }
            return junctionsSeen;
         }

         // If we're on an isolated node, let's jump to the last place we were at
         // checking if a node is isolated is basically just checking CanTravel in every direction lol


         else if (NodeIsIsolated(pos, turns, map)) {
             try {
                 pos = turns.pop(); // jump back to our last known turn until we're not unable to travel anymore

                 if (Verbose) System.out.println("Go back to " + pos);
                 // start from this new position straight away
                 Solve(pos,goal,direction,turns,junctionsSeen);
             } catch (Exception e) {
                 if (Verbose) System.out.println("There was a problem getting to the destination, oops!");
                 throw (e);
             }
         }



         //System.out.println("Left: " + left + " | Right: " + right + " | Direction: " + direction + "\n");
         // if we can go left, and we haven't been there
         else if (CanTravel(pos, left, map) && !AlreadyBeenThere(pos, left, turns)) {
             Point newPos = Travel(pos, left,map);
             if (Verbose) System.out.println("Add new turn at " + pos);
             turns.push(pos);

             Solve(Travel(pos, left,map), goal, left, turns, junctionsSeen);
             // add that we took a turn, even when we take a right, it's actually just three lefts so we only need to log it here
         }


         // otherwise, if we can just go straight ahead and we haven't been there
         else if (CanTravel(pos, direction, map) && !AlreadyBeenThere(pos, direction, turns)) {
             // TODO: issue where we can travel straight ahead at a location where we SHOULD turn
             // could do 'if reachable points > 2'
             if(ReachablePoints(pos,map.map).size() >= 2){
                 turns.push(pos);
             }
             Solve(Travel(pos, direction,map), goal, direction, turns, junctionsSeen);
         } else // otherwise, let's turn right instead
         {
             Solve(pos, goal, direction + 1, turns, junctionsSeen); // just turn and look for a node we can go to then
         }

         return junctionsSeen;
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
        if(difference.x < 0 && map.GetComponentAtPoint(currentPosition).connections.right.isConnected && map.GetComponentAtPoint(toReach).connections.left.isConnected)
            return true;
        else if (difference.x > 0 && map.GetComponentAtPoint(currentPosition).connections.left.isConnected && map.GetComponentAtPoint(toReach).connections.right.isConnected)
            return true;
        else if (difference.y < 0 && map.GetComponentAtPoint(currentPosition).connections.bot.isConnected && map.GetComponentAtPoint(toReach).connections.top.isConnected)
            return true;
        else if (difference.y > 0 && map.GetComponentAtPoint(currentPosition).connections.top.isConnected && map.GetComponentAtPoint(toReach).connections.bot.isConnected)
            return true;

        return false; // if all other checks failed, these two points are not connected
    }



    public Pathfinding(int[][] map, Breadboard.BreadboardGridUnit[][] breadboard, Boolean Verbose){
        this.map = new CircuitMap(map,breadboard);
        this.Verbose = Verbose;
    }

}
