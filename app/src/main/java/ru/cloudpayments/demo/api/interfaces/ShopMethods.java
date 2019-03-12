package ru.cloudpayments.demo.api.interfaces;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;
import ru.cloudpayments.demo.api.ApiMap;
import ru.cloudpayments.demo.models.Product;

public interface ShopMethods {

    @GET("products")
    Observable<List<Product>> getProducts(@Header("Content-Type") String contentType, @Header("Authorization") String authKey, @QueryMap ApiMap args);

}
