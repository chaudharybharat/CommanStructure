package com.example.agc_linux.accounting.fragment;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.agc_linux.accounting.R.id.tv_cretid;
import static com.example.agc_linux.accounting.R.id.tv_debit;
import static com.example.agc_linux.accounting.R.id.tv_total_dr;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.tv_cretid)
    TextView tv_credit;
    @BindView(R.id.tv_debit)
    TextView tv_debit;
    @BindView(R.id.tv_payment_date_total)
    TextView tv_payment_date_total;
    @BindView(R.id.tv_customer)
    TextView tv_customer;
    @BindView(R.id.tv_marque_scrolling)
    TextView tv_marque_scrolling;
    @BindView(R.id.tv_date)
    TextView tv_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHeader();
        initComponet();
    }

    public void initComponet() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

        String date1 = format1.format(date);
        Log.e("test","=>>"+date1);




        FlowCursor query = SQLite.select().from(Customer.class).query();
        final Cursor wrappedCursor = query.getWrappedCursor();
        long total_customer = wrappedCursor.getCount();
        tv_customer.setText("" + total_customer);
        tv_date.setText(getString(R.string.today_date)+CommnMethod.getTodayDate());
        List<CustomerTranscation> transactionList = CustomerTranscation.getTranscationList();
        int size = transactionList.size();
        long total_cradit = 00;
        long total_debit = 00;
        for (int i = 0; i < size; i++) {
            CustomerTranscation customerTranscation = transactionList.get(i);
            if (customerTranscation != null) {
                if (customerTranscation.getTranscation_type().equalsIgnoreCase("1")) {
                    total_cradit = total_cradit + Integer.parseInt(customerTranscation.getAmount());
                } else {
                    total_debit = total_debit + Integer.parseInt(customerTranscation.getAmount());
                }
                tv_credit.setText("" + total_cradit);
                tv_debit.setText("" + total_debit);
            }
        }
        String today_date=CommnMethod.getTodayDate();
       List<CustomerTranscation> customerTranscationList=CustomerTranscation.getTranscationListDate(today_date);
        if(customerTranscationList!=null){
            tv_payment_date_total.setText(""+customerTranscationList.size());
        }

        String tomorrow=CommnMethod.getTomorrowDate();
      String tomorrow_payment_date_customer="";
        List<CustomerTranscation> customerTomorrowpaymentdate=CustomerTranscation.getTranscationListDate(tomorrow);
        if(customerTomorrowpaymentdate!=null){
            List<Customer> tomorrowpaymentcustomelist=new ArrayList<>();
            for (int i = 0; i <customerTomorrowpaymentdate.size() ; i++) {
                int customerid=customerTomorrowpaymentdate.get(i).getCustomer_id();
                Customer customer=Customer.getCustomer(customerid);
                tomorrowpaymentcustomelist.add(customer);
            }
            if(!customerTomorrowpaymentdate.isEmpty()){
               ArrayList<String> customerList=new ArrayList<>();

                for (int i = 0; i <tomorrowpaymentcustomelist.size() ; i++) {
                    String customer_name=tomorrowpaymentcustomelist.get(i).getName();
                   if(i==0){
                       customerList.add(customer_name);
                       tomorrow_payment_date_customer=getString(R.string.customer_list_tomorrow_pay)+customer_name;
                   }else {
                       for (int j = 0; j <customerList.size() ; j++) {

                           if(!customerList.contains(customer_name)){
                               customerList.add(customer_name);
                               tomorrow_payment_date_customer=tomorrow_payment_date_customer+","+customer_name;
                               Log.e("test","===>"+tomorrow_payment_date_customer);
                           }
                       }
                   }

                }

            }else {
                tomorrow_payment_date_customer=getString(R.string.tomorrow_payment_date);
            }
            tv_marque_scrolling.setText(tomorrow_payment_date_customer);
            tv_marque_scrolling.setSelected(true);
        }
    }

    private void setHeader() {
        mActivity.setToolbar(StaticConfig.HOME);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setHeader();
            initComponet();
        }
    }
    @OnClick({R.id.paymetn_cardview,R.id.customer_card_view})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customer_card_view:
                if(!tv_customer.getText().toString().equalsIgnoreCase("0")) {
                    mActivity.pushFragmentIgnoreCurrent(CustomerListFragment.getInstance(), mActivity.FRAGMENT_ADD_TO_BACKSTACK_AND_ADD);
                }else {
                    mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.not_found_customer));
                }
                break;
            case R.id.paymetn_cardview:
                if(!tv_payment_date_total.getText().toString().equalsIgnoreCase("0")){
                    mActivity.pushFragmentIgnoreCurrent(PaymentDateFragment.getInstance(),mActivity.FRAGMENT_ADD_TO_BACKSTACK_AND_ADD);
                }else {
                    mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.customer_payment_data_not));
                }
                break;
            default:
                break;
        }
    }

}
