package ru.cloudpayments.demo.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.cloudpayments.demo.api.interfaces.PayMethods;

public class PayApiFactory {
    private static final String HOST = "https://wp-demo.cloudpayments.ru/";
    private static final String API_URL = "";

    private static final int TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 20;
    private static final int CONNECT_TIMEOUT = 10;
    private static final HttpLoggingInterceptor LOGGING_INTERCEPTOR = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    public static final String API_ENDPOINT = HOST + API_URL;

    // API implementations
    public static PayMethods getPayMethods() {
        return getRetrofit().create(PayMethods.class);
    }
    // API implementations

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(LOGGING_INTERCEPTOR)
            .build();

    private static final Gson GSON = new GsonBuilder()
            .setLenient()
            .create();

    @NonNull
    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(CLIENT)
                .build();
    }
}
