package com.example.agc_linux.accounting.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;

import com.example.agc_linux.accounting.MainActivity;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.uicustome.ProgressHUD;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import static com.example.agc_linux.accounting.dialog.SweetEdit_Dialog.customerTranscation;

/**
 * Created by agc-linux on 14/8/17.
 */

public class CommnMethod {
    public static ProgressHUD mProgressHUD;
    public static boolean is_marshmallow(){

        if (Build.VERSION.SDK_INT >= 23){
            // Do something for lollipop and above versions
            return true;

        } else{
            return false;
            // do something for phones running an SDK before lollipop
        }
    }

    public static boolean isPermissionNotGranted(Context context, String[] permissions) {
        boolean flag = false;
        for (int i = 0; i < permissions.length; i++) {
            if (context.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                flag = true;
                break;
            }

        }
        return flag;
    }
public static String getTodayDate(){
    Date today = Calendar.getInstance().getTime();

    // (2) create a date "formatter" (the date format we want)
    SimpleDateFormat formatter = new SimpleDateFormat(StaticConfig.DATE_FORMATE);

    // (3) create a new String using the date format we want
    String today_date = formatter.format(today);
    return today_date;
}
public static String getTomorrowDate(){

    Calendar calendar = Calendar.getInstance();

    calendar.add(Calendar.DAY_OF_YEAR, 1);
    Date tomorrow = calendar.getTime();

    SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConfig.DATE_FORMATE);

    String tomorrowAsString = dateFormat.format(tomorrow);

    return tomorrowAsString;

}

    public static String firstlaterCapse(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void isProgressShowMessage(Context mContext, final String message) {
        try {
            if (mProgressHUD == null) {
                mProgressHUD = ProgressHUD.show(mContext,
                        "" + message, false, false, null);
            } else {
                if (!mProgressHUD.isShowing()) {
                    mProgressHUD = ProgressHUD.show(mContext,
                            "" + message, false, false, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String createUniqueId() {
        String ts = String.valueOf(System.currentTimeMillis());
        String rand = UUID.randomUUID().toString();
        return rand;
    }

    public static void isProgressShowNoMessage(Context mContext) {
        if (mProgressHUD == null) {
            mProgressHUD = ProgressHUD.show(mContext, null, false, false, null);
        } else {
            if (!mProgressHUD.isShowing()) {
                mProgressHUD = ProgressHUD.show(mContext, null, false, false,
                        null);
            }
        }
    }
    public static void isProgressShow(Context mContext) {
        try {
            if (mProgressHUD == null) {
                mProgressHUD = ProgressHUD.show(mContext,
                        "Please wait", false, false, null);
            } else {
                if (!mProgressHUD.isShowing()) {
                    mProgressHUD = ProgressHUD.show(mContext,
                            "Please wait", false, false, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void isProgressHide() {
        try {
            if (mProgressHUD != null) {

                if (mProgressHUD.isShowing()) {
                    mProgressHUD.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getCurrentDate(){
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date resultdate = new Date(yourmilliseconds);
        return sdf.format(resultdate).replace("/","_");
    }
    //______________________________________GET SURPLUS DAYS METHODS_______________________________________

    public static int getSurplusDays(int calenderDays, int calendarMonth, int calendarYear)
    {
        int surplusdays = 0;
        int totaldays = 0;

        //_____________________FOR MONTHS WITH 31 DAYS_________________
        if (calendarMonth == (0 | 2 | 4 | 6 | 7 | 9 | 11))
        {
            totaldays = 31;
            surplusdays = totaldays - calenderDays;
        }
        //_____________________FOR FEB HAVING 28 OR 29 DAYS____________
        else if (calendarMonth == 1)
        {
            if (calendarYear % 4 == 0)
            {
                totaldays = 29;
                surplusdays = totaldays - calenderDays;
            }
            else
            {
                totaldays = 28;
                surplusdays = totaldays - calenderDays;
            }
        }
        //_____________________FOR MONTHS WITH 30 DAYS_________________
        else
        {
            totaldays = 30;
            surplusdays = totaldays - calenderDays;
        }

        return surplusdays;
    }

    //______________________________________GET SURPLUS MONTHS METHODS FOR AGO_______________________________________

    public static int getSurplusMonth(int calendarMonth)
    {
        int surplusMonths = 0;
        int totalMonths = 12;
        surplusMonths = totalMonths - calendarMonth;
        return surplusMonths;
    }


    //______________________________________GET SURPLUS MINUTES METHODS_______________________________________

    public static int getSurplusMinutes(int minutes)
    {
        int surplusMinutes = 0;
        int totalminutes = 60;
        surplusMinutes = totalminutes - minutes;
        return surplusMinutes;
    }

}
