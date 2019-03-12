# Использование  

Приложение CloudPayments Checkout Example демонстрирует работу Android приложения с платежным шлюзом CloudPayments, а так же работу с Google Pay.

Схемы проведения платежа http://cloudpayments.ru/Docs/Integration#schemes

## Инсталяция
git clone https://github.com/cloudpayments/CloudPayments_AndroidCheckout.git

## Описание работы приложения с SDK CloudPayments

SDK CloudPayments позволяет:

* Проводить проверку карточного номера на корректность

```
Card card = CardFactory.create(String number);
boolean card.isValidNumber();

```

* Определять тип платежной системы

```

Card card = CardFactory.create(java.lang.String number);
String card.getType();

```

* Шифровать карточные данные и создавать криптограмму для отправки на сервер

```

Card card = CardFactory.create(String number);
String card.cardCryptogram(String publicId);

```
## Подключение Google Pay API для клиентов CloudPayments

https://cloudpayments.ru/docs/googlepay - о Google Pay

[https://developers.google.com/payments/setup](https://developers.google.com/payments/setup) \- документация Google, по подключению Google Pay API в приложение.

[https://github.com/android-pay/paymentsapi-quickstart](https://github.com/android-pay/paymentsapi-quickstart) -  пример использования Google Pay API от Google.

ВАЖНО:

При формирования параметров для запроса токена необходимо указать тип оплаты через шлюз (Wallet-Constants.PAYMENT\_METHOD\_TOKENIZATION\_TYPE\_PAYMENT_GATEWAY) и добавить два параметра:

1) gateway: cloudpayments
2) gatewayMerchantId: Ваш Public ID, его можно посмотреть в личном в Личном кабинете: [https://merchant.cloudpayments.ru/](https://merchant.cloudpayments.ru/)

```
PaymentMethodTokenizationParameters  params =
PaymentMethodTokenizationParameters.newBuilder()
.setPaymentMethodTokenizationType(
WalletConstants.PAYMENT\_METHOD\_TOKENIZATION\_TYPE\_PAYMENT_GATEWAY)
.addParameter("gateway", "cloudpayments")
.addParameter("gatewayMerchantId", "Ваш Public ID")
.build();
```

После получения токена необходимо провести оплату:
String token = paymentData.getPaymentMethodToken().getToken();

## Проведение оплаты

В примере MERCHANT\_PUBLIC\_ID и MERCHANT\_API\_PASS это тестовые Public ID и пароль для API, Вам необходимо получить свои данные в личном кабинете на сайте CloudPayments.
Не храните пароль для API в мобильном приложении это не безопасно, приложение должно выполнять запросы согласно схеме через ваш сервер: https://cloudpayments.ru/Docs/MobileSDK

1) В приложении необходимо получить  токен от Google Pay либо получить карточные данные и создать на из основе криптограмму (токен уже является криптограммой каких либо модификаций с ним проводить нет необходимости);
2) Отправить криптограмму (токен) и все данные для платежа с мобильного устройства на ваш сервер; 
3) С сервера вашего сервера провести оплату через платежное API CloudPayments.

## Ключевые моменты

В демо-проекте частично используется код из библиотеки https://github.com/LivotovLabs/3DSView. Все права на код этой библиотеки принадлежат авторам библиотеки.
