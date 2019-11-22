package ru.cloudpayments.sdk.cp_card.api.models;

import com.google.gson.annotations.SerializedName;

public class BinInfo {

    @SerializedName("LogoUrl")
    private String logoUrl;

    @SerializedName("BankName")
    private String bankName;

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getBankName() {
        return bankName;
    }
}
