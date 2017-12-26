package com.stewardbank.omnichannel.business.rest;

import com.stewardbank.omnichannel.business.domain.Customer;
import com.stewardbank.omnichannel.business.util.HttpStatus;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * @uthor Tasu Muzinda
 */
public interface CustomerService {

    @POST("omnichannel-client-rest/rest/mobile/customer/save")
    Call<HttpStatus> addCustomer(
            @Body Customer customer
    );

    @Multipart
    @POST("/omnichannel-client-portal/upload/signature-upload")
    Call<ResponseBody> postSignatureImage(
            @Part MultipartBody.Part image,
            @Part("file") RequestBody file,
            @Query("idNumber") String idNumber);

    @Multipart
    @POST("/omnichannel-client-portal/upload/id-upload")
    Call<ResponseBody> postIdImage(
            @Part MultipartBody.Part image,
            @Part("file") RequestBody file,
            @Query("idNumber") String idNumber);

    @Multipart
    @POST("/omnichannel-client-portal/upload/head-shot-upload")
    Call<ResponseBody> postHeadShotImage(
            @Part MultipartBody.Part image,
            @Part("file") RequestBody file,
            @Query("idNumber") String idNumber);

    @Multipart
    @POST("/omnichannel-client-portal/upload/proof-of-residence-upload")
    Call<ResponseBody> postProofOfResidenceImage(
            @Part MultipartBody.Part image,
            @Part("file") RequestBody file,
            @Query("idNumber") String idNumber);
}
