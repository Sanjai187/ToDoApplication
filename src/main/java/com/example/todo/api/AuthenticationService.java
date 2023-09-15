package com.example.todo.api;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.example.todo.model.Credential;
import com.example.todo.model.ResetPassword;
import com.example.todo.model.SignUp;
import com.example.todo.model.UserProfile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthenticationService {

    private ApiService apiService;

    public AuthenticationService(final String url) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public void signUp(final UserProfile userProfile, final Credential credential, final ApiResponseCallBack apiResponseCallBack) {
        final SignUp signUp = new SignUp(userProfile, credential);
        final Call<ResponseBody> responseBodyCall = apiService.signUp(signUp);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull final Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    apiResponseCallBack.onSuccess(response.body().toString());
                }
                apiResponseCallBack.onError(String.format("Response message %s", response.message()));
            }

            @Override
            public void onFailure(@NonNull final Call<ResponseBody> call, @NonNull final Throwable throwable) {
                apiResponseCallBack.onError(throwable.getMessage());
            }
        });
    }

    public void signIn(final Credential credential, final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.singIn(credential);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    apiResponseCallBack.onSuccess(response.body().toString());
                }
                apiResponseCallBack.onError(String.format("Response message %s", response.message()));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                apiResponseCallBack.onError(throwable.getMessage());
            }
        });
    }

    public void resetPassword(final Credential credential, final String newHint, final ApiResponseCallBack apiResponseCallBack) {
        final ResetPassword resetPassword = new ResetPassword(credential, newHint);
        final Call<ResponseBody> responseBodyCall = apiService.resetPassword(resetPassword);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(@NonNull final Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    apiResponseCallBack.onSuccess(response.body().toString());
                }
                apiResponseCallBack.onError(String.format("Response message %s", response.message()));
            }

            @Override
            public void onFailure(@NonNull final Call<ResponseBody> call, @NonNull final Throwable throwable) {
                apiResponseCallBack.onError(throwable.getMessage());
            }
        });
    }

    public interface ApiResponseCallBack {

        void onSuccess(final String response);
        void onError(final String errorMessage);
    }
}
