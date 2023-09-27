package com.example.todo.api;

import androidx.annotation.NonNull;

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

    public void signUp(final UserProfile userProfile, final Credential credential, final ApiResponseCallBack apiResponseCallBack) {
        final SignUp signUp = new SignUp(userProfile, credential);
        final Call<ResponseBody> responseBodyCall = apiService.signUpRequest(signUp);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void signIn(final Credential credential, final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.singInRequest(credential);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void resetPassword(final Credential credential, final String newHint, final ApiResponseCallBack apiResponseCallBack) {
        final ResetPassword resetPassword = new ResetPassword(credential, newHint);
        final Call<ResponseBody> responseBodyCall = apiService.resetPasswordRequest(resetPassword);

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void getUserDetail(final ApiResponseCallBack apiResponseCallBack) {
        final Call<ResponseBody> responseBodyCall = apiService.getUserDetail();

        executeRequest(responseBodyCall, apiResponseCallBack);
    }

    public void updateUserDetail(final UserProfile userProfile, final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateUserDetail(userProfile);

        executeRequest(call, callBack);
    }

    public void getSystemSetting(final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.getSystemSetting();

        executeRequest(call, callBack);
    }

    public void updateSystemSetting(final String fontFamily, final int fontSize, final String color,
                                    final ApiResponseCallBack callBack) {
        final Call<ResponseBody> call = apiService.updateSystemSetting(fontFamily, fontSize, color);

        executeRequest(call, callBack);
    }

    private void executeRequest(final Call<ResponseBody> responseBodyCall, final AuthenticationService.ApiResponseCallBack apiResponseCallBack) {
        responseBodyCall.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull final Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    try {
                        try {
                            apiResponseCallBack.onSuccess(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (JSONException exception) {
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


    public interface ApiResponseCallBack {

        void onSuccess(final String response) throws JSONException;
        void onError(final String errorMessage);
    }
}