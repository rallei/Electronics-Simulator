package Circuits.Components;
import Units.Metric.StandardNum;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class VoltageSource extends Component {
     private StandardNum voltageRating;

     private Boolean isOn = true;

     @JsonIgnore
    // standard connection is top-> bottom
    public int[] getConnectionPoints(){
        return new int[]{0,2};
    }

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

    public VoltageSource(StandardNum rating){
        voltageRating = rating;
    }

    public VoltageSource(){

    }
}
