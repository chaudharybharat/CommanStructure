package com.example.agc_linux.accounting.uicustome;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.agc_linux.accounting.R;


public class ProgressHUD extends Dialog {

    private static ProgressHUD dialog;


    public ProgressHUD(Context context) {
        super(context);
    }

    public ProgressHUD(Context context, int theme) {
        super(context, theme);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView
                .getBackground();
        spinner.start();
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            //  findViewById(R.id.message).setVisibility(View.VISIBLE);
            // TextView txt = (TextView) findViewById(R.id.message);
            //txt.setText(message);
            //txt.invalidate();
        }
    }

    public static ProgressHUD show(Context context, CharSequence message,
                                   boolean indeterminate, boolean cancelable,
                                   OnCancelListener cancelListener) {

        dialog = new ProgressHUD(context, R.style.NewDialog);
        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_hud);

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP){
            dialog.findViewById(R.id.pb_load).setBackground(context.getResources().getDrawable(R.drawable.custom_progress_bar));
        }

        if (message == null || message.length() == 0) {
            //dialog.findViewById(R.id.message).setVisibility(View.GONE);
        } else {
            //TextView txt = (TextView) dialog.findViewById(R.id.message);
            //txt.setText(message);
        }
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.show();
        return dialog;
    }

    public static void dialogDismiss() {
        dialog.dismiss();
    }

}
