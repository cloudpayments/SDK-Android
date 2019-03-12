package ru.cloudpayments.sdk.three_ds;

public interface ThreeDSDialogListener {

    void onAuthorizationCompleted(final String md, final String paRes);

    void onAuthorizationFailed(final String html);
}