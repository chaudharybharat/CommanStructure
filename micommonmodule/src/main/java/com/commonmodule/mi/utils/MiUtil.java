package com.commonmodule.mi.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;


/**
 * Created by mind on 2/10/15.
 */
public class MiUtil {


    /**
     * Gives the device independent constant which can be used for scaling images, manipulating view
     * sizes and changing dimension and display pixels etc.
     * **
     */
    public static float getDensityMultiplier(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into app.com.airstylz.db
     * @param context Context to get resources and device specific display metrics
     * @return A int value to represent dp equivalent to px value
     */
    public static int getDip(int px, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }


    /**
     * Checks if the SD Card is mounted on the device.
     *
     * @deprecated use {@link #isSdCardMounted()}
     * **
     */
    public static boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;

        return false;
    }

    public static boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;

        return false;
    }

    /**
     *
     * @param context application context.
     * @param positiveButtonText Text which will set as positive button text.
     * @param negativeButtonText Text which will set as negative button text.
     * @param message message which show in dialog.
     */
    public static final void showAlertDialog(Context context, String positiveButtonText, String negativeButtonText, String message) {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void intentToWeb(Activity activity,String str_url){
        try{
            if(!str_url.startsWith("http://")){
                str_url = "http://"+str_url;
            }
            String url = str_url;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
