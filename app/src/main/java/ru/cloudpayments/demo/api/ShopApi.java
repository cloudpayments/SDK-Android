package ru.cloudpayments.demo.api;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.List;

import io.reactivex.Observable;
import ru.cloudpayments.demo.models.Product;
import ru.cloudpayments.demo.support.Constants;

public class ShopApi {

    private static final String CONTENT_TYPE = "application/json";

    public static Observable<List<Product>> getProducts() {

        ApiMap args = ApiMap
                .builder()
                .build();

        return ShopApiFactory.getShopMethods()
                .getProducts(CONTENT_TYPE, getShopAuthToken(), args);
    }

    private static String getShopAuthToken() {
        byte[] data = new byte[0];
        try {
            data = (Constants.CONSUMER_KEY + ":" + Constants.CONSUMER_SECRET).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }
}