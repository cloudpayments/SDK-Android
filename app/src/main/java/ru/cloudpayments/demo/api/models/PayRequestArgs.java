package ru.cloudpayments.demo.api.models;

import com.google.gson.annotations.SerializedName;

public class PayRequestArgs {

    @SerializedName("amount")
    private String amount; // Сумма платежа (Обязательный)

    @SerializedName("currency")
    private String currency; // Валюта (Обязательный)

    @SerializedName("name")
    private String name; // Имя держателя карты в латинице (Обязательный для всех платежей кроме Apple Pay и Google Pay)

    @SerializedName("card_cryptogram_packet")
    private String cardCryptogramPacket; // Криптограмма платежных данных (Обязательный)

    @SerializedName("invoice_id")
    private String invoiceId; // Номер счета или заказа в вашей системе (необязательный)

    @SerializedName("description")
    private String description; // Описание оплаты в свободной форме (необязательный)

    @SerializedName("account_id")
    private String accountId; // Идентификатор пользователя в вашей системе (необязательный)

    @SerializedName("json_data")
    private String jsonData; // Любые другие данные, которые будут связаны с транзакцией (необязательный)

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardCryptogramPacket() {
        return cardCryptogramPacket;
    }

    public void setCardCryptogramPacket(String cardCryptogramPacket) {
        this.cardCryptogramPacket = cardCryptogramPacket;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}
