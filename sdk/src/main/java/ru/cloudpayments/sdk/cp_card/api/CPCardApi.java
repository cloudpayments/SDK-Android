package ru.cloudpayments.sdk.cp_card.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.cloudpayments.sdk.cp_card.api.models.BinInfo;
import ru.cloudpayments.sdk.cp_card.api.models.BinInfoResponse;

public class CPCardApi {

    public interface CompleteBinInfoListener {

        void onCompleted(final BinInfo binInfo);
    }

    public interface ErrorListener {

        void onError(final String message);
    }

    private final Context context;
    private final Gson gson;

    public CPCardApi(Context context) {
        this.context = context;
        this.gson = createGson();
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    public void getBinInfo(String firstSixDigits, final CompleteBinInfoListener completeListener, final ErrorListener errorListener) {

        firstSixDigits = firstSixDigits.replace(" ", "");

        if (firstSixDigits.length() < 6) {
            errorListener.onError("You must specify the first 6 digits of the card number");
            return;
        }

        firstSixDigits = firstSixDigits.substring(0, 6);

        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://widget.cloudpayments.ru/Home/BinInfo?firstSixDigits=" + firstSixDigits;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        BinInfoResponse binInfoResponse = gson.fromJson(response, BinInfoResponse.class);

                        if (binInfoResponse.isSuccess() && binInfoResponse.getBinInfo() != null) {
                            completeListener.onCompleted(binInfoResponse.getBinInfo());
                        } else {
                            errorListener.onError("Unable to determine bank");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onError(error.getMessage());
            }
        });

        queue.add(stringRequest);
    }
}
