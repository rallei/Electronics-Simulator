package Extensions;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

// borrowed from https://stackoverflow.com/questions/32001/resettable-java-timer
public class ReschedulableTimer extends Timer
{
    private Runnable  task;
    private TimerTask timerTask;
    public Boolean isRunning = false;

    public void schedule(Runnable runnable, long delay)
    {
        task = runnable;
        isRunning = true;
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                task.run();
                isRunning = false;
            }
        };
        this.schedule(timerTask, delay);
    }

    public void reschedule(long delay)
    {
        timerTask.cancel();
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                task.run();
            }
        };
        this.schedule(timerTask, delay);
    }
}