package ivan.infotechpvt.stuffyrider.model;

public class TimeSlotModel {

    String ride_base_time;
    String ride_base_charge;
    boolean isSelected= false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public TimeSlotModel(String ride_base_time, String ride_base_charge) {
        this.ride_base_time = ride_base_time;
        this.ride_base_charge = ride_base_charge;
    }

    public String getRide_base_time() {
        return ride_base_time;
    }

    public void setRide_base_time(String ride_base_time) {
        this.ride_base_time = ride_base_time;
    }

    public String getRide_base_charge() {
        return ride_base_charge;
    }

    public void setRide_base_charge(String ride_base_charge) {
        this.ride_base_charge = ride_base_charge;
    }
}
