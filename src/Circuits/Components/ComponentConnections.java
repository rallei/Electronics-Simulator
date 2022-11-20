package Circuits.Components;

public class ComponentConnections {

    public static class ConnectionPoint{
        public boolean isConnected;
        public Rotation point;


        public ConnectionPoint(boolean isConnected, Rotation point){
            this.isConnected = isConnected;
            this.point = point;
        }

        public ConnectionPoint(){
            // empty constructor to make Jackson happy?
        }

        public void Clear(){
            isConnected = false;
        }
    }

    public ConnectionPoint top = new ConnectionPoint(false, Rotation.TOP);
    public ConnectionPoint right = new ConnectionPoint(false, Rotation.RIGHT);
    public ConnectionPoint bot = new ConnectionPoint(false, Rotation.BOT);
    public ConnectionPoint left = new ConnectionPoint(false, Rotation.LEFT);

    // put them in a list so we can iterate over them
    public ConnectionPoint[] points = new ConnectionPoint[]{ top, right, bot, left };

    public void Clear(){
        for(ConnectionPoint point: points)
            point.Clear();
    }

    public void PrintActiveConnections(){
        for(ConnectionPoint point : points)
            if(point.isConnected)
                System.out.println(point.point.GetDirection(point.point.GetValue()) + " is active.");
    }

    public ComponentConnections GetRotatedConnections(int[] connections, Rotation rotation){
        // 'unrotate' stuff
        ConnectionPoint[] rotatedPoints = new ConnectionPoint[connections.length];
        int counter = 0;
        for(int point : connections){
        }
        return null;
    }

    public ConnectionPoint GetConnectionPoint(Rotation rotation){
        switch(rotation){
            case TOP: return top;
            case RIGHT: return right;
            case BOT: return bot;
            case LEFT: return left;
        }
        return null;
    }

    public boolean CheckCanAdd(int[] connections, Rotation rotation){
        for(int i : connections){
            if(points[(i+ rotation.GetValue()) % 4].isConnected == true)
                return false;
        }
        return true;
    }

    public boolean CheckIsPlaced(ComponentConnections connections){
        if(!top.isConnected && connections.top.isConnected ||
                !right.isConnected && connections.right.isConnected ||
                !bot.isConnected && connections.bot.isConnected ||
                !left.isConnected && connections.left.isConnected)
            return false;
        return true;
    }

    public int GetNumConnections(){
        int counter = 0;
        for(ConnectionPoint point: points)
            if(point.isConnected)
                counter++;
        return counter;
    }

    public boolean Add(int[] connections, Rotation rotation){
        // if you want to connect to something that i already have connected... sorry, but no
        if(!CheckCanAdd(connections, rotation))
            return false;

        for(int i : connections){
            points[ (i+ rotation.GetValue()) % 4].isConnected = true;
            //System.out.println("Set " + points[ (i+ rotation.GetValue()) % 4].point.GetDirection(points[ (i+ rotation.GetValue()) % 4].point.GetValue()) + " to " + points[ (i+ rotation.GetValue()) % 4].isConnected);
        }
        //PrintActiveConnections();
        return true;
    }

    public ComponentConnections(boolean top, boolean right, boolean bot, boolean left){
        this.top.isConnected = top;
        this.right.isConnected = right;
        this.bot.isConnected = bot;
        this.left.isConnected = left;
    }

    public ComponentConnections(){
        //empty or default constructor.. o.o
    }

}
