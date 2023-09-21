package com.example.todo.api;

import androidx.annotation.NonNull;

import com.example.todo.model.Todo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodoItemService {

    private final TodoApiService apiService;

    public TodoItemService(final String baseUrl, final String token) {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor(token));
        final OkHttpClient okHttpClient = httpClient.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        apiService = retrofit.create(TodoApiService.class);
    }

    public void create(final String todo, final String projectId, final AuthenticationService.ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.create(todo, projectId);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void getAll(final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.getAll();

        executeRequest(call, callBack);
    }

    public void delete(final String id, final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.delete(id);

        executeRequest(call, callBack);
    }

    public void updateOrder(final Todo todo, final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateOrder(todo.getId(),
                Math.toIntExact(todo.getOrder()), todo.getParentId());

        executeRequest(call, callBack);
    }

    private void executeRequest(final Call<ResponseBody> responseBodyCall, final AuthenticationService.ApiResponseCallBack apiResponseCallBack) {
        responseBodyCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull final Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    try {
                        apiResponseCallBack.onSuccess(response.body().string());
                    } catch (IOException | JSONException exception) {
                        throw new RuntimeException(exception);
                    }
                } else {
                    assert response.errorBody() != null;

                    try {
                        final String errorBody = response.errorBody().string();
                        final JSONObject jsonObject = new JSONObject(errorBody);
                        final String message = jsonObject.getString("message");

                        apiResponseCallBack.onError(message);
                    } catch (IOException | JSONException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull final Call<ResponseBody> call, @NonNull final Throwable throwable) {
                apiResponseCallBack.onError(throwable.getMessage());
            }
        });
    }

}
