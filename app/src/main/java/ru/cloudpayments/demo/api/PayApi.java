package ru.cloudpayments.demo.api;

import io.reactivex.Observable;
import ru.cloudpayments.demo.api.models.PayRequestArgs;
import ru.cloudpayments.demo.api.models.Post3dsRequestArgs;
import ru.cloudpayments.demo.api.models.Transaction;
import ru.cloudpayments.demo.api.response.PayApiResponse;

public class PayApi {

    private static final String CONTENT_TYPE = "application/json";

    public static Observable<Transaction> charge(String cardCryptogramPacket, String cardHolderName, int amount) {

        // Параметры:
        PayRequestArgs args = new PayRequestArgs();
        args.setAmount(Integer.toString(amount));  // Сумма платежа (Обязательный)
        args.setCurrency("RUB"); // Валюта (Обязательный)
        args.setName(cardHolderName); // Имя держателя карты в латинице (Обязательный для всех платежей кроме Apple Pay и Google Pay)
        args.setCardCryptogramPacket(cardCryptogramPacket); // Криптограмма платежных данных (Обязательный)
        args.setInvoiceId("1122"); // Номер счета или заказа в вашей системе (необязательный)
        args.setDescription("Оплата цветов"); // Описание оплаты в свободной форме (необязательный)
        args.setAccountId("123"); // Идентификатор пользователя в вашей системе (необязательный)
        args.setJsonData("{\"age\":27,\"name\":\"Ivan\",\"phone\":\"+79998881122\"}"); // Любые другие данные, которые будут связаны с транзакцией (необязательный)

        return PayApiFactory.getPayMethods()
                .charge(CONTENT_TYPE, args)
                .flatMap(PayApiResponse::handleError)
                .map(PayApiResponse::getData);
    }

    public static Observable<Transaction> auth(String cardCryptogramPacket, String cardHolderName, int amount) {

        // Параметры:
        PayRequestArgs args = new PayRequestArgs();
        args.setAmount(Integer.toString(amount));  // Сумма платежа (Обязательный)
        args.setCurrency("RUB"); // Валюта (Обязательный)
        args.setName(cardHolderName); // Имя держателя карты в латинице (Обязательный для всех платежей кроме Apple Pay и Google Pay)
        args.setCardCryptogramPacket(cardCryptogramPacket); // Криптограмма платежных данных (Обязательный)
        args.setInvoiceId("1122"); // Номер счета или заказа в вашей системе (необязательный)
        args.setDescription("Оплата цветов"); // Описание оплаты в свободной форме (необязательный)
        args.setAccountId("123"); // Идентификатор пользователя в вашей системе (необязательный)
        args.setJsonData("{\"age\":27,\"name\":\"Ivan\",\"phone\":\"+79998881122\"}"); // Любые другие данные, которые будут связаны с транзакцией (необязательный)

        return PayApiFactory.getPayMethods()
                .auth(CONTENT_TYPE, args)
                .flatMap(PayApiResponse::handleError)
                .map(PayApiResponse::getData);
    }

    public static Observable<Transaction> post3ds(String transactionId, String paRes) {

        Post3dsRequestArgs args = new Post3dsRequestArgs();
        args.setTransactionId(transactionId);
        args.setPaRes(paRes);

        return PayApiFactory.getPayMethods()
                .post3ds(CONTENT_TYPE, args)
                .flatMap(PayApiResponse::handleError)
                .map(PayApiResponse::getData);
    }
}