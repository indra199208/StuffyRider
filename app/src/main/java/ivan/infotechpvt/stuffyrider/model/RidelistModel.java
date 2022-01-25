package ivan.infotechpvt.stuffyrider.model;

import java.util.List;

public class RidelistModel {

    private String  id, store_id, ride_name, ride_desc, ride_img, color_code, ride_min_time, ride_base_price, extra_per_min_cost, status, ride_time_slot, availibility;
    private String ride_base_time, ride_base_charge, ride_id;
    private String booking_id, customer_name, customer_mobile;
    private boolean isRideOngoning = false;
    private List<TimeSlotModel> timeSlot;

    public List<TimeSlotModel> getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(List<TimeSlotModel> timeSlot) {
        this.timeSlot = timeSlot;
    }

    public boolean isRideOngoning() {
        return isRideOngoning;
    }

    public void setRideOngoning(boolean rideOngoning) {
        isRideOngoning = rideOngoning;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_mobile() {
        return customer_mobile;
    }

    public void setCustomer_mobile(String customer_mobile) {
        this.customer_mobile = customer_mobile;
    }

    private boolean isSelected = false;

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getAvailibility() {
        return availibility;
    }

    public void setAvailibility(String availibility) {
        this.availibility = availibility;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getRide_time_slot() {
        return ride_time_slot;
    }

    public void setRide_time_slot(String ride_time_slot) {
        this.ride_time_slot = ride_time_slot;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getRide_name() {
        return ride_name;
    }

    public void setRide_name(String ride_name) {
        this.ride_name = ride_name;
    }

    public String getRide_desc() {
        return ride_desc;
    }

    public void setRide_desc(String ride_desc) {
        this.ride_desc = ride_desc;
    }

    public String getRide_img() {
        return ride_img;
    }

    public void setRide_img(String ride_img) {
        this.ride_img = ride_img;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getRide_min_time() {
        return ride_min_time;
    }

    public void setRide_min_time(String ride_min_time) {
        this.ride_min_time = ride_min_time;
    }

    public String getRide_base_price() {
        return ride_base_price;
    }

    public void setRide_base_price(String ride_base_price) {
        this.ride_base_price = ride_base_price;
    }

    public String getExtra_per_min_cost() {
        return extra_per_min_cost;
    }

    public void setExtra_per_min_cost(String extra_per_min_cost) {
        this.extra_per_min_cost = extra_per_min_cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
