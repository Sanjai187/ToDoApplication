package com.example.todo.api;

import com.example.todo.model.Credential;
import com.example.todo.model.ResetPassword;
import com.example.todo.model.SignUp;
import com.example.todo.model.UserProfile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

    @POST("api/v1/user/signup")
    Call<ResponseBody> signUpRequest(@Body final SignUp signUp);
    @POST("api/v1/user/login")
    Call<ResponseBody> singInRequest(@Body final Credential credential);
    @POST("api/v1/user/reset/password")
    Call<ResponseBody> resetPasswordRequest(@Body final ResetPassword resetPassword);
    @GET("api/v1/user/details")
    Call<ResponseBody> getUserDetail();
    @PUT("api/v1/user/details")
    Call<ResponseBody> updateUserDetail(@Body final UserProfile userProfile);
    @GET("api/v1/user/system/settings")
    Call<ResponseBody> getSystemSetting();
    @FormUrlEncoded
    @PUT("api/v1/user/system/settings")
    Call<ResponseBody> updateSystemSetting(@Field("font_family") final String font,
                                           @Field("font_size") final int size,
                                           @Field("color") final String color);
}