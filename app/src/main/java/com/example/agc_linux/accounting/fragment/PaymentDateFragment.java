package com.example.agc_linux.accounting.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.interfacecustom.OnDatePicker;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.example.agc_linux.accounting.util.CommnMethod;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDateFragment extends BaseFragment implements View.OnClickListener,OnDatePicker {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
    @BindView(R.id.tv_not_found)
    TextView tv_not_found;
    List<CustomerTranscation> customerTranscationList;
    TranscationAdaptor transcationAdaptor;
    String today_date;
    String that_date="";
    boolean is_that_data=false;
    BroadcastReceiver br_mainActivity=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent!=null){
                if(intent.getExtras()!=null){
                    if(intent.getExtras().containsKey(StaticConfig.DATE_SELECT_VALUE)){
                        is_that_data=true;
                        String date=intent.getExtras().getString(StaticConfig.DATE_SELECT_VALUE);
                        that_date=date;
                        getLocalTranscataion(date);
                    }
                }
            }
        }
    };
    public static PaymentDateFragment getInstance(String date){
        Bundle bundle=new Bundle();
        PaymentDateFragment paymentDateFragment=new PaymentDateFragment();
        bundle.putString(StaticConfig.DATE,date);
        paymentDateFragment.setArguments(bundle);
        return  paymentDateFragment;

    }

    public static PaymentDateFragment getInstance(){
        PaymentDateFragment paymentDateFragment=new PaymentDateFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(StaticConfig.USER_COMMING_HOME,true);
        paymentDateFragment.setArguments(bundle);
        return paymentDateFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.payment_date_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        Bundle bundle=getArguments();
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(br_mainActivity,new IntentFilter(StaticConfig.DATE_SELECT_BR));
        today_date= CommnMethod.getTodayDate();

        initComponet();
    }

    private void setHeader() {
            mActivity.setToolbar(StaticConfig.PAYMENT_DATE_FRAGMENT);
    }

    private void initComponet() {
        customerTranscationList = new ArrayList<>();
        transcationAdaptor = new TranscationAdaptor();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(transcationAdaptor);

        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.containsKey(StaticConfig.USER_COMMING_HOME)) {
                mActivity.setToolbarLayout(false, true, false, false, true, mActivity.getResources().getString(R.string.payment_date_fragment),true);
                getLocalTranscataion(today_date);
            } else if (bundle.containsKey(StaticConfig.DATE)) {
                Log.e("test", "==>" + bundle.getString(StaticConfig.DATE));
                String date_str = bundle.getString(StaticConfig.DATE);
                getLocalTranscataion(date_str);
                setHeader();
            } else {
                setHeader();
                getLocalTranscataion(today_date);
            }
        }else {
            setHeader();
            getLocalTranscataion(today_date);
        }
    }

    private void getLocalTranscataion(String date) {

        customerTranscationList=CustomerTranscation.getTranscationListDate(date);
        if(customerTranscationList!=null){
            transcationAdaptor.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            default:
                break;
        }
    }

    @Override
    public void onDataSelected() {

    }

    public class TranscationAdaptor extends RecyclerView.Adapter<TranscationAdaptor.MyViewHolder> {


        public class MyViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_contact)
            TextView tv_contact;
            @BindView(R.id.tv_customer_name)
            TextView tv_customer_name;
            @BindView(R.id.tv_detail)
            TextView tv_detail;
             @BindView(R.id.iv_call)
            ImageView iv_call;
            @BindView(R.id.tv_payment_data)
            TextView tv_payment_data;
             @BindView(R.id.tv_amount)
            TextView tv_amount;

            public MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this,view);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_payment_date, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final CustomerTranscation customerTranscation = customerTranscationList.get(position);
            if(customerTranscation!=null){
                final Customer customer=Customer.getCustomer(customerTranscation.getCustomer_id());
                if(customer!=null){
                    if(Validation.isRequiredField(customer.getName())){
                        holder.tv_customer_name.setText(customer.getName());
                    }
                    if(Validation.isRequiredField(customer.getMobile())){
                        holder.tv_contact.setText(customer.getMobile());
                        holder.iv_call.setVisibility(View.VISIBLE);
                        holder.iv_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+customer.getMobile()));
                                startActivity(intent);
                            }
                        });


                    }else {
                        holder.tv_contact.setText(mActivity.getResources().getString(R.string.nan));
                        holder.iv_call.setVisibility(View.GONE);

                    }
                }

                if(Validation.isRequiredField(customerTranscation.getPaymentTermdate())){
                    holder.tv_payment_data.setText("("+mActivity.getResources().getString(R.string.today)+")"+customerTranscation.getPaymentTermdate());
                }
                if(Validation.isRequiredField(customerTranscation.getDescraption())){
                    holder.tv_detail.setText(customerTranscation.getDescraption());
                }
                if(Validation.isRequiredField(customerTranscation.getAmount())){
                    holder.tv_amount.setText(customerTranscation.getAmount()+"/-");
                }
            }
        }

        @Override
        public int getItemCount() {
            if(customerTranscationList.isEmpty() && customerTranscationList.size()==0){
                tv_not_found.setVisibility(View.VISIBLE);
                if(is_that_data){
                    tv_not_found.setText(""+that_date+" "+mActivity.getResources().getString(R.string.that_date_not_payment_customer));
                }
            }else {
                tv_not_found.setVisibility(View.GONE);
            }
            return customerTranscationList.size();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(br_mainActivity);

    }
}
