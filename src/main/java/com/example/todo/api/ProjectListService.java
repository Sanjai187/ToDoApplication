package com.example.todo.api;

import androidx.annotation.NonNull;

import com.example.todo.model.Project;

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

public class ProjectListService {

    private final ProjectApiService apiService;

    public ProjectListService(final String baseUrl, final String token) {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor(token));
        final OkHttpClient okHttpClient = httpClient.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        apiService = retrofit.create(ProjectApiService.class);
    }

    public void create(final Project project, final AuthenticationService.ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.create(project);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void getAll(final AuthenticationService.ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.getAll();

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void delete(final String id, final AuthenticationService.ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.delete(id);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void updateOrder(final Project project,
                            final AuthenticationService.ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateOrder(project.getId(),
                Math.toIntExact(project.getOrder()));

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