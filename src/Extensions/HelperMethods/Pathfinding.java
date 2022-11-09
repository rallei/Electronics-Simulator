package Extensions.HelperMethods;

import java.util.Stack;

public class Pathfinding {
    public static class Coordinate
    {
        public int x;
        public int y;

        public Coordinate(int _x, int _y)
        {
            x = _x;
            y = _y;
        }
    }


    public class CircuitMap{
        int[][] map; // create a multidimensional array of nodes for access with coordinate numbers
        /*
        public Coordinate goal
        {
            get
            {
                return _goal;
            }
            private set
            {
                _goal = value;
            }
        }
        Coordinate _goal;
        */
        public Boolean CanTravel(int x, int y)
        {
            //Console.WriteLine("Checking to see if I can move to " + x + "," + y + "...");

            // You gotta be within the map!
            if (x < 0 || y < 0)
                return false;
            // In the map, I say!
            if (x >= map.length || y >= map[x].length)
                return false;

            else if (map[x][y] != 0)
            return true;
            else
            return false;
        }
    }

    public CircuitMap map; // we have to feed this in somehow, but hey... looks like our solution translates easily!

    public static Coordinate CoordValue(int direction){
        switch(direction){
            case 0: return new Coordinate(0,1);
            case 1: return new Coordinate(1,0);
            case 2: return new Coordinate(0,-1);
            case 3: return new Coordinate(-1,0);
        }
        return null;
    }

    static Coordinate Sum(Coordinate a, Coordinate b)
    {
        return new Coordinate(a.x + b.x, a.y + b.y);
    }

    static Coordinate Travel(Coordinate pos, int direction)
    {
        return Sum(pos, CoordValue(direction));
    }

    static Boolean CanTravel(Coordinate current, int direction, CircuitMap m)
    {
        Coordinate destination = Sum(current, CoordValue(direction));

        if (m.CanTravel(destination.x, destination.y))
            return true;
        else
            return false;
    }

    static Boolean NodeIsIsolated(Coordinate node, Stack<Coordinate> turns, CircuitMap m)
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

    static Boolean AlreadyBeenThere(Coordinate node, int direction, Stack<Coordinate> turns)
    {
        return turns.contains(Sum(node, CoordValue(direction)));
    }

    static Boolean ArrivedAtGoal(Coordinate pos, Coordinate goal)
    {
        if (pos.x == goal.x && pos.y == goal.y)
            return true;
        else
            return false;
    }

    static void Solve(Coordinate pos, Coordinate goal, int direction, Stack<Coordinate> turns)
    {
        direction = direction % 4;

        // Rule 0
        if(ArrivedAtGoal(pos, goal))
        {
            System.out.println("Printing route: ");
            // pop everything off the stack to show the route

            while(!turns.isEmpty())
            {
                Coordinate c = turns.pop();
                System.out.println("( " + c.x + "," + c.y + ")"); // we can work on this more later
            }
            return;
        }

        // If we're on an isolated node, let's jump to the last place we were at
        // checking if a node is isolated is basically just checking CanTravel in every direction lol

        /*
        if (NodeIsIsolated(pos, turns, map))
        {
            try{
                pos = turns.pop(); // jump back to our last known turn until we're not unable to travel anymore
            }
            catch(Exception e){
            System.out.println("There was a problem getting to the destination, oops!");
            throw(e);
        }
        }

         */

        /*
        // initialize left here...
        int left = (direction + 3) % 4; // to avoid indexing errors here, i just used more addition cause numbers are COOL! :)
        int right = (direction + 1) % 4;
        System.out.println("Left: " + left + " | Right: " + right + " | Direction: " + direction + "\n");
        // if we can go left, and we haven't been there

        if (CanTravel(pos, left, map) && !AlreadyBeenThere(pos, left, turns))
        {
            Coordinate newPos = Travel(pos, left);
            turns.push(newPos);

            Solve(Travel(pos, left), goal, left, turns);
            // add that we took a turn, even when we take a right, it's actually just three lefts so we only need to log it here
        }


        // otherwise, if we can just go straight ahead and we haven't been there
        else if (CanTravel(pos,direction,map) && !AlreadyBeenThere(pos, direction, turns))
        {
            Solve(Travel(pos, direction), goal, direction, turns);
        }
        else // otherwise, let's turn right instead
            Solve(pos, goal, direction + 1, turns); // just turn and look for a node we can go to then

         */
    }

}
