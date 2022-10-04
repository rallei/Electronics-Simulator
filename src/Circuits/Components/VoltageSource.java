package Circuits.Components;
import Units.Electrical.Properties.*;

public class VoltageSource extends Component {
     private Voltage voltageRating;

     private Boolean isOn = true;


    public double GetVoltage(){
        if(!isOn)
            return 0;
        else
            return voltageRating.GetValue();
    }

    public class Switch{
        public void On(){
            isOn = true;
        }
        public void Off(){
            isOn = false;
        }
    }

    public VoltageSource(Voltage rating){
        voltageRating = rating;
    }
}
