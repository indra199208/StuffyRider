package ivan.infotechpvt.stuffyrider.model;

import java.util.List;

public class OngoingrideModel {

    private String id,
            booking_id, ride_id, store_id, statr_time, end_time, total_ride, min_ride_time, min_ride_price,ride_desc, addition_ride_time, addition_ride_cost, total_ride_cost, status, ride_name, ride_img, color_code, ride_time_slot, time_spend, extra_per_min_cost;

    private boolean isRideOngoning = false;
    private boolean isRideStop = false;
    private List<TimeSlotModel> timeSlot;
    int counter;

    public int getCounter() {
        return counter;
    }

    public String getExtra_per_min_cost() {
        return extra_per_min_cost;
    }

    public void setExtra_per_min_cost(String extra_per_min_cost) {
        this.extra_per_min_cost = extra_per_min_cost;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public List<TimeSlotModel> getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(List<TimeSlotModel> timeSlot) {
        this.timeSlot = timeSlot;
    }

    String customer_name,customer_mobile;

    public boolean isRideStop() {
        return isRideStop;
    }

    public void setRideStop(boolean rideStop) {
        isRideStop = rideStop;
    }

    public String getTime_spend() {
        return time_spend;
    }

    public void setTime_spend(String time_spend) {
        this.time_spend = time_spend;
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

    public boolean isRideOngoning() {
        return isRideOngoning;
    }

    public void setRideOngoning(boolean rideOngoning) {
        isRideOngoning = rideOngoning;
    }

    public String getRide_desc() {
        return ride_desc;
    }

    public void setRide_desc(String ride_desc) {
        this.ride_desc = ride_desc;
    }

    public String getRide_time_slot() {
        return ride_time_slot;
    }

    public void setRide_time_slot(String ride_time_slot) {
        this.ride_time_slot = ride_time_slot;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

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

    public String getStatr_time() {
        return statr_time;
    }

    public void setStatr_time(String statr_time) {
        this.statr_time = statr_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTotal_ride() {
        return total_ride;
    }

    public void setTotal_ride(String total_ride) {
        this.total_ride = total_ride;
    }

    public String getMin_ride_time() {
        return min_ride_time;
    }

    public void setMin_ride_time(String min_ride_time) {
        this.min_ride_time = min_ride_time;
    }

    public String getMin_ride_price() {
        return min_ride_price;
    }

    public void setMin_ride_price(String min_ride_price) {
        this.min_ride_price = min_ride_price;
    }

    public String getAddition_ride_time() {
        return addition_ride_time;
    }

    public void setAddition_ride_time(String addition_ride_time) {
        this.addition_ride_time = addition_ride_time;
    }

    public String getAddition_ride_cost() {
        return addition_ride_cost;
    }

    public void setAddition_ride_cost(String addition_ride_cost) {
        this.addition_ride_cost = addition_ride_cost;
    }

    public String getTotal_ride_cost() {
        return total_ride_cost;
    }

    public void setTotal_ride_cost(String total_ride_cost) {
        this.total_ride_cost = total_ride_cost;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getRide_img() {
        return ride_img;
    }

    public void setRide_img(String ride_img) {
        this.ride_img = ride_img;
    }

    public String getRide_name() {
        return ride_name;
    }

    public void setRide_name(String ride_name) {
        this.ride_name = ride_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
