package com.example.agc_linux.accounting.fragment;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.commonmodule.mi.Activity.MIActivity;
import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTranscationFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.tv_customer_name)
    TextView tv_customer_name;
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
    @BindView(R.id.ll_payment_term)
    LinearLayout ll_payment_terml;
    DatePickerDialog.OnDateSetListener datePickerListener_payment_term;

    @BindView(R.id.tv_payment_data)
    TextView tv_payment_data;
    Calendar cal;
    Calendar cal_paymet_term;
    // 0 mean debit
    // 1 mean credit
    String trascation_type = "";


    String customer_name;
    int customer_id=0;
    public static String profile_pic="";
    public static AddTranscationFragment getInstance(String custome_name, int customer_id) {
        AddTranscationFragment addTranscation = new AddTranscationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StaticConfig.CUSTOMER_NAME, custome_name);
        bundle.putInt(StaticConfig.CUSTOMER_ID, customer_id);
        addTranscation.setArguments(bundle);
        return addTranscation;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_transcation_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHeader();
        getBundleValues();
        initComponet();
    }

    private void getBundleValues() {
        Bundle bundle = getArguments();
        if (bundle != null) {

            if (bundle.containsKey(StaticConfig.CUSTOMER_NAME)) {
                customer_name = bundle.getString(StaticConfig.CUSTOMER_NAME);
                tv_customer_name.setText(customer_name);
            }
            if (bundle.containsKey(StaticConfig.CUSTOMER_ID)) {
                customer_id = bundle.getInt(StaticConfig.CUSTOMER_ID);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponet();

    }

    public void setHeader() {
        mActivity.setToolbar(StaticConfig.ADD_TRANSCATION);
    }
    // Listener
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            cal.set(Calendar.YEAR,selectedYear);
            cal.set(Calendar.MONTH,selectedMonth);
            cal.set(Calendar.DAY_OF_MONTH,selectedDay);
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


    public void initComponet() {
        cal_paymet_term = Calendar.getInstance(TimeZone.getDefault());
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

        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConfig.DATE_FORMATE);
        cal = Calendar.getInstance(TimeZone.getDefault());
        tv_date.setText(dateFormat.format(new Date())); // it will show 16/07/13

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


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setHeader();
        }
    }

    @OnClick({R.id.btn_clear, R.id.btn_save,R.id.tv_date,R.id.tv_payment_data})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:

                String amount = edt_amount.getText().toString().trim();

                boolean is_valid = checkValidation(amount);
                if (is_valid)
                    uploadTrandcationDetail(amount, trascation_type);
                break;
            case R.id.tv_date:
                openDatedialog();
                break;
            case R.id.btn_clear:
                clearTextDate();
                break;
            case R.id.tv_payment_data:
                openDatedialogPatmentRerm();
                break;
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

// Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(mActivity, datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle(mActivity.getResources().getString(R.string.select_date));
        datePicker.show();
    }
    private void openDatedialogPatmentRerm() {
        // Get current date

// Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), datePickerListener_payment_term,
                cal_paymet_term.get(Calendar.YEAR),
                cal_paymet_term.get(Calendar.MONTH),
                cal_paymet_term.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(cal_paymet_term.getTime().getTime());
        datePicker.setCancelable(false);
        datePicker.setTitle(mActivity.getResources().getString(R.string.select_date));
        datePicker.show();
    }


    public boolean checkValidation(String amount) {
        if (!Validation.isRequiredField(amount)) {
            edt_amount.setError(mActivity.getString(R.string.validation_amount));
            edt_amount.requestFocus();
            return false;
        }else{
            int amount_int=Integer.parseInt(amount);
            if(amount_int<=0){
                edt_amount.setError(mActivity.getString(R.string.validation_amount_greter_then_));
                edt_amount.requestFocus();
                return false;
            }
        }
        if (!Validation.isRequiredField(trascation_type)) {
            mActivity.showSnakbarMessage(mActivity.getString(R.string.valid_amount));
            return false;
        }
        return true;

    }

    public void uploadTrandcationDetail(String amount, String trascation_type) {

        CustomerTranscation customerTranscation = new CustomerTranscation();
        String descraption = edt_descraption.getText().toString().trim();
        String date_string = tv_date.getText().toString();
        String payment_date = tv_payment_data.getText().toString().trim();
        if(payment_date.equalsIgnoreCase(mActivity.getResources().getString(R.string.select_date))){
            payment_date="0";
        }

        if (customer_id!=0) {
            customerTranscation.setTranscation_date(date_string);
            customerTranscation.setPaymentTermdate(payment_date);
            customerTranscation.setAmount(amount);
            Log.e("test","==>"+customer_id);
            customerTranscation.setCustomer_id(customer_id);
            customerTranscation.setTranscation_type(trascation_type);
            customerTranscation.setDescraption("" + descraption);
              boolean is_insert=customerTranscation.save();
            if(is_insert){
                mActivity.clearBackStackFragmets();
                mActivity.pushFragmentDontIgnoreCurrent(new CustomerListFragment(), MIActivity.FRAGMENT_JUST_REPLACE);
                mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.transcation_success_messag));
            }else {
                CommnMethod.isProgressHide();
                mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.failure_try_again));
            }
        }
    }
}