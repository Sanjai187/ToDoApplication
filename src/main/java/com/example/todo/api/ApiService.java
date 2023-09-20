package com.example.todo.api;

import com.example.todo.model.Credential;
import com.example.todo.model.Project;
import com.example.todo.model.ResetPassword;
import com.example.todo.model.SignUp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/v1/user/signup")
    Call<ResponseBody> signUpRequest(@Body final SignUp signUp);
    @POST("api/v1/user/login")
    Call<ResponseBody> singInRequest(@Body final Credential credential);
    @POST("api/v1/user/reset/password")
    Call<ResponseBody> resetPasswordRequest(@Body final ResetPassword resetPassword);
    @POST("api/v1/project")
    Call<ResponseBody> createProjectRequest(@Body final Project project);
    @GET("api/v1/project")
    Call<ResponseBody> getAllProject();
    @DELETE("api/v1/project/{projectId}")
    Call<ResponseBody> delete(@Path("projectId") final String projectId);
}