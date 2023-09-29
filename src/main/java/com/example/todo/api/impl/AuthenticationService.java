package com.example.todo.api.impl;

import androidx.annotation.NonNull;

import com.example.todo.api.ApiService;
import com.example.todo.model.Credential;
import com.example.todo.model.ResetPassword;
import com.example.todo.model.SignUp;
import com.example.todo.model.UserProfile;

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

public class AuthenticationService {

    private final ApiService apiService;

    public AuthenticationService(final String url) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public AuthenticationService(final String url, final String token) {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor(token));
        final OkHttpClient okHttpClient = httpClient.build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void signUp(final UserProfile userProfile, final Credential credential,
                       final ApiResponseCallBack apiResponseCallBack) {
        final SignUp signUp = new SignUp(userProfile, credential);
        final Call<ResponseBody> responseBodyCall = apiService.signUpRequest(signUp);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void signIn(final Credential credential,
                       final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.singInRequest(credential);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void resetPassword(final Credential credential, final String newHint,
                              final ApiResponseCallBack apiResponseCallBack) {
        final ResetPassword resetPassword = new ResetPassword(credential, newHint);
        final Call<ResponseBody> responseBodyCall = apiService.resetPasswordRequest(resetPassword);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void getUserDetail(final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.getUserDetail();

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void updateUserDetail(final UserProfile userProfile,
                                 final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.updateUserDetail(userProfile);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void getSystemSetting(final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.getSystemSetting();

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void updateFontFamily(final String fontFamily,
                                 final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.updateFontFamily(fontFamily);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void updateFontSize(final int fontSize, final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.updateFontSize(fontSize);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void updateColor(final String fontColor, final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.updateColor(fontColor);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    private void executeRequest(final Call<ResponseBody> responseBodyCall,
                                final ApiResponseCallBack apiResponseCallBack) {
        responseBodyCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull final Call<ResponseBody> call,
                                   @NonNull final Response<ResponseBody> response) {
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
            public void onFailure(@NonNull final Call<ResponseBody> call,
                                  @NonNull final Throwable throwable) {
                apiResponseCallBack.onError(throwable.getMessage());
            }
        });
    }


    public interface ApiResponseCallBack {

        void onSuccess(final String response) throws JSONException;
        void onError(final String errorMessage);
    }
}