package com.stewardbank.omnichannel.business.rest;

import com.stewardbank.omnichannel.business.domain.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @uthor Tasu Muzinda
 */
public interface LoginService {

    @GET("omnichannel-client-rest/rest/mobile/login/get-user")
    Call<User> login(@Query("userName") String userName);
}
