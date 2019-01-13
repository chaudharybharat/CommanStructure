package com.example.agc_linux.accounting;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.commonmodule.mi.utils.ConnectionUtil;
import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.db.SharedPreferenceHelper;
import com.example.agc_linux.accounting.dialog.SweetAlertDialog;
import com.example.agc_linux.accounting.model.User;
import com.example.agc_linux.accounting.util.MyPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.agc_linux.accounting.dialog.SweetAlertDialog.WARNING_TYPE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static String TAG = "LoginActivity";
    FloatingActionButton fab;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText editTextUsername, editTextPassword;
    private LovelyProgressDialog waitingDialog;
    private AuthUtils authUtils;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean firstTimeAccess;
    @BindView(R.id.iv_language)
    ImageView iv_langaue;
    @Override
    protected void onStart() {
        super.onStart();
     //   mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setLangauge();
        authUtils = new AuthUtils();
        mAuth = FirebaseAuth.getInstance();
        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        editTextUsername = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        firstTimeAccess = true;
        String is_login=MyPreferences.getPref(this,StaticConfig.IS_LOGIN);
        if(is_login.equalsIgnoreCase(StaticConfig.IS_LOGIN_TRUE))
        {
           String oneTime_password= MyPreferences.getPref(getApplicationContext(),StaticConfig.ONE_TIME_PASS);
            if(Validation.isRequiredField(oneTime_password)){

                String is_alllow_confirm= MyPreferences.getPref(getApplicationContext(),StaticConfig.ONE_TIME_PASS_SETTING_PR);
                if(is_alllow_confirm.equalsIgnoreCase(StaticConfig.TRUE)){
                    cofirmOpenTimePassword();
                }else {
                    finish();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                }
            }else {
                authUtils.setOpenTimePassword();
            }
        }
        //initFirebase();
    }

    private void cofirmOpenTimePassword() {
        final Dialog dialog=new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.confirm_onetime_password);
        dialog.setCancelable(false);
        final EditText  edt_paswword=(EditText) dialog.findViewById(R.id.edt_password);


        Button btn_go=(Button) dialog.findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=edt_paswword.getText().toString().trim();
                if(!Validation.isRequiredField(password)){
                    Toast.makeText(getApplicationContext(),R.string.password_empty,Toast.LENGTH_LONG).show();
                    return;
                }

               String onetime_password= MyPreferences.getPref(getApplicationContext(),StaticConfig.ONE_TIME_PASS);

                if(onetime_password.equalsIgnoreCase(password)){
                    finish();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    dialog.dismiss();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.not_match_one_time_password,Toast.LENGTH_LONG);
                }

            }
        });
        // Toast.makeText(getApplicationContext(),"Please enter one time password",Toast.LENGTH_LONG).show();
        dialog.show();
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
    /**
     * Khởi tạo các thành phần cần thiết cho việc quản lý đăng nhập
     */
    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (firstTimeAccess) {
                        MyPreferences.setPref(getApplicationContext(),StaticConfig.IS_LOGIN,StaticConfig.IS_LOGIN_TRUE);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    }else {
                        Log.e(TAG,"second time not Access");
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                firstTimeAccess = false;
            }
        };

        //Khoi tao dialog waiting khi dang nhap
        waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void clickRegisterLayout(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);
        Toast.makeText(getApplication(),R.string.please_contact_to_admin,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StaticConfig.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
            //authUtils.createUser(data.getStringExtra(StaticConfig.STR_EXTRA_USERNAME), data.getStringExtra(StaticConfig.STR_EXTRA_PASSWORD));
        }
    }

    public void clickLogin(View view) {

        if(ConnectionUtil.isInternetAvailable(this)){

            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if(!Validation.isRequiredField(username)){
                Toast.makeText(this, R.string.emai_empty,Toast.LENGTH_SHORT).show();
                return;
            }else {
                if(!Validation.isEmailValid(username)){
                    Toast.makeText(this, R.string.email__not_valid,Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if(!Validation.isRequiredField(password)){
                Toast.makeText(this, R.string.password_empty,Toast.LENGTH_SHORT).show();
                return;
            }
            authUtils.signIn(username, password);
        }else {
            Toast.makeText(this, R.string.not_internate,Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* setResult(RESULT_CANCELED, null);
        finish();*/
    }

    public void clickResetPassword(View view) {
        String username = editTextUsername.getText().toString();
        if(!Validation.isRequiredField(username)){
            Toast.makeText(this,getResources().getString(R.string.emai_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Validation.isEmailValid(username)){
            Toast.makeText(this,getResources().getString(R.string.email__not_valid), Toast.LENGTH_SHORT).show();
            return;
        }
        authUtils.resetPassword(username);
        }
   @OnClick({R.id.iv_language})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_language:
                changeLangageOpen();
                break;
            default:
                break;
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
            recreate();
        }
    }

    /**
     * Dinh nghia cac ham tien ich cho quas trinhf dang nhap, dang ky,...
     */
    class AuthUtils {
        /**
         * Action register
         *
         * @param email
         * @param password
         */
        void createUser(String email, String password) {
            waitingDialog.setIcon(R.drawable.ic_add_friend)
                    .setTitle("Registering....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            waitingDialog.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                new LovelyInfoDialog(LoginActivity.this) {
                                    @Override
                                    public LovelyInfoDialog setConfirmButtonText(String text) {
                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dismiss();
                                            }
                                        });
                                        return super.setConfirmButtonText(text);
                                    }
                                }
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("Register false")
                                        .setMessage("Email exist or weak password!")
                                        .setConfirmButtonText("ok")
                                        .setCancelable(false)
                                        .show();
                            } else {
                                initNewUserInfo();
                                Toast.makeText(LoginActivity.this, "Register and Login success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                LoginActivity.this.finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    })
            ;
        }


        /**
         * Action Login
         *
         * @param email
         * @param password
         */
        void signIn(String email, String password) {
            waitingDialog.setIcon(R.drawable.ic_person_low)
                    .setTitle("Login....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            waitingDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                new LovelyInfoDialog(LoginActivity.this) {
                                    @Override
                                    public LovelyInfoDialog setConfirmButtonText(String text) {
                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dismiss();
                                            }
                                        });
                                        return super.setConfirmButtonText(text);
                                    }
                                }
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_person_low)
                                        .setTitle("Login false")
                                        .setMessage("Email not exist or wrong password!")
                                        .setCancelable(false)
                                        .setConfirmButtonText("Ok")
                                        .show();
                            } else {
                                MyPreferences.setPref(getApplicationContext(),StaticConfig.IS_LOGIN,StaticConfig.IS_LOGIN_TRUE);
                                setOpenTimePassword();

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            waitingDialog.dismiss();
                        }
                    });
        }
        private void setOpenTimePassword() {
            final Dialog dialog=new Dialog(LoginActivity.this);
            dialog.setContentView(R.layout.open_time_password);
            dialog.setCancelable(false);
         final EditText  edt_paswword=(EditText) dialog.findViewById(R.id.edt_password);
         final EditText  edt_paswword_confirm=(EditText) dialog.findViewById(R.id.edt_password_confirm);

            Button btn_go=(Button) dialog.findViewById(R.id.btn_go);
            btn_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String password=edt_paswword.getText().toString().trim();
                    String password_confirm=edt_paswword_confirm.getText().toString().trim();

                    if(!Validation.isRequiredField(password)){
                        Toast.makeText(getApplicationContext(),R.string.password_empty,Toast.LENGTH_LONG).show();
                         return;
                    }else {
                        if(password.length()<4){
                            Toast.makeText(getApplicationContext(), R.string.minimum_four_digit,Toast.LENGTH_LONG).show();

                        }
                    }
                    if(!Validation.isRequiredField(password_confirm)){
                        Toast.makeText(getApplicationContext(),R.string.enter_pass_confirm,Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(!password.matches(password_confirm)){
                        Toast.makeText(getApplicationContext(),R.string.not_match,Toast.LENGTH_LONG).show();
                        return;
                    }
                    MyPreferences.setPref(getApplicationContext(),StaticConfig.ONE_TIME_PASS,password);
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                    dialog.dismiss();

                }
            });
                       // Toast.makeText(getApplicationContext(),"Please enter one time password",Toast.LENGTH_LONG).show();
            dialog.show();
        }
        /**
         * Action reset password
         *
         * @param email
         */
        void resetPassword(final String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new LovelyInfoDialog(LoginActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("Password Recovery")
                                    .setMessage("Sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new LovelyInfoDialog(LoginActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("False")
                                    .setMessage("False to sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    });
        }
        /**
         * Luu thong tin user info cho nguoi dung dang nhap
         */
        void saveUserInfo() {
            FirebaseDatabase.getInstance().getReference().child("user/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    waitingDialog.dismiss();
                    HashMap hashUser = (HashMap) dataSnapshot.getValue();
                    User userInfo = new User();
                    userInfo.name = (String) hashUser.get("name");
                    userInfo.email = (String) hashUser.get("email");
                    userInfo.avata = (String) hashUser.get("avata");
                    SharedPreferenceHelper.getInstance(LoginActivity.this).saveUserInfo(userInfo);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        /**
         * Khoi tao thong tin mac dinh cho tai khoan moi
         */
        void initNewUserInfo() {
            User newUser = new User();
            newUser.email = user.getEmail();
            newUser.name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
            newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
            FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
        }
    }
}
