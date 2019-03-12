package ru.cloudpayments.demo.screens.checkout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.cloudpayments.demo.R;
import ru.cloudpayments.demo.api.PayApi;
import ru.cloudpayments.demo.api.models.Transaction;
import ru.cloudpayments.demo.base.BaseActivity;
import ru.cloudpayments.demo.googlepay.PaymentsUtil;
import ru.cloudpayments.demo.managers.CartManager;
import ru.cloudpayments.demo.models.Product;
import ru.cloudpayments.demo.support.Constants;
import ru.cloudpayments.sdk.cp_card.CPCard;
import ru.cloudpayments.sdk.three_ds.ThreeDSDialogListener;
import ru.cloudpayments.sdk.three_ds.ThreeDsDialogFragment;

public class CheckoutActivity extends BaseActivity implements ThreeDSDialogListener {

    private static final int CARD_NUMBER_TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int CARD_NUMBER_TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int CARD_NUMBER_DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int CARD_NUMBER_DIVIDER_POSITION = CARD_NUMBER_DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char CARD_NUMBER_DIVIDER = ' ';

    private static final int CARD_DATE_TOTAL_SYMBOLS = 5; // size of pattern MM/YY
    private static final int CARD_DATE_TOTAL_DIGITS = 4; // max numbers of digits in pattern: MM + YY
    private static final int CARD_DATE_DIVIDER_MODULO = 3; // means divider position is every 3rd symbol beginning with 1
    private static final int CARD_DATE_DIVIDER_POSITION = CARD_DATE_DIVIDER_MODULO - 1; // means divider position is every 2nd symbol beginning with 0
    private static final char CARD_DATE_DIVIDER = '/';

    private static final int CARD_CVC_TOTAL_SYMBOLS = 3;

    @BindView(R.id.text_total)
    TextView textViewTotal;

    @BindView(R.id.edit_card_number)
    EditText editTextCardNumber;

    @BindView(R.id.edit_card_date)
    EditText editTextCardDate;

    @BindView(R.id.edit_card_cvc)
    EditText editTextCardCVC;

    @BindView(R.id.edit_card_holder_name)
    EditText editTextCardHolderName;

    // GOOGLE PAY
    @BindView(R.id.pwg_button)
    View buttonPwg;

    @BindView(R.id.pwg_status)
    TextView textViewPwgStatus;

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    private PaymentsClient paymentsClient;

    private int total = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_checkout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setTitle(R.string.checkout_title);

        initTotal();

        // GOOGLE PAY

        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        paymentsClient = PaymentsUtil.createPaymentsClient(this);

        checkIsReadyToPay();

        buttonPwg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPayment(view);
            }
        });
    }

    private void initTotal() {

        for (Product product : CartManager.getInstance().getProducts()) {
            total += Integer.parseInt(product.getPrice());
        }

        textViewTotal.setText(getString(R.string.checkout_total) + " " + total + " " + getString(R.string.main_rub));
    }

    @OnTextChanged(value = R.id.edit_card_number, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardNumberTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_NUMBER_TOTAL_SYMBOLS, CARD_NUMBER_DIVIDER_MODULO, CARD_NUMBER_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_NUMBER_TOTAL_DIGITS), CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.edit_card_date, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardDateTextChanged(Editable s) {
        if (!isInputCorrect(s, CARD_DATE_TOTAL_SYMBOLS, CARD_DATE_DIVIDER_MODULO, CARD_DATE_DIVIDER)) {
            s.replace(0, s.length(), concatString(getDigitArray(s, CARD_DATE_TOTAL_DIGITS), CARD_DATE_DIVIDER_POSITION, CARD_DATE_DIVIDER));
        }
    }

    @OnTextChanged(value = R.id.edit_card_cvc, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onCardCVCTextChanged(Editable s) {
        if (s.length() > CARD_CVC_TOTAL_SYMBOLS) {
            s.delete(CARD_CVC_TOTAL_SYMBOLS, s.length());
        }
    }

    private boolean isInputCorrect(Editable s, int size, int dividerPosition, char divider) {
        boolean isCorrect = s.length() <= size;
        for (int i = 0; i < s.length(); i++) {
            if (i > 0 && (i + 1) % dividerPosition == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String concatString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    @OnClick(R.id.text_phone)
    void onPhoneClick() {
        String phone = getString(R.string.main_phone);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @OnClick(R.id.text_email)
    void onEmailClick() {
        String email = getString(R.string.main_email);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.main_select_app)));
    }


    @OnClick(R.id.button_payment)
    void onPaymentClick() {
        String cardNumber = editTextCardNumber.getText().toString().replace(" ", "");
        String cardDate = editTextCardDate.getText().toString().replace("/", "");
        String cardCVC = editTextCardCVC.getText().toString();
        String cardHolderName = editTextCardHolderName.getText().toString();

        // Проверям номер карты.
        if (!CPCard.isValidNumber(cardNumber)) {
            showToast(R.string.checkout_error_card_number);
            return;
        }

        // Проверям срок действия карты.
        if (!CPCard.isValidExpDate(cardDate)) {
            showToast(R.string.checkout_error_card_date);
            return;
        }

        // Проверям cvc код карты.
        if (cardCVC.length() != 3) {
            showToast(R.string.checkout_error_card_cvc);
            return;
        }

        // После проверики, если все данные корректны, создаем объект CPCard, иначе при попытке создания объекта CPCard мы получим исключение.
        CPCard card = new CPCard(cardNumber, cardDate, cardCVC);

        // Создаем криптограмму карточных данных
        String cardCryptogram = null;
        try {
            // Чтобы создать криптограмму необходим PublicID (его можно посмотреть в личном кабинете)
            cardCryptogram = card.cardCryptogram(Constants.MERCHANT_PUBLIC_ID);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // Если данные карты введены корректно и криптограмма успешно созданна
        // используя методы API выполняем оплату по криптограмме
        if (cardCryptogram != null) {
            auth(cardCryptogram, cardHolderName, total);
        }
    }

    // Запрос на прведение одностадийного платежа
    private void charge(String cardCryptogramPacket, String cardHolderName, int amount) {
        compositeDisposable.add(PayApi
                .charge(cardCryptogramPacket, cardHolderName, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showLoading())
                .doOnEach(notification -> hideLoading())
                .subscribe(transaction -> {
                    checkResponse(transaction);
                }, this::handleError));
    }

    // Запрос на проведение двустадийного платежа
    private void auth(String cardCryptogramPacket, String cardHolderName, int amount) {
        compositeDisposable.add(PayApi
                .auth(cardCryptogramPacket, cardHolderName, total)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showLoading())
                .doOnEach(notification -> hideLoading())
                .subscribe(transaction -> {
                    checkResponse(transaction);
                }, this::handleError));
    }

    // Проверяем необходимо ли подтверждение с использованием 3DS
    private void checkResponse (Transaction transaction) {
        if (transaction.getPaReq() != null && transaction.getAcsUrl() != null) {
            // Показываем 3DS форму
            show3DS(transaction);
        } else {
            // Показываем результат
            showToast(transaction.getCardHolderMessage());
        }
    }

    private void show3DS(Transaction transaction) {
        // Открываем 3ds форму
        ThreeDsDialogFragment.newInstance(transaction.getAcsUrl(),
                transaction.getId(),
                transaction.getPaReq())
                .show(getSupportFragmentManager(), "3DS");
    }

    // Завершаем транзакцию после прохождения 3DS формы
    private void post3ds(String md, String paRes) {
        compositeDisposable.add(PayApi
                .post3ds(md, paRes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> showLoading())
                .doOnEach(notification -> hideLoading())
                .subscribe(transaction -> {
                    checkResponse(transaction);
                }, this::handleError));
    }

    // GOGGLE PAY
    private void checkIsReadyToPay() {
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        PaymentsUtil.isReadyToPay(paymentsClient).addOnCompleteListener(
                task -> {
                    try {
                        boolean result = task.getResult(ApiException.class);
                        setPwgAvailable(result);
                    } catch (ApiException exception) {
                        // Process error
                        Log.w("isReadyToPay failed", exception);
                    }
                });
    }

    private void setPwgAvailable(boolean available) {
        // If isReadyToPay returned true, show the button and hide the "checking" text. Otherwise,
        // notify the user that Pay with Google is not available.
        // Please adjust to fit in with your current user flow. You are not required to explicitly
        // let the user know if isReadyToPay returns false.
        if (available) {
            textViewPwgStatus.setVisibility(View.GONE);
            buttonPwg.setVisibility(View.VISIBLE);
        } else {
            textViewPwgStatus.setText(R.string.pwg_status_unavailable);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handlePaymentError(status.getStatusCode());
                        break;
                }

                // Re-enables the Pay with Google button.
                buttonPwg.setClickable(true);
                break;
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        // PaymentMethodToken contains the payment information, as well as any additional
        // requested information, such as billing and shipping address.
        //
        // Refer to your processor's documentation on how to proceed from here.
        PaymentMethodToken token = paymentData.getPaymentMethodToken();

        // getPaymentMethodToken will only return null if PaymentMethodTokenizationParameters was
        // not set in the PaymentRequest.
        if (token != null) {
            String billingName = paymentData.getCardInfo().getBillingAddress().getName();
            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show();

            // Use token.getToken() to get the token string.
            Log.d("GooglePaymentToken", token.getToken());

            charge(token.getToken(), "Google Pay", total);
        }
    }

    private void handlePaymentError(int statusCode) {
        // At this stage, the user has already seen a popup informing them an error occurred.
        // Normally, only logging is required.
        // statusCode will hold the value of any constant from CommonStatusCode or one of the
        // WalletConstants.ERROR_CODE_* constants.
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    // This method is called when the Pay with Google button is clicked.
    public void requestPayment(View view) {
        // Disables the button to prevent multiple clicks.
        buttonPwg.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = PaymentsUtil.microsToString(total);

        TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        PaymentDataRequest request = PaymentsUtil.createPaymentDataRequest(transaction);
        Task<PaymentData> futurePaymentData = paymentsClient.loadPaymentData(request);

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        AutoResolveHelper.resolveTask(futurePaymentData, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    @Override
    public void onAuthorizationCompleted(String md, String paRes) {
        post3ds(md, paRes);
    }

    @Override
    public void onAuthorizationFailed(String html) {
        showToast("AuthorizationFailed: " + html);
    }
}
