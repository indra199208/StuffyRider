package ivan.infotechpvt.stuffyrider.model;

import java.util.ArrayList;
import java.util.List;

public class RidehistoryModel {

    private String id, customer_name, customer_mobile, store_id, user_id, booking_no, booking_start_time, booking_end_time, total_ride_time,
            booking_date, no_of_ride, ride_cost, addition_cost, total_cost, created_by, end_by, status, total_ride_cost, total_ride, payment_status;

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getTotal_ride_cost() {
        return total_ride_cost;
    }
    private List<RidedetailsModel> detailslist = new ArrayList<>();

    public List<RidedetailsModel> getDetailslist() {
        return detailslist;
    }

    public void setDetailslist(List<RidedetailsModel> detailslist) {
        this.detailslist = detailslist;
    }

    public void setTotal_ride_cost(String total_ride_cost) {
        this.total_ride_cost = total_ride_cost;
    }

    public String getTotal_ride() {
        return total_ride;
    }

    public void setTotal_ride(String total_ride) {
        this.total_ride = total_ride;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBooking_no() {
        return booking_no;
    }

    public void setBooking_no(String booking_no) {
        this.booking_no = booking_no;
    }

    public String getBooking_start_time() {
        return booking_start_time;
    }

    public void setBooking_start_time(String booking_start_time) {
        this.booking_start_time = booking_start_time;
    }

    public String getBooking_end_time() {
        return booking_end_time;
    }

    public void setBooking_end_time(String booking_end_time) {
        this.booking_end_time = booking_end_time;
    }

    public String getTotal_ride_time() {
        return total_ride_time;
    }

    public void setTotal_ride_time(String total_ride_time) {
        this.total_ride_time = total_ride_time;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getNo_of_ride() {
        return no_of_ride;
    }

    public void setNo_of_ride(String no_of_ride) {
        this.no_of_ride = no_of_ride;
    }

    public String getRide_cost() {
        return ride_cost;
    }

    public void setRide_cost(String ride_cost) {
        this.ride_cost = ride_cost;
    }

    public String getAddition_cost() {
        return addition_cost;
    }

    public void setAddition_cost(String addition_cost) {
        this.addition_cost = addition_cost;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(String total_cost) {
        this.total_cost = total_cost;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getEnd_by() {
        return end_by;
    }

    public void setEnd_by(String end_by) {
        this.end_by = end_by;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
