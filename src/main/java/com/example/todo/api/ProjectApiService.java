package com.example.todo.api;

import com.example.todo.model.Project;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProjectApiService {

    @POST("api/v1/project")
    Call<ResponseBody> create(@Body final Project project);
    @GET("api/v1/project")
    Call<ResponseBody> getAll();
    @DELETE("api/v1/project/{projectId}")
    Call<ResponseBody> delete(@Path("projectId") final String projectId);
    @FormUrlEncoded
    @PUT("api/v1/project/{projectId}")
    Call<ResponseBody> updateOrder(@Path("projectId") final String projectId,
                                   @Field("sort_order") final int projectOrder);
}
