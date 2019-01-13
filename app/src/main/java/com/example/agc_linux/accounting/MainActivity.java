package com.example.agc_linux.accounting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonmodule.mi.Activity.MIActivity;
import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.dialog.SweetAlertDialog;
import com.example.agc_linux.accounting.fragment.AddCustomerFragment;
import com.example.agc_linux.accounting.fragment.BackupFragment;
import com.example.agc_linux.accounting.fragment.CustomerListFragment;
import com.example.agc_linux.accounting.fragment.GenrateXclFileFragment;
import com.example.agc_linux.accounting.fragment.HomeFragment;
import com.example.agc_linux.accounting.fragment.PaymentDateFragment;
import com.example.agc_linux.accounting.fragment.SeetingFragment;
import com.example.agc_linux.accounting.model.UserTable;
import com.example.agc_linux.accounting.util.BlurBuilder;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.example.agc_linux.accounting.util.ImageUtils;
import com.example.agc_linux.accounting.util.MyPreferences;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.agc_linux.accounting.dialog.SweetAlertDialog.WARNING_TYPE;


public class MainActivity extends MIActivity implements View.OnClickListener{
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.rl_left_drawer)
    RelativeLayout rl_left_drawer;
   @BindView(R.id.ll_main)
   LinearLayout ll_main;
    private float lastTranslate = 0.0f;
    @BindView(R.id.btn_back)
    ImageView btn_back;
    @BindView(R.id.btn_menu)
    ImageView btn_menu;
    @BindView(R.id.iv_serach)
    ImageView iv_serach;
    @BindView(R.id.rl_prfile_pic)
    SimpleDraweeView rl_blure_pic;
    @BindView(R.id.profile_pic)
    SimpleDraweeView profile_pic;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_iv_profile)
    SimpleDraweeView toolbar_iv_profile;
    LayoutInflater mInflater;
    @BindView(R.id.edt_serach)
    public EditText edt_serach;
    @BindView(R.id.iv_change_langaue)
    ImageView iv_change_langaue;
    @BindView(R.id.iv_filter)
    ImageView iv_filter;
    @BindView(R.id.coordinater)
    CoordinatorLayout containerLayout;
    String custome_profile_bitmap_path ="";
    private static final int PICK_IMAGE = 1994;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT =122 ;
    DatePickerDialog.OnDateSetListener date_picker;
    boolean load_profile_drawable=false;
    Calendar calender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // your language
        setLangauge();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerdisable(false);
        drawerlistener();
        expireDate();
        intDatapicker();
        UserTable userTable=UserTable.getUserData();
        if(userTable!=null){
            String profile_pic=userTable.getProfile_pic();
            Bitmap bitmap=null;
            try{
                byte[] imageAsBytes = Base64.decode(profile_pic.getBytes(),Base64.DEFAULT);
                bitmap=BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            }catch (Exception e){
            }
            if(bitmap!=null){
                setBlurImage(bitmap);
                //bitmap.recycle();
            }
        }
        //getUserProfile();
        mInflater=LayoutInflater.from(this);
        pushFragmentDontIgnoreCurrent(new HomeFragment(),FRAGMENT_JUST_ADD);
        if(getIntent()!=null){
            if(getIntent().getExtras()!=null){
                if(getIntent().getExtras().containsKey(StaticConfig.LANGAUGE)){
                    if(!getIntent().getExtras().getString(StaticConfig.LANGAUGE).equalsIgnoreCase("true")){
                        if(getIntent().getExtras().containsKey(StaticConfig.DATE)){
                            pushFragmentDontIgnoreCurrent(PaymentDateFragment.getInstance(getIntent().getExtras().getString(StaticConfig.DATE)),FRAGMENT_ADD_TO_BACKSTACK_AND_ADD);
                            return;
                        }
                    }
                }else {
                    if(getIntent().getExtras().containsKey(StaticConfig.DATE)){
                        pushFragmentDontIgnoreCurrent(PaymentDateFragment.getInstance(getIntent().getExtras().getString(StaticConfig.DATE)),FRAGMENT_ADD_TO_BACKSTACK_AND_ADD);
                        return;
                    }
                }
            }
        }
           // pushFragmentDontIgnoreCurrent(new HomeFragment(),FRAGMENT_JUST_ADD);
    }

    private void intDatapicker() {
        calender = Calendar.getInstance(TimeZone.getDefault());
        date_picker = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                String year1 = String.valueOf(selectedYear);
                String month1 = String.valueOf(selectedMonth + 1);
                String day1 = String.valueOf(selectedDay);
                calender.set(Calendar.YEAR, selectedYear);
                calender.set(Calendar.MONTH, selectedMonth);
                calender.set(Calendar.DAY_OF_MONTH, selectedDay);
                String date=day1 + "/" + month1 + "/" + year1;
                Date date1=new Date();
                SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat output = new SimpleDateFormat(StaticConfig.DATE_FORMATE);
                try {
                    date1 = input.parse(date);

                  String date_start=  output.format(date1);
                    Intent intent=new Intent(StaticConfig.DATE_SELECT_BR);
                      intent.putExtra(StaticConfig.DATE_SELECT_VALUE,date_start);
                    LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(intent);
                    // parse input
                   Log.e("test","data==>"+date_start); // format output
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new Error("UNExpected Erro not conver date ========>>>>");
                }
            }
        };

    }

    /**
     * logic of expire date after this date you can not access this apk
     */
    private void expireDate() {
        try {
            int year;
            int month;
            int day;

            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            String chDtC = year + "-" + (month + 1) + "-" + day;

            if (month < 10) {
                if (day < 10) {
                    chDtC = year + "-0" + (month + 1) + "-0" + day;
                } else {
                    chDtC = year + "-0" + (month + 1) + "-" + day;
                }
            } else {
                if (day < 10) {
                    chDtC = year + "-" + (month + 1) + "-0" + day;
                }
            }
            SimpleDateFormat formatter_dest;
            Date date_dest = null;
            formatter_dest = new SimpleDateFormat("yyyy-MM-dd");
            try {
                try {
                    date_dest = formatter_dest.parse(chDtC);
                } catch (java.text.ParseException e) {

                    e.printStackTrace();
                }
            } catch (Exception e1) {

                e1.printStackTrace();
            }

            SimpleDateFormat formatter_source;
            Date date_source = null;
            formatter_source = new SimpleDateFormat("yyyy-MM-dd");
            try {
                try {
                    date_source = formatter_source.parse("2017-10-01");
                } catch (java.text.ParseException e) {

                    e.printStackTrace();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (!date_source.after(date_dest)) {
                finish();
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" +getPackageName()));
                startActivity(intent);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLangauge() {
        String langaue=  MyPreferences.getPref(this,StaticConfig.LANGAUGE);
        Log.e("test","==>"+langaue);
        Locale locale = new Locale(langaue);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
    }

    public void getUserProfile(){
        FirebaseDatabase.getInstance().getReference().child(StaticConfig.USER).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue()!= null) {
                     String imageUrl=dataSnapshot.getValue().toString();
                    if(Validation.isRequiredField(imageUrl)){
                        {
                            load_profile_drawable=true;
                            Bitmap bitmap=null;
                            try{
                                byte[] imageAsBytes = Base64.decode(imageUrl.getBytes(),Base64.DEFAULT);
                                bitmap=BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                            }catch (Exception e){

                            }

                            if(bitmap!=null){
                              setBlurImage(bitmap);
                                //bitmap.recycle();
                            }
                        }
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CommnMethod.isProgressHide();
            }
        });

    }



public void setTitle(String titile){
    if(toolbar.getVisibility()==View.GONE){
        toolbar.setVisibility(View.VISIBLE);
    }
    tv_title.setText(titile);
}
public void setBack_visbile(boolean is_visible){
    if(is_visible){
        btn_back.setVisibility(View.VISIBLE);
       btn_menu.setVisibility(View.GONE);
    }else {
       btn_back.setVisibility(View.GONE);
       btn_menu.setVisibility(View.VISIBLE);
    }

}
public void setToolbarProfilePicGone(){
    toolbar_iv_profile.setVisibility(View.GONE);
}
public void setToobarProfile(String imageString){
    toolbar_iv_profile.setVisibility(View.VISIBLE);
    Bitmap bitmap=null;
    try{
        byte[] imageAsBytes = Base64.decode(imageString.getBytes(),Base64.DEFAULT);
        bitmap=BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }catch (Exception e){
        bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.default_avata);
    }

    if(bitmap!=null){
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCornerRadius(100.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        toolbar_iv_profile.setImageDrawable(roundedBitmapDrawable);
        //bitmap.recycle();
    }
}
    private void drawerlistener() {

        try {
            mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerStateChanged(int arg0) {

                }

                /**
                 * @param drawerView
                 * @param slideOffset - it will slide based on off set value
                 *    this method is animate to the header titile up to the drawer width
                 */
                @SuppressLint("NewApi")
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    // super.onDrawerSlide(drawerView, slideOffset);
                    float moveFactor = (rl_left_drawer.getWidth() * slideOffset);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        ll_main.setTranslationX(moveFactor);

                    } else {
                        TranslateAnimation anim = new TranslateAnimation(
                                lastTranslate, moveFactor, 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        ll_main.startAnimation(anim);
                        lastTranslate = moveFactor;
                    }
                }

                @Override
                public void onDrawerOpened(View arg0) {

                }


                @Override
                public void onDrawerClosed(View arg0) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void drawerdisable(boolean isdisable) {
        try {
            if (isdisable) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  long clickOn;
    @OnClick({R.id.iv_filter,R.id.ll_payment,R.id.iv_change_langaue,R.id.ll_setting,R.id.profile_pic,R.id.btn_menu,R.id.btn_back,R.id.ll_view_customer,R.id.ll_logout,R.id.ll_home,R.id.ll_add_customer,R.id.ll_backup,R.id.iv_serach,R.id.ll_xcle_shear})
    @Override
    public void onClick(View view) {
        long time=System.currentTimeMillis();
        if(clickOn+1000>time){
            return;
        }else {
            clickOn=time;
        }
        switch (view.getId()){
            case R.id.btn_menu:
                try {
                    //methods.hideKeyboard();
                    if(!load_profile_drawable){
                        //getUserProfile();
                    }
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    // getdatalogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_add_customer:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                clearBackStackFragmets();
                pushFragmentDontIgnoreCurrent(new AddCustomerFragment(),FRAGMENT_JUST_ADD);
                break;
            case R.id.ll_home:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                clearBackStackFragmets();
                pushFragmentDontIgnoreCurrent(new HomeFragment(),FRAGMENT_JUST_ADD);
                break;
            case R.id.ll_view_customer:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                clearBackStackFragmets();
                pushFragmentDontIgnoreCurrent(new CustomerListFragment(),FRAGMENT_JUST_ADD);
                break;
            case R.id.ll_logout:
                MyPreferences.setPref(this,StaticConfig.IS_LOGIN,"false");
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));

                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.profile_pic:
                openAlertDialig();
                break;
            case R.id.ll_backup:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                clearBackStackFragmets();
               // startActivity(new Intent(MainActivity.this,BackUpActivity.class));
                 pushFragmentDontIgnoreCurrent(new BackupFragment(),FRAGMENT_JUST_ADD);
                break;
            case R.id.iv_change_langaue:
               changeLangageOpen();
                break;
            case R.id.ll_setting:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                clearBackStackFragmets();
                pushFragmentDontIgnoreCurrent(new SeetingFragment(),FRAGMENT_JUST_ADD);
                break;
            case R.id.iv_serach:
                if(edt_serach.getVisibility()==View.GONE){
                    edt_serach.setVisibility(View.VISIBLE);
                    tv_title.setVisibility(View.GONE);
                    iv_serach.setVisibility(View.GONE);
                    edt_serach.requestFocus();

                }else {

                }
                break;
            case R.id.ll_xcle_shear:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                clearBackStackFragmets();
                pushFragmentDontIgnoreCurrent(new GenrateXclFileFragment(),FRAGMENT_JUST_ADD);
                break;
            case R.id.ll_payment:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                clearBackStackFragmets();
                pushFragmentDontIgnoreCurrent(new PaymentDateFragment(),FRAGMENT_JUST_ADD);
                break;
            case R.id.iv_filter:
                DatePickerDialog datePicker = new DatePickerDialog(this, date_picker,
                        calender.get(Calendar.YEAR),
                        calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle(getResources().getString(R.string.select_date));
                datePicker.show();
                break;
            default:
                break;
        }
    }

    public void setToolbar(int screen)
    {
        toolbar.setVisibility(View.VISIBLE);
        switch (screen)
        {
            case StaticConfig.HOME:
                setToolbarLayout(true,false,false,true,true,getResources().getString(R.string.home),false);
                break;
            case StaticConfig.CUSTOMERLIST:
              setToolbarLayout(true,false,true,false,true,getResources().getString(R.string.customer_list),false);
                break;
            case StaticConfig.ADD_CUSTOMER:
                setToolbarLayout(true,false,false,true,true,getResources().getString(R.string.add_customer),false);
                break;
            case StaticConfig.ADD_TRANSCATION:
                setToolbarLayout(false,true,false,true,true,getResources().getString(R.string.add_transcation),false);
                break;
            case StaticConfig.SHOW_TRANSCATION:
               // setToolbarLayout(false,true,false,true,true,);
                break;
                case StaticConfig.SEETING:
                    setToolbarLayout(true,false,false,true,true,getResources().getString(R.string.seeting),false);
                    break;
                case StaticConfig.GENRATEXCLFILE:
                    setToolbarLayout(true,false,false,true,true,getString(R.string.gengratexclfile),false);
                    break;
                case StaticConfig.PAYMENT_DATE_FRAGMENT:
                    setToolbarLayout(true,false,false,false,true,getResources().getString(R.string.payment_date_fragment),true);
                    break;
            case StaticConfig.BACKUP_FRAGMENT:
                setToolbarLayout(true,false,false,true,true,getResources().getString(R.string.backUp_fragment),false);
                break;
            case StaticConfig.EDIT_CUSTOMER:
                setToolbarLayout(true,false,false,true,true,getResources().getString(R.string.add_customer),false);
                break;
            default:
                break;
        }
    }

    public void setToolbarLayout(boolean is_menu,boolean is_back,boolean is_serach,boolean is_langau,boolean is_title,String title_name,boolean is_fillter) {
        edt_serach.setVisibility(View.GONE);
        if(is_menu){
            btn_menu.setVisibility(View.VISIBLE);
        }else {
            btn_menu.setVisibility(View.GONE);
        }
        if(is_back){
            btn_back.setVisibility(View.VISIBLE);
        }else {
            btn_back.setVisibility(View.GONE);
        }

        if(is_serach){
            iv_serach.setVisibility(View.VISIBLE);
        }else {
            iv_serach.setVisibility(View.GONE);
        }

        if(is_title){
            tv_title.setVisibility(View.VISIBLE);
            if(Validation.isRequiredField(title_name)){
                tv_title.setText(title_name);
            }
        }else {
            tv_title.setVisibility(View.GONE);
        }
        if(is_langau){
            iv_change_langaue.setVisibility(View.VISIBLE);
        }else {
            iv_change_langaue.setVisibility(View.GONE);

        }
        if(is_fillter){
            iv_filter.setVisibility(View.VISIBLE);
        }else {
            iv_filter.setVisibility(View.GONE);
        }
    }

    private void changeLangageOpen() {
        new SweetAlertDialog(this,WARNING_TYPE).setConfirmText(getResources().getString(R.string.yes))
                .setCancelText(getResources().getString(R.string.no))
                .setTitleText(getString(R.string.do_you_want_change_languge))
                .setConfirmText(getResources().getString(R.string.yes))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        changeLangageConfig();
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
               sweetAlertDialog.dismiss();
            }
        }).show();
    }

    private void changeLangageConfig() {
         String langaue=  MyPreferences.getPrefLanguage(this,StaticConfig.LANGAUGE);
        if(langaue!=null){
            if(langaue.equalsIgnoreCase(StaticConfig.LANGAUGE_ENG)){
                MyPreferences.setPref(this,StaticConfig.LANGAUGE,StaticConfig.LANGAUGE_GUJRATI);
            }else {
                MyPreferences.setPref(this,StaticConfig.LANGAUGE,StaticConfig.LANGAUGE_ENG);
            }

            getIntent().putExtra(StaticConfig.LANGAUGE,StaticConfig.LANGAUGE_VALUE);
            clearBackStack();
            clearBackStackFragmets();
            recreate();
        }
    }

    private void openAlertDialig() {
        new SweetAlertDialog(this, WARNING_TYPE)
                .setTitleText(getString(R.string.userprofile_uplaod_alert_messag))
                .setConfirmText(getString(R.string.yes))
                .setCancelText(getString(R.string.no_))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        uploadUserProfile();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDrawerLayout.openDrawer(Gravity.START);
                            }
                        },200);


                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void uploadUserProfile() {
        if(CommnMethod.is_marshmallow()){

            int permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);


            if (writePermission != PackageManager.PERMISSION_GRANTED && readPermission!=PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                        EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }else {
                openDialogPicture();
            }
        }else {
            openDialogPicture();
        }

    }
    private void openDialogPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                throw new Error("Image Data null found");
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);
                if (imgBitmap != null) {
                    setBlurImage(liteImage);
                }
                custome_profile_bitmap_path = ImageUtils.encodeBase64(liteImage);
                if(Validation.isRequiredField(custome_profile_bitmap_path)){
                    Delete.table(UserTable.class);
                    UserTable userTable=new UserTable();
                      userTable.setProfile_pic(custome_profile_bitmap_path);
                    userTable.save();
                }
                //save picture to firebase
         /*       if(Validation.isRequiredField(custome_profile_bitmap_path)) {

                    CommnMethod.isProgressShow(this);
                    FirebaseDatabase.getInstance().getReference().child(StaticConfig.USER + "/profile").setValue(custome_profile_bitmap_path).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            CommnMethod.isProgressHide();
                            showSnakbarMessage(getString(R.string.profile_upload_message));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommnMethod.isProgressHide();
                            showSnakbarMessage(getString(R.string.failure));

                        }
                    });
                }*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBlurImage(Bitmap bitmap) {

        if (bitmap != null) {
            Uri uri = CommnMethod.getImageUri(this, bitmap);
            if(uri!=null){
                profile_pic.setImageURI(uri);
            }
            rl_blure_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            Bitmap blurBmp = BlurBuilder.blur(this, bitmap);
            if(blurBmp!=null){
               Log.e("test","==>>>>"+blurBmp);
                rl_blure_pic.setImageBitmap(blurBmp);
            }else {
                Log.e("test","Bitmap found null");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            openDialogPicture();
        }
    }
public void showSnakbarMessage(String message){
    // Create the Snackbar
    Snackbar snackbar = Snackbar.make(containerLayout, "", Snackbar.LENGTH_LONG);
// Get the Snackbar's layout view
    Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
// Hide the text
    TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
    textView.setVisibility(View.INVISIBLE);

// Inflate our custom view
    View snackView = mInflater.inflate(R.layout.snakbar_view, null);
// Configure the view
    TextView textViewTop = (TextView) snackView.findViewById(R.id.tv_message);
    textViewTop.setText(message);
    textViewTop.setTextColor(Color.WHITE);

// Add the view to the Snackbar's layout
    layout.addView(snackView, 0);
// Show the Snackbar
    snackbar.show();

}
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    
}
