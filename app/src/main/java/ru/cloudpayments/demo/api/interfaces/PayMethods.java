package ru.cloudpayments.demo.api.interfaces;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ru.cloudpayments.demo.api.models.PayRequestArgs;
import ru.cloudpayments.demo.api.models.Post3dsRequestArgs;
import ru.cloudpayments.demo.api.models.Transaction;
import ru.cloudpayments.demo.api.response.PayApiResponse;

public interface PayMethods {

    @POST("cp_charge.php")
    Observable<PayApiResponse<Transaction>> charge(@Header("Content-Type") String contentType, @Body PayRequestArgs args);

    @POST("cp_auth.php")
    Observable<PayApiResponse<Transaction>> auth(@Header("Content-Type") String contentType, @Body PayRequestArgs args);

    @POST("cp_post3ds.php")
    Observable<PayApiResponse<Transaction>> post3ds(@Header("Content-Type") String contentType, @Body Post3dsRequestArgs args);
}
