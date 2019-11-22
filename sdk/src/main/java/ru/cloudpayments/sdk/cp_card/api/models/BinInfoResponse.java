package ru.cloudpayments.sdk.cp_card.api.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class BinInfoResponse {

    @Nullable
    @SerializedName("Model")
    private BinInfo binInfo;

    @SerializedName("Success")
    private boolean success;

    @Nullable
    public BinInfo getBinInfo() {
        return binInfo;
    }

    public void setBinInfo(@Nullable BinInfo binInfo) {
        this.binInfo = binInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}


