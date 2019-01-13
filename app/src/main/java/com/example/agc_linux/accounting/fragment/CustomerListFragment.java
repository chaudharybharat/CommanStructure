package com.example.agc_linux.accounting.fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonmodule.mi.utils.ConnectionUtil;
import com.commonmodule.mi.utils.Validation;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.dialog.SweetAlertDialog;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.CustomerTranscation;

import com.example.agc_linux.accounting.model.CustomerTranscation_Table;
import com.example.agc_linux.accounting.model.Customer_Table;
import com.example.agc_linux.accounting.rimindertask.ReminderManager;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.example.agc_linux.accounting.util.RecyclerTouchListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_not_found)
    TextView tv_not_found;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;
    private List<Customer> customerList = new ArrayList<>();
    CustomerAdaptor customerAdaptor;

    public static CustomerListFragment getInstance(){
        CustomerListFragment listFragment=new CustomerListFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(StaticConfig.USER_COMMING_HOME,true);
        listFragment.setArguments(bundle);
        return listFragment;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initComponet();
        Bundle bundle=getArguments();
        if(bundle!=null){
            if(bundle.containsKey(StaticConfig.USER_COMMING_HOME)){
                mActivity.setToolbarLayout(false,true,false,true,true,mActivity.getResources().getString(R.string.customer_list),false);
            }else {
                setHeader();
            }
        }else {
            setHeader();
        }
    }
public void setHeader(){
    mActivity.setToolbar(StaticConfig.CUSTOMERLIST);
}
    private void initComponet() {
    //    setRiminderTranscation();
        swipe_refresh_layout.setOnRefreshListener(this);
        swipe_refresh_layout.setColorSchemeColors(mActivity.getResources().getColor(R.color.colorPrimary));
        mActivity.edt_serach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int lengh) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int lengh) {
                if(lengh==0){
                    customerAdaptor.updateList(customerList);
                }else {

                    filter(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        customerAdaptor = new CustomerAdaptor(customerList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(customerAdaptor);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(mActivity, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(mActivity, "Single Click on item :" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                if(customerList!=null){

                    Customer customer=customerList.get(position);
                    mActivity.pushFragmentIgnoreCurrent(CustomerEditFragment.getInstance(customer.getCustome_id()),mActivity.FRAGMENT_ADD_TO_BACKSTACK_AND_ADD);

                }
               // Toast.makeText(mActivity, "Long Click on item :" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleClick(View view, int position) {
              //  Toast.makeText(mActivity, "Double Click on item :" + position, Toast.LENGTH_SHORT).show();
            }
        }));
        getLocalCustomerList();
    }

    private void setRiminderTranscation() {

        List<CustomerTranscation> customerTranscationslist=CustomerTranscation.getTranscationList();
        if(customerTranscationslist!=null){
            for (int i = 0; i <customerTranscationslist.size() ; i++) {

                CustomerTranscation  customerTranscation=customerTranscationslist.get(i);
                if(Validation.isRequiredField(customerTranscation.getPaymentTermdate())){
                    if(!customerTranscation.getPaymentTermdate().equalsIgnoreCase("0")) {


                        String dateTime = customerTranscation.getPaymentTermdate();
                        int uniqueId = customerTranscation.getUniqeId();
                        Log.e("test", "====>>>" + uniqueId);
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat(StaticConfig.DATE_FORMATE);

                        try {
                            java.util.Date date = format.parse(dateTime);
                            cal.setTime(date);
                            new ReminderManager(mActivity).setReminder(uniqueId, cal);
                        } catch (java.text.ParseException e) {
                            Log.e("OnBootReceiver", e.getMessage(), e);

                            throw new Error("ERROR UNEXPECTED ERROR SET REMINDERTIME CUSTOMER" + e.getMessage());
                        }
                    }
                }

            }
        }
    }

    public void filter(String text){
        text=CommnMethod.firstlaterCapse(text);
        List<Customer> temp = new ArrayList();
       if(customerList.size()>0){
           for(Customer d: customerList){

               //or use .equal(text) with you want equal match
               //use .toLowerCase() for better matches
               if(d.getName().contains(text)){
                   temp.add(d);
               }
           }
           //update recyclerview
           customerAdaptor.updateList(temp);
       }
    }

    private void getLocalCustomerList() {
        customerList=Customer.getCustomerList();
        if(customerList!=null) {
            customerAdaptor.updateList(customerList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.customer_list_fragment, container, false);
    }

    @Override
    public void onRefresh() {
        if(customerList!=null && !customerList.isEmpty()){
            customerList.clear();
        }
        getLocalCustomerList();
    }


    class CustomerAdaptor extends RecyclerSwipeAdapter<CustomerAdaptor.MyViewHolder> {
         List<Customer> customerList;

         public CustomerAdaptor(List<Customer> customerList) {
             this.customerList = customerList;
         }

         public void updateList(List<Customer> customerList){
             this.customerList=customerList;
             notifyDataSetChanged();
         }

         @Override
         public int getSwipeLayoutResourceId(int position) {
             return R.id.swipe_raw;
         }


         public class MyViewHolder extends RecyclerView.ViewHolder {
             @BindView(R.id.tv_name)
            TextView tv_name;
            @BindView(R.id.tv_mobile)
            TextView tv_mobile;
             @BindView(R.id.tv_address)
            TextView tv_address;
            @BindView(R.id.iv_profile)
            SimpleDraweeView iv_profile;
            @BindView(R.id.swipe_raw)
            SwipeLayout swipe_raw;
             @BindView(R.id.tv_show_transcation)
             ImageView tv_showtranscation;
             @BindView(R.id.tv_delete)
             ImageView tv_delete;
             @BindView(R.id.tv_addtranscation)
             ImageView tv_addtranscation;
             @BindView(R.id.ll_main)
             LinearLayout ll_main;

            public MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_customer, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final Customer customer = customerList.get(position);
            if (customer != null) {
                if (Validation.isRequiredField(customer.getName())) {
                    holder.tv_name.setText(customer.getName());
                }
                if(Validation.isRequiredField(customer.getProfile_pic())){
                    {
                        Bitmap bitmap=null;
                        try{
                            byte[] imageAsBytes = Base64.decode(customer.getProfile_pic().getBytes(),Base64.DEFAULT);
                             bitmap=BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        }catch (Exception e){

                        }

                        if(bitmap!=null){
                            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            roundedBitmapDrawable.setCornerRadius(100.0f);
                            roundedBitmapDrawable.setAntiAlias(true);
                            holder.iv_profile.setImageDrawable(roundedBitmapDrawable);
                            //bitmap.recycle();
                        }
                    }
                }
                if (Validation.isRequiredField(customer.getMobile())) {
                    holder.tv_mobile.setText(customer.getMobile());
                } if (Validation.isRequiredField(customer.getAddress())) {
                    holder.tv_address.setVisibility(View.VISIBLE);
                    holder.tv_address.setText(customer.getAddress());
                }else {
                    holder.tv_address.setVisibility(View.GONE);
                }
                holder.tv_addtranscation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.swipe_raw.close();
                        AddTranscationFragment.profile_pic=customer.getProfile_pic();
                        mActivity.pushFragmentDontIgnoreCurrent(AddTranscationFragment.getInstance(customer.getName(),customer.getCustome_id()),mActivity.FRAGMENT_ADD_TO_BACKSTACK_AND_ADD);

                    }
                });

                holder.tv_showtranscation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.swipe_raw.close();

                        ShowTranscationFragment.profile_pic=customer.getProfile_pic();
                        mActivity.pushFragmentDontIgnoreCurrent(ShowTranscationFragment.getInstance(customer.getName(),customer.getCustome_id()),mActivity.FRAGMENT_ADD_TO_BACKSTACK_AND_ADD);

                    }
                });
                holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.swipe_raw.close();
                        openConfirmDialog(customer.getName(),customer.getCustome_id());

                    }
                });

            }

            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.swipe_raw.open();
                }
            });


            mItemManger.bindView(holder.swipe_raw, position);
        }

        @Override
        public int getItemCount() {
            if(customerList.size()==0){
                tv_not_found.setVisibility(View.VISIBLE);
            }else {
                tv_not_found.setVisibility(View.GONE);
            }
            return customerList.size();
        }
      }

    private void openConfirmDialog(final String customer_name, final int custome_id) {
        new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.customer_delete_alert))
                .setConfirmText(getString(R.string.yest_delete_it))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        removeCustomer(customer_name,custome_id);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void removeCustomer(final String customer_name,int customer_id) {

        SQLite.delete().from(Customer.class).where(Customer_Table.name.eq(customer_name)).async().execute();
        SQLite.delete().from(CustomerTranscation.class).where(CustomerTranscation_Table.customer_id.is(customer_id)).async().execute();

        getLocalCustomerList();

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setHeader();

        }
    }
}
