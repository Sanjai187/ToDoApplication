package com.example.todo.api;

import com.example.todo.model.Credential;
import com.example.todo.model.ResetPassword;
import com.example.todo.model.SignIn;
import com.example.todo.model.SignUp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/v1/user/signup")
    Call<ResponseBody> signUp(@Body final SignUp signUp);
    @POST("api/v1/user/login")
    Call<ResponseBody> singIn(@Body final SignIn signIn);
    @POST("api/v1/user/reset/password")
    Call<ResponseBody> resetPassword(@Body final ResetPassword signIn);
}
