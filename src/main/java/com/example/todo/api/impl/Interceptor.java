package com.example.todo.api.impl;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class Interceptor implements okhttp3.Interceptor {

    private final String token;

    public Interceptor(final String token) {
        this.token = token;
    }

    @NonNull
    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final Request newRequest = request.newBuilder().header("Authorization", String.format("Bearer %s", token)).build();

        return chain.proceed(newRequest);
    }
}