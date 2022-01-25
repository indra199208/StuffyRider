package ivan.infotechpvt.stuffyrider.allurl;

public class AllUrl {


    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static String KEY_PASSWORD = null;
    public static String USER_NAME = "USER_NAME";
    public static String baseUrl;
    public static String loginUrl;
    public static String rideListUrl;
    public static String bookingrideUrl;
    public static String cancelBookingUrl;
    public static String StartBookingUrl;
    public static String StopRideUrl;
    public static String RidehistroyUrl;
    public static String RidehistroyInprogressCompleteUrl;
    public static String ListbystatusUrl;
    public static String changePasswordUrl;
    public static String ForgotPasswordUrl;
    public static String ResetPasswordUrl;
    public static String ridedetailsUrl;
    public static String paymentStatusUrl;
    public static String paidUrl;
    public static String customerList;
    public static String stopallride;
    public static String startallride;




    static {

//        baseUrl = "http://dev6.ivantechnology.in/stuffyridersenterprises/adminpanel/api/";
        baseUrl = "https://www.stuffyrider.com/app/api/";
        loginUrl = baseUrl + "v1/login";
        rideListUrl = baseUrl+"v1/ridelist";
        bookingrideUrl = baseUrl+"v1/createbooking";
        cancelBookingUrl = baseUrl+"v1/cancel_ride";
        StartBookingUrl = baseUrl + "v1/ride_start";
        StopRideUrl = baseUrl+"v1/ride_end";
        RidehistroyUrl = baseUrl+"v1/booking_list";
        RidehistroyInprogressCompleteUrl = baseUrl+"v1/booking_list_by_status";
        ListbystatusUrl = baseUrl+"v1/ride_list_by_status";
        changePasswordUrl = baseUrl+"v1/changepassword";
        ForgotPasswordUrl = baseUrl+"v1/forgotpass";
        ResetPasswordUrl = baseUrl+"v1/resetpassword";
        ridedetailsUrl = baseUrl+"v1/ride_list_by_booking";
        paymentStatusUrl= baseUrl+"v1/booking_list_by_payment_status";
        paidUrl = baseUrl+"v1/change_payment_status";
        customerList = baseUrl+"v1/ridelist_by_customer";
        stopallride = baseUrl+"v1/all_ride_stop";
        startallride = baseUrl+"v1/all_ride_start";



        USER_NAME = "user_name";
        KEY_PASSWORD = "password";
    }
}
