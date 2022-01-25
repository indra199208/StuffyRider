package ivan.infotechpvt.stuffyrider.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
    @Multipart
    @POST("v1/imageupload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file,
                                   @Part("type") RequestBody requestBody,
                                   @Header("Authorization") String Authorization);

    @FormUrlEncoded
    @POST("customer/postOrder")
    Call<ResponseBody> postOrder(@Header("Authorization") String Authorization,
                                 @Field("userType") String userType,
                                 @Field("customerId") String customerId,
                                 @Field("vendorId") String vendorId,
                                 @Field("price") String price,
                                 @Field("specialInstruction") String specialInstruction,
                                 @Field("discount") String discount,
                                 @Field("finalPrice") String finalPrice,
                                 @Field("promocodeId") String promocodeId,
                                 @Field("offerId") String offerId,
                                 @Field("items") String items,
                                 @Field("latitude") String latitude,
                                 @Field("longitude") String longitude,
                                 @Field("appType") String appType,
                                 @Field("deliveryPreference") String deliveryPreference,
                                 @Field("addressId") String addressId,
                                 @Field("orderType") String orderType,
                                 @Field("cartId") String cartId);

}

