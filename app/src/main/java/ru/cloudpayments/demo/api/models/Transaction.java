package ru.cloudpayments.demo.api.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("TransactionId")
    private String id;

    @Nullable
    @SerializedName("ReasonCode")
    private int reasonCode;

    @Nullable
    @SerializedName("CardHolderMessage")
    private String cardHolderMessage;

    // 3DS Begin
    @Nullable
    @SerializedName("PaReq")
    private String paReq;

    @Nullable
    @SerializedName("AcsUrl")
    private String acsUrl;
    // 3DS End

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(@Nullable int reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Nullable
    public String getCardHolderMessage() {
        return cardHolderMessage;
    }

    public void setCardHolderMessage(@Nullable String cardHolderMessage) {
        this.cardHolderMessage = cardHolderMessage;
    }

    @Nullable
    public String getPaReq() {
        return paReq;
    }

    public void setPaReq(@Nullable String paReq) {
        this.paReq = paReq;
    }

    @Nullable
    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(@Nullable String acsUrl) {
        this.acsUrl = acsUrl;
    }
}
