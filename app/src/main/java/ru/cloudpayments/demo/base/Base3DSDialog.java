package ru.cloudpayments.demo.base;

import android.app.Dialog;
import android.content.Context;

import ru.cloudpayments.demo.R;

public class Base3DSDialog extends Dialog {

    public static Base3DSDialog create(Context context) {
        final Base3DSDialog dialog = new Base3DSDialog(context, R.style.Theme_CustomDialog);
        dialog.setContentView(R.layout.progress_dialog);
        return dialog;
    }

    public Base3DSDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onBackPressed() {
    }
}
