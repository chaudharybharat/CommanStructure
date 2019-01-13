package com.example.agc_linux.accounting.dialog;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.commonmodule.mi.Activity.MIActivity;
import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.application.App;
import com.example.agc_linux.accounting.fragment.CustomerListFragment;
import com.example.agc_linux.accounting.fragment.ShowTranscationFragment;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by agc-linux on 18/8/17.
 */

public class SweetEdit_Dialog extends Dialog implements View.OnClickListener {
    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private Animation mOverlayOutAnim;
    private Animation mErrorInAnim;
    private AnimationSet mErrorXInAnim;
    private AnimationSet mSuccessLayoutAnimSet;
    private Animation mSuccessBowAnim;

    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;
    private int mAlertType;

    private SuccessTickView mSuccessTick;

    private View mSuccessLeftMask;
    private View mSuccessRightMask;
    private Drawable mCustomImgDrawable;

    @BindView(R.id.edt_amout)
    EditText edt_amount;
    @BindView(R.id.radio_cradit)
    RadioButton radio_cradit;
    @BindView(R.id.radio_debit)
    RadioButton radio_debit;
    @BindView(R.id.rgp)
    RadioGroup radioGroup;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.edt_descraption)
    TextView edt_descraption;
    @BindView(R.id.btn_save)
    TextView btn_save;
    @BindView(R.id.reset)
    ImageView reset;
    @BindView(R.id.ll_payment_term)
    LinearLayout ll_payment_terml;

    @BindView(R.id.tv_payment_data)
    TextView tv_payment_data;
    // 0 mean debit
    // 1 mean credit
    String trascation_type = "";
   DatePickerDialog.OnDateSetListener datePickerListener;
   DatePickerDialog.OnDateSetListener datePickerListener_payment_term;
    Context context;
    private FrameLayout mWarningFrame;
    private boolean mCloseFromCancel;
    onUpdateTranscation onUpdateTranscation;
    public static final int NORMAL_TYPE = 0;
    public static final int ERROR_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;


   public static CustomerTranscation customerTranscation;
    Calendar cal;
    Calendar cal_paymet_term;
    public static interface onUpdateTranscation {
        public void onUpdateTransnction();
    }

    public SweetEdit_Dialog(Context context,ShowTranscationFragment onUpdateTranscation) {
        this(context, NORMAL_TYPE,customerTranscation,onUpdateTranscation);
        this.context=context;
        this.onUpdateTranscation=onUpdateTranscation;
    }

    public SweetEdit_Dialog(Context context, int alertType,CustomerTranscation customerTranscation,ShowTranscationFragment onUpdateTranscation) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        this.onUpdateTranscation=onUpdateTranscation;
        this.customerTranscation=customerTranscation;
        mAlertType = alertType;
        mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.error_frame_in);
        mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.error_x_in);
        // 2.3.x system don't support alpha-animation on layer-list drawable
        // remove it from animation set
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            List<Animation> childAnims = mErrorXInAnim.getAnimations();
            int idx = 0;
            for (;idx < childAnims.size();idx++) {
                if (childAnims.get(idx) instanceof AlphaAnimation) {
                    break;
                }
            }
            if (idx < childAnims.size()) {
                childAnims.remove(idx);
            }
        }
        mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.success_bow_roate);
        mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.success_mask_layout);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            SweetEdit_Dialog.super.cancel();
                        } else {
                            SweetEdit_Dialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transcation_edit_alert);
        setCancelable(false);
        ButterKnife.bind(this);
        cal = Calendar.getInstance(TimeZone.getDefault());
        cal_paymet_term = Calendar.getInstance(TimeZone.getDefault());
        setRadioButtonClick();
        context= App.getGetApp();
        setUIData();
        Log.e("test", "onCreate: ");
      /*  btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        tv_date.setOnClickListener(this);*/
        // Listener
       datePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                String year1 = String.valueOf(selectedYear);
                String month1 = String.valueOf(selectedMonth + 1);
                String day1 = String.valueOf(selectedDay);
                cal.set(Calendar.YEAR, selectedYear);
                cal.set(Calendar.MONTH, selectedMonth);
                cal.set(Calendar.DAY_OF_MONTH, selectedDay);
                String date=day1 + "/" + month1 + "/" + year1;
                Date date1=new Date();
                SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat output = new SimpleDateFormat(StaticConfig.DATE_FORMATE);
                try {
                    date1 = input.parse(date);                 // parse input
                    tv_date.setText(output.format(date1));    // format output
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new Error("UNExpected Erro not conver date ========>>>>");
                }
            }
        };
        datePickerListener_payment_term = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                String year1 = String.valueOf(selectedYear);
                String month1 = String.valueOf(selectedMonth + 1);
                String day1 = String.valueOf(selectedDay);
                cal_paymet_term.set(Calendar.YEAR, selectedYear);
                cal_paymet_term.set(Calendar.MONTH, selectedMonth);
                cal_paymet_term.set(Calendar.DAY_OF_MONTH, selectedDay);
                String date=day1 + "/" + month1 + "/" + year1;
                Date date1=new Date();
                SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat output = new SimpleDateFormat(StaticConfig.DATE_FORMATE);
                try {
                    date1 = input.parse(date);                 // parse input
                    tv_payment_data.setText(output.format(date1));    // format output
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new Error("UNExpected Erro not conver date ========>>>>");
                }
            }
        };
        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);


        mWarningFrame = (FrameLayout)findViewById(R.id.warning_frame);

        //  mProgressHelper.setProgressWheel((ProgressWheel)findViewById(R.id.progressWheel));


        changeAlertType(mAlertType, true);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_payment_data.setText(context.getResources().getString(R.string.select_date));
            }
        });

    }

    private void setRadioButtonClick() {
        // it will show 16 Jul 2013
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_cradit:
                        trascation_type = "1";
                        ll_payment_terml.setVisibility(View.GONE);
                        // do operations specific to this selection
                        break;
                    case R.id.radio_debit:
                        trascation_type = "0";
                        ll_payment_terml.setVisibility(View.VISIBLE);
                        // do operations specific to this selection
                        break;


                }
            }
        });
    }

    private void setUIData() {
        if(customerTranscation!=null){

        if(Validation.isRequiredField(customerTranscation.getTranscation_date())){
                tv_date.setText(""+customerTranscation.getTranscation_date());
            try{

                SimpleDateFormat sdf = new SimpleDateFormat(StaticConfig.DATE_FORMATE, Locale.ENGLISH);
                cal.setTime(sdf.parse(customerTranscation.getTranscation_date()));
            }catch(Exception e){
                throw new Error("UNEXPECTED ERROR=>>>>>>>>>>Date not not formate propar");
            }
            }

            if(Validation.isRequiredField(customerTranscation.getAmount())){
                edt_amount.setText(customerTranscation.getAmount());
                edt_amount.setSelection(customerTranscation.getAmount().length());
            }
            if(Validation.isRequiredField(customerTranscation.getDescraption())){
                edt_descraption.setText(customerTranscation.getDescraption());
            }
            if(Validation.isRequiredField(customerTranscation.getTranscation_type())){

                if(customerTranscation.getTranscation_type().equalsIgnoreCase("1")){
                    radio_cradit.setChecked(true);
                }else {

                    if(customerTranscation.getPaymentTermdate().equalsIgnoreCase("0")){
                        reset.setVisibility(View.GONE);

                        tv_payment_data.setText(context.getResources().getString(R.string.select_date));
                    }else {
                        reset.setVisibility(View.VISIBLE);
                        tv_payment_data.setText(""+customerTranscation.getPaymentTermdate());
                        SimpleDateFormat sdf = new SimpleDateFormat(StaticConfig.DATE_FORMATE, Locale.ENGLISH);
                        try {
                            cal_paymet_term.setTime(sdf.parse(customerTranscation.getPaymentTermdate()));
                        } catch (ParseException e) {
                            e.printStackTrace();

                            throw  new Error("ERROR FOR DAT FORAMTE CONVER 324 line number");
                        }
                    }
                    radio_debit.setChecked(true);
                }
            }
        }
    }

    private void restore () {


        mWarningFrame.setVisibility(View.GONE);
        mSuccessTick.clearAnimation();
        mSuccessLeftMask.clearAnimation();
        mSuccessRightMask.clearAnimation();
    }

    private void playAnimation () {
        if (mAlertType == ERROR_TYPE) {
        } else if (mAlertType == SUCCESS_TYPE) {
            mSuccessTick.startTickAnim();
            mSuccessRightMask.startAnimation(mSuccessBowAnim);
        }
    }

    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case ERROR_TYPE:
                   // mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS_TYPE:
                    // initial rotate layout of success mask
                    mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
                    mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
                    break;

            }
            if (!fromCreate) {
                playAnimation();
            }
        }
    }


    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
        playAnimation();
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */


    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mDialogView.startAnimation(mModalOutAnim);
    }

    @OnClick({R.id.btn_clear,R.id.btn_save,R.id.tv_date,R.id.iv_close,R.id.tv_payment_data,R.id.tv_close})
    public void onClick(View v) {
        switch (v.getId()) {
                case R.id.btn_save:
                    String amount = edt_amount.getText().toString().trim();
                    boolean is_valid = checkValidation(amount);
                    if (is_valid)
                       // uploadTrandcationDetail(amount, trascation_type);
                        updateLocalInsideTranscation(amount,trascation_type);
                    break;
                case R.id.tv_date:
                    openDatedialog();
                    break;
                case R.id.btn_clear:
                    clearTextDate();
                    break;
            case R.id.iv_close:
                dismiss();
                break;

            case R.id.tv_payment_data:
             openDatedialogPatmentRerm();
                break;
            case R.id.tv_close:
                dismiss();
                break;
            }
        }

    private void updateLocalInsideTranscation(String amount, String trascation_type) {
        String descraption=edt_descraption.getText().toString().trim();
        String date_string=tv_date.getText().toString();
        String payment_date=tv_payment_data.getText().toString().trim();
        if(trascation_type.equalsIgnoreCase("0")){
            if(payment_date.equalsIgnoreCase(context.getResources().getString(R.string.select_date))){
                payment_date="0";
            }
        }else {
            payment_date="0";
        }
        CustomerTranscation customerTrans=CustomerTranscation.getTranscation(customerTranscation.getUniqeId());
         if(customerTrans!=null){
             customerTrans.setPaymentTermdate(payment_date);
             customerTrans.setTranscation_date(date_string);
             customerTrans.setAmount(amount);
             customerTrans.setCustomer_id(customerTranscation.getCustomer_id());
             customerTrans.setTranscation_type(trascation_type);
             customerTrans.setDescraption(""+descraption);
             customerTrans.update();
             customerTrans.save();
             dismiss();
         onUpdateTranscation.onUpdateTransnction();
         }else {
             Toast.makeText(context,"No update Some issue",Toast.LENGTH_LONG).show();
         }
    }
    private void clearTextDate() {
        edt_amount.setText("");
        edt_descraption.setText("");
        radio_cradit.setChecked(false);
        radio_debit.setChecked(false);
    }
    private void openDatedialog() {
         // Get current date
  Calendar calendar=Calendar.getInstance(TimeZone.getDefault());
// Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.getDatePicker().setMinDate(calendar.getTime().getTime());
        datePicker.setTitle(context.getResources().getString(R.string.select_date));
        datePicker.show();
    }
    private void openDatedialogPatmentRerm() {
        // Get current date
      Calendar calendar=Calendar.getInstance();
// Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), datePickerListener_payment_term,
                cal_paymet_term.get(Calendar.YEAR),
                cal_paymet_term.get(Calendar.MONTH),
                cal_paymet_term.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.getDatePicker().setMinDate(calendar.getTime().getTime());
        datePicker.setTitle(context.getResources().getString(R.string.select_date));
        datePicker.show();
    }

    public boolean checkValidation(String amount) {
        if (!Validation.isRequiredField(amount)) {
            edt_amount.setError(context.getString(R.string.validation_amount));
            edt_amount.requestFocus();
            return false;
        }else {
            int amount_int = Integer.parseInt(amount);
            if (amount_int <= 0) {
                edt_amount.setError(context.getString(R.string.validation_amount_greter_then_));
                edt_amount.requestFocus();
                return false;
            }
        }
        if (!Validation.isRequiredField(trascation_type)) {
            Toast.makeText(context,context.getString(R.string.valid_amount),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void uploadTrandcationDetail(String amount, String trascation_type) {
        CommnMethod.isProgressShow(context);

        final CustomerTranscation updatecustomerTranscation=new CustomerTranscation();
        String descraption=edt_descraption.getText().toString().trim();
        String date_string=tv_date.getText().toString();

            updatecustomerTranscation.setTranscation_date(date_string);
            updatecustomerTranscation.setAmount(amount);
            updatecustomerTranscation.setCustomer_id(customerTranscation.getCustomer_id());
            updatecustomerTranscation.setTranscation_type(trascation_type);
            updatecustomerTranscation.setDescraption(""+descraption);
            String uniqueID =CommnMethod.createUniqueId();
            if(Validation.isRequiredField(uniqueID)) {
               // updatecustomerTranscation.setUniqeId(uniqueID);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child(StaticConfig.TRANSCATION + "/").orderByChild("uniqeId").equalTo(customerTranscation.getUniqeId());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            CommnMethod.isProgressHide();
                            for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {

                                HashMap<String,CustomerTranscation> result = new HashMap<>();
                                 result.put(appleSnapshot.getKey(),updatecustomerTranscation);

                               appleSnapshot.getRef().setValue(updatecustomerTranscation);
                              //  Log.e("test","==<<<<<>>>>>>"+appleSnapshot.getRef().getKey());

                            }
                            Log.e("test","found data"+dataSnapshot.getValue().toString());
                            onUpdateTranscation.onUpdateTransnction();
                        } else {
                            Log.e("test","failure ");

                            CommnMethod.isProgressHide();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("test", "onCancelled", databaseError.toException());
                       Toast.makeText(context,context.getString(R.string.failure_try_again),Toast.LENGTH_SHORT).show();
                        CommnMethod.isProgressHide();
                    }
                });

               /* FirebaseDatabase.getInstance().getReference().child(StaticConfig.TRANSCATION + "/").push().setValue(updatecustomerTranscation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        CommnMethod.isProgressHide();
                        // mActivity.pushFragmentDontIgnoreCurrent(new CustomerListFragment(), MIActivity.FRAGMENT_JUST_REPLACE);
                        Toast.makeText(context, "Transcation detail upload Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CommnMethod.isProgressHide();
                        Toast.makeText(context, "Failure ", Toast.LENGTH_SHORT).show();

                    }
                });
               */



            dismiss();
        }else {
            throw new Error("Exception not found customer id");
        }



    }
}
