package com.example.todo.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TodoApiService {

    @FormUrlEncoded
    @POST("api/v1/item")
    Call<ResponseBody> create(@Field("name") final String name,
                              @Field("project_id") final String projectId);
    @GET("api/v1/item")
    Call<ResponseBody> getAll();
    @DELETE("api/v1/item/{itemId}")
    Call<ResponseBody> delete(@Path("itemId") final String itemId);
    @FormUrlEncoded
    @PUT("api/v1/item/{itemId}")
    Call<ResponseBody> updateOrder(@Path("itemId") final String itemId,
                                   @Field("sort_order") final int sortingOrder,
                                   @Field("project_id") final String projectId);
    @FormUrlEncoded
    @PUT("api/v1/item/{itemId}")
    Call<ResponseBody> updateStatus(@Path("itemId") final String id,
                                    @Field("is_completed") final boolean isCompleted,
                                    @Field("project_id") final String parentId);

}
