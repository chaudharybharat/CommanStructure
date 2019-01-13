package com.example.agc_linux.accounting.fragment;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonmodule.mi.utils.Validation;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.dialog.SweetAlertDialog;
import com.example.agc_linux.accounting.dialog.SweetEdit_Dialog;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.example.agc_linux.accounting.model.CustomerTranscation_Table;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.example.agc_linux.accounting.util.MyPreferences;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTranscationFragment extends BaseFragment implements View.OnClickListener, SweetEdit_Dialog.onUpdateTranscation {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_total_dr)
    TextView tv_total_dr;
    @BindView(R.id.total_cr)
    TextView tv_totla_cr;
    @BindView(R.id.ll_total)
    LinearLayout ll_total;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.tv_not_found)
    TextView tv_not_found;
    @BindView(R.id.header)
    LinearLayout ll_header;
    @BindView(R.id.iv_excle)
    ImageView iv_excle;
    @BindView(R.id.iv_share)
    ImageView iv_share;
    @BindView(R.id.bottom_view)
    View bottom_view;
    private List<CustomerTranscation> transactionList;
    TranscationAdaptor transcationAdaptor;
    int customer_id;
    String customer_name="";
    long total_cradit=00;
    long total_debit=00;
    public static String profile_pic="";
    public static int payment_date_color;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT =122 ;
    public static ShowTranscationFragment getInstance(String customer,int customer_id){
        ShowTranscationFragment showTranscationFragment=new ShowTranscationFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(StaticConfig.CUSTOMER_ID,customer_id);
        bundle.putString(StaticConfig.CUSTOMER_NAME,customer);
        showTranscationFragment.setArguments(bundle);
        return showTranscationFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.show_transcation_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        getBundleValue();
        initComponet();
        setHeader();
    }
    public void getBundleValue(){
        Bundle bundle=getArguments();
        if(bundle!=null){
            if(bundle.containsKey(StaticConfig.CUSTOMER_ID)){
                customer_id=bundle.getInt(StaticConfig.CUSTOMER_ID);
            }
            if(bundle.containsKey(StaticConfig.CUSTOMER_NAME)){
                customer_name=bundle.getString(StaticConfig.CUSTOMER_NAME);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public void setHeader() {
        mActivity.setToolbarLayout(false,true,false,true,true,customer_name,false);
    }
    private void initComponet() {
       payment_date_color= mActivity.getResources().getColor(R.color.colorPrimary);
        transactionList = new ArrayList<>();
        transcationAdaptor = new TranscationAdaptor();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(transcationAdaptor);
      getLocalTranscataion();

    }
    private void getLocalTranscataion() {
        total_cradit=00;
        total_debit=00;
        transactionList= CustomerTranscation.getTranscationList(customer_id);
        if(transactionList!=null &&!transactionList.isEmpty()){
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
            ll_total.setVisibility(View.VISIBLE);
            ll_header.setVisibility(View.VISIBLE);
            tv_not_found.setVisibility(View.GONE);
            bottom_view.setVisibility(View.VISIBLE);
            for (int i = 0; i <transactionList.size() ; i++) {
                CustomerTranscation  customerTranscation=transactionList.get(i);
                if(customerTranscation!=null){
                    if(customerTranscation.getTranscation_type().equalsIgnoreCase("1")){
                        total_cradit=total_cradit+Integer.parseInt(customerTranscation.getAmount());
                    }else {
                        total_debit=total_debit+Integer.parseInt(customerTranscation.getAmount());
                    }
                    tv_totla_cr.setText(""+total_cradit);
                    tv_total_dr.setText(""+total_debit);
                }
            }
        }else {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            ll_total.setVisibility(View.GONE);
            ll_header.setVisibility(View.GONE);
            tv_not_found.setVisibility(View.VISIBLE);
            bottom_view.setVisibility(View.GONE);
        }
        transcationAdaptor.notifyDataSetChanged();
    }



    @Override
    public void onUpdateTransnction() {
        mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.transanction_update_success_message));
        total_debit=00;
        total_cradit=00;
        if(transactionList!=null && !transactionList.isEmpty()){
            transactionList.clear();
        }
       getLocalTranscataion();
    }

    @OnClick({R.id.iv_excle,R.id.iv_share})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_excle:
                takePermmistion(false);
                break;
            case R.id.iv_share:
                takePermmistion(true);

                break;
            default:
                break;
        }
    }

    private void share_excleFile(File path) {


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("*/*");

        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(path));
        share.putExtra(Intent.EXTRA_TEXT,
                "" + getResources().getString(R.string.app_name));

        startActivity(Intent.createChooser(share, getString(R.string.share_excle_file)));
       /* Uri path_uri = Uri.fromFile(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setDataAndType(path_uri,"application/vnd.ms-excel");
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mActivity, "No Application available to view XLS", Toast.LENGTH_SHORT).show();
        }*/


    }


    private void takePermmistion(boolean isShare) {
        if(CommnMethod.is_marshmallow()){
            int permission = ContextCompat.checkSelfPermission(mActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);
       int permission_write = ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED && permission_write!=PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }else {

                    generateTransactionXcl(isShare);

            }
        }else {

                generateTransactionXcl(isShare);

        }
    }
   String file_name="";
    private void generateTransactionXcl(final boolean is_share) {
        file_name="";

        if(transactionList!=null && !transactionList.isEmpty()){
            File sd = new File(Environment.getExternalStorageDirectory() + File.separator +StaticConfig.APP_NAME);

            final String outFileName = Environment.getExternalStorageDirectory() + File.separator + StaticConfig.APP_NAME+ File.separator;
            final File directory = new File(sd.getAbsolutePath());
            //create directory if not exist
            boolean success = true;
            if (!directory.exists())
                success = directory.mkdirs();
            if (success) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mActivity.getResources().getString(R.string.Enter_file_name));
                final EditText input = new EditText(mActivity);
                String str=customer_name+"_"+CommnMethod.getCurrentDate();
                input.setText(""+str);
                if(str!=null && str.length()<0){
                    input.setSelection(str.length());
                }
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton(mActivity.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if(Validation.isRequiredField(m_Text)){
                            String out = m_Text + ".xls";
                            file_name=m_Text;
                            generateTransactionListXcl(directory,out,transactionList,is_share);
                            dialog.dismiss();
                        }else {
                            Toast.makeText(mActivity,mActivity.getResources().getString(R.string.Enter_file_name),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            } else
                Toast.makeText(mActivity, R.string.direcory_create_error, Toast.LENGTH_SHORT).show();
        }else
        {
            throw new Error("ERRO UnExppected Query data fetch wrappedCursor null object ");
        }


    }

    private void generateTransactionListXcl(File directory, String file_name, List<CustomerTranscation> transactionList,boolean is_share) {
        try {

            File file = new File(directory, file_name);
            WorkbookSettings wbSettings = new WorkbookSettings();

            String langaue=  MyPreferences.getPref(mActivity,StaticConfig.LANGAUGE);
            Locale locale = new Locale(langaue);
            wbSettings.setLocale(locale);
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet(customer_name+"_"+getString(R.string.trnsaction_list), 0);
            //font heare
            WritableCellFormat cFormat = new WritableCellFormat();
            WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            cFormat.setFont(font);
            cFormat.setAlignment(Alignment.CENTRE);
            //cFormat.setWrap(false);


            //forn detail
            WritableCellFormat AllFormat = new WritableCellFormat();
            WritableFont aall_font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
            AllFormat.setFont(aall_font);
            AllFormat.setAlignment(Alignment.RIGHT);
            // Lets automatically wrap the cells

            int col=0;
            sheet.addCell(new Label(col, 0, getString(R.string.no_index),cFormat));
            col=1;
            sheet.setColumnView(col,14);
            sheet.addCell(new Label(col, 0, getString(R.string.date),cFormat));
            col=2;
            sheet.setColumnView(col,20);
            sheet.addCell(new Label(col, 0, getString(R.string.detail),cFormat));
            col=3;
            sheet.setColumnView(col,15);
            sheet.addCell(new Label(col, 0, getString(R.string.cradit),cFormat));
            col=4;
            sheet.setColumnView(col,15);
            sheet.addCell(new Label(col, 0, getString(R.string.debit),cFormat));

             int size=transactionList.size();
            for (int i = 0; i <size ; i++) {
                int col_number = i + 1;
                CustomerTranscation customerTranscation=transactionList.get(i);

                String date_trasncation=customerTranscation.getTranscation_date()!=null?customerTranscation.getTranscation_date():"--";
                String detail=customerTranscation.getDescraption()!=null?customerTranscation.getDescraption():"--";
                String amount=customerTranscation.getAmount()!=null?customerTranscation.getAmount():"--";

                sheet.addCell(new Label(0,col_number,""+col_number,AllFormat));
                sheet.addCell(new Label(1, col_number,date_trasncation,AllFormat));
                sheet.addCell(new Label(2, col_number,detail,AllFormat));
                if(customerTranscation.getTranscation_type().equalsIgnoreCase("1")){

                    sheet.addCell(new Label(3, col_number,amount,AllFormat));
                    sheet.addCell(new Label(4, col_number," -- ",AllFormat));

                }else {
                    sheet.addCell(new Label(3, col_number," -- ",AllFormat));
                    sheet.addCell(new Label(4, col_number,amount,AllFormat));
                }

            }
            WritableCellFormat totlaFormat = new WritableCellFormat();
            WritableFont totla_font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            totlaFormat.setFont(totla_font);
            totlaFormat.setAlignment(Alignment.RIGHT);
          //  cFormat.setAlignment(Alignment.RIGHT);
            sheet.addCell(new Label(2, transactionList.size()+1,mActivity.getResources().getString(R.string.total),totlaFormat));
            sheet.addCell(new Label(3, transactionList.size()+1,""+total_cradit,totlaFormat));
            sheet.addCell(new Label(4, transactionList.size()+1,""+total_debit,totlaFormat));
            workbook.write();
            workbook.close();

            if(is_share){
                final String outFileName = Environment.getExternalStorageDirectory() + File.separator + StaticConfig.APP_NAME+ File.separator;
                File shareFile = new File(Environment.getExternalStorageDirectory() + File.separator +StaticConfig.APP_NAME+File.separator+file_name);
               if(shareFile.exists())
               {
                   share_excleFile(shareFile.getAbsoluteFile());
               }else {
                   mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.excle_file_not_found));
               }
            }
            mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.excle_file_create_success_messge));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    class TranscationAdaptor extends RecyclerSwipeAdapter<TranscationAdaptor.MyViewHolder> {
        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe_raw;

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            @BindView(R.id.tv_no)
            TextView tv_no;
            @BindView(R.id.tv_date)
            TextView tv_date;
            @BindView(R.id.tv_cr)
            TextView tv_cr;
            @BindView(R.id.tv_dr)
            TextView tv_dr;
            @BindView(R.id.tv_descaption)
            TextView tv_descraption;
            @BindView(R.id.swipe_raw)
            SwipeLayout swipe_raw;
            @BindView(R.id.tv_edit)
            ImageView tv_edit;
            @BindView(R.id.tv_delete)
            ImageView tv_delete;
            public MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
        @Override
        public TranscationAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_transcation, parent, false);

            return new TranscationAdaptor.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TranscationAdaptor.MyViewHolder holder, final int position) {
            final CustomerTranscation transaction = transactionList.get(position);
            if (transaction != null) {
                if (Validation.isRequiredField(transaction.getTranscation_date())) {
                    int no = position + 1;
                    holder.tv_no.setText("" + no);
                    holder.tv_date.setText(transaction.getTranscation_date());
                    if (transaction.getTranscation_type().equalsIgnoreCase("1")) {
                        holder.tv_cr.setText(transaction.getAmount());
                        holder.tv_dr.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        holder.tv_dr.setText("--");

                    } else {
                        holder.tv_dr.setText(transaction.getAmount());
                        holder.tv_cr.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        holder.tv_cr.setText("--");

                    }
                    if(Validation.isRequiredField(transaction.getDescraption())){

                        if(transaction.getTranscation_type().equalsIgnoreCase("0")){
                            holder.tv_descraption.setTextColor(mActivity.getResources().getColor(R.color.blue));
                        }else {
                            if(transaction.getDescraption().length()>8){
                                holder.tv_descraption.setTextColor(mActivity.getResources().getColor(R.color.blue));
                            }else {
                                holder.tv_descraption.setTextColor(mActivity.getResources().getColor(R.color.black));

                            }
                        }

                        holder.tv_descraption.setText(transaction.getDescraption());

                    }else {
                        if(transaction.getTranscation_type().equalsIgnoreCase("0")){
                            holder.tv_descraption.setTextColor(mActivity.getResources().getColor(R.color.blue));
                        }else {
                            holder.tv_descraption.setTextColor(mActivity.getResources().getColor(R.color.white));

                        }
                        holder.tv_descraption.setText(" -- ");

                    }
                }
              holder.tv_descraption.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if(transaction.getTranscation_type().equalsIgnoreCase("0")){
                              opentDescraptionDilaog(transaction);

                      }else {
                          if(transaction.getDescraption().length()>8){
                              opentDescraptionDilaog(transaction);
                          }

                      }
                  }
              });
                holder.tv_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.swipe_raw.close();

                        openEditTranscationDialog(transactionList.get(position));
                    }
                });
                holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.swipe_raw.close();
                        openTranscationDeleteConfirmDialog(transaction.getUniqeId());
                    }
                });
            }
            mItemManger.bindView(holder.swipe_raw, position);
        }

        @Override
        public int getItemCount() {
            return transactionList.size();
        }
    }

    public void opentDescraptionDilaog(CustomerTranscation  customerTranscation) {
        long time = 0;
        String payment_date=customerTranscation.getPaymentTermdate();
        try {
          if(!customerTranscation.getPaymentTermdate().equalsIgnoreCase("0"))
          {
              DateFormat formatter = new SimpleDateFormat(StaticConfig.DATE_FORMATE);
              Date date = (Date) formatter.parse(customerTranscation.getPaymentTermdate());
              time = date.getTime();
              payment_date=getTimeAgo(time);
          }else {
              payment_date_color=mActivity.getResources().getColor(R.color.colorPrimary);
          }
        }catch(Exception e){
            throw  new Error("ERROR DATE FORMATE EXCEPTION "+e.getMessage());
        }

        String all_string;
        String message=customerTranscation.getTranscation_type().equalsIgnoreCase("0")?customerTranscation.getPaymentTermdate().equalsIgnoreCase("0")?mActivity.getResources().getString(R.string.not_payment_term):getString(R.string.payment_term_dialog)+payment_date+"\n"+customerTranscation.getPaymentTermdate():"";
        SpannableString spannable;
       String first= customerTranscation.getDescraption();
        if(message!=null){
            all_string=first+"\n"+message;
            spannable= new SpannableString(all_string);
            int start = first.length();
            int end = start + message.length()+1;
            spannable.setSpan(new ForegroundColorSpan(payment_date_color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }else {
            spannable= new SpannableString(first);
        }
        // here we set the color

        new SweetAlertDialog(mActivity, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleTextSpanable(spannable).setConfirmText(getString(R.string.dialog_ok))
                .setCancelableDialog(true)
                .setCancleButtonVisiblityGone()
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        //  removeTranscationEntry(unique);
                    }
                }).show();

    }

    private void openTranscationDeleteConfirmDialog(final int unique) {
        new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.transanction_delete_alert_message))
                .setConfirmText(getString(R.string.yest_delete_it))
                .setCancelableDialog(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        removeLocalDataTranscation(unique);
                      //  removeTranscationEntry(unique);

                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void removeLocalDataTranscation(int unique) {
        SQLite.delete().from(CustomerTranscation.class).where(CustomerTranscation_Table.uniqeId.is(unique)).async().execute();
       transactionList.clear();
        getLocalTranscataion();
    }

    private void openEditTranscationDialog(CustomerTranscation transcation) {

        new SweetEdit_Dialog(mActivity, SweetAlertDialog.WARNING_TYPE,transcation,this).show();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setHeader();
        }
    }
    public static String getTimeAgo(long time) {
   String YEAR_AGO = mActivity.getResources().getString(R.string.year_ago);

        String YEARS_AGO = mActivity.getResources().getString(R.string.years);

         String YEAR_TO_GO = mActivity.getResources().getString(R.string.yeargo);

        String YEARS_TO_GO =mActivity.getResources().getString(R.string.years_go);

        String MONTHS_AGO = mActivity.getResources().getString(R.string.monthsago);

         String MONTH_AGO =mActivity.getResources().getString(R.string.month_ago);

      String MONTHS_TO_GO =mActivity.getResources().getString(R.string.months_go);

        String MONTH_TO_GO = mActivity.getResources().getString(R.string.month_go);

       String WEEKS_AGO =  mActivity.getResources().getString(R.string.weeks_ago);

         String WEEK_AGO =  mActivity.getResources().getString(R.string.week_ago);

        String WEEKS_TO_GO =  mActivity.getResources().getString(R.string.weeks_to_go);

        String WEEK_TO_GO =  mActivity.getResources().getString(R.string.week_to_go);

        String DAYS_AGO =  mActivity.getResources().getString(R.string.days_ago);

         String DAY_AGO =  mActivity.getResources().getString(R.string.day_to_ago);

        String DAYS_TO_GO =  mActivity.getResources().getString(R.string.days_to_go);
        String DAY_TO_GO =  mActivity.getResources().getString(R.string.day_to_go);
         String TODAY =  mActivity.getResources().getString(R.string.today);


        //_________________________CURRENT CALENDER FOR GETTING CURRENT TIMINGS___________________________

        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.setTimeInMillis(System.currentTimeMillis());
        //_________________________SELECTED DATE CALENDER FOR GETTING SELECTED TIMINGS____________________

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        //______________________________________ TIMING DIFFERENCE________________________________________


        int days = calendarCurrent.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
        int months = calendarCurrent.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
        int years = calendarCurrent.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        int daysCal = calendarCurrent.get(Calendar.DAY_OF_MONTH) + CommnMethod.getSurplusDays(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        int minScalc = calendarCurrent.get(Calendar.MINUTE) + CommnMethod.getSurplusMinutes(calendar.get(Calendar.MINUTE));

        String timings = "years " + years + "\nmonths " + months + "\ndays " + days ;
        StringBuffer stringBuffer=new StringBuffer();
        if(years!=0){
            stringBuffer.append("Year =>"+years);
        }
        if(months!=0){
            stringBuffer.append("\n Month=>"+months);
        }
        stringBuffer.append("\n"+days);

        //_______________________________CONDITIONS WHICH WILL BE EXECUTED._______________________________

        boolean isMonthPassed = calendar.get(Calendar.DAY_OF_MONTH) <= calendarCurrent.get(Calendar.DAY_OF_MONTH);
        boolean isMonthNOTPassed = calendar.get(Calendar.DAY_OF_MONTH) >= calendarCurrent.get(Calendar.DAY_OF_MONTH);
        boolean isYearNOTPassed = calendar.get(Calendar.MONTH) >= calendarCurrent.get(Calendar.MONTH);
        boolean isYearPassed = calendar.get(Calendar.MONTH) <= calendarCurrent.get(Calendar.MONTH);
        boolean isHourPassed = calendar.get(Calendar.MINUTE) <= calendarCurrent.get(Calendar.MINUTE);
        boolean isMonthOrDayNotPassed = (months < 0 || days < 0);
        boolean isMonthOrDayPassed = (months > 0 || days > 0);


        boolean isWeekAdjusted = daysCal >= 7;
        boolean isWeek =days >= 7;

        boolean isMonthLessThanOREq_1 = months <= 1;
        boolean isMonthPositive = months > 0;

        String duration = "";
//___________________________________________________________________TO GO___________________________________________________________________

        if (time>System.currentTimeMillis())
        {
           payment_date_color=mActivity.getResources().getColor(R.color.colorPrimary);
            if (years < 0)
            {
                years = -(years);

                if (isMonthOrDayPassed && years <= 1)
                {
                    int monthssCalc = calendar.get(Calendar.MONTH) + CommnMethod.getSurplusMonth(calendarCurrent.get(Calendar.MONTH));

                    if (isYearPassed)
                    {
//                    duration = monthssCalc + MONTHS_TO_GO;
                        --monthssCalc;
                        if (monthssCalc == 1)
                        {
                            duration = monthssCalc + MONTH_AGO;
                        }
                        else
                        {
                            duration = monthssCalc + MONTHS_AGO;
                        }
                    }
                    else
                    {
                        if (isMonthPassed)
                        {
                            duration = years + YEAR_TO_GO;
                        }
                        else
                        {
                            --monthssCalc;
                            if (monthssCalc == 1)
                            {
                                duration = monthssCalc + MONTH_AGO;
                            }
                            else
                            {
                                duration = monthssCalc + MONTHS_AGO;
                            }
                        }

                    }
                }
                else if (isMonthOrDayPassed && years > 1)
                {
                    --years;
                    if (years == 1)
                    {
                        duration = years + YEAR_TO_GO;
                    }
                    else
                    {
                        duration = years + YEARS_TO_GO;
                    }
                }
                else
                {
                    if ((years) == 1)
                    {
                        duration = years + YEAR_TO_GO;
                    }
                    else
                    {
                        duration = years + YEARS_TO_GO;
                    }
                }
            }
            else if (isYearNOTPassed && days > 0)
            {
                if (!isMonthPositive)
                {
                    months = -(months);

                    if (isMonthPassed)
                    {
                        --months;
                        if (months == 1)
                        {
                            duration = months + MONTH_AGO;
                        }
                        else
                        {
                            if (months<=0)
                            {
                                int daysCalTOGO = calendar.get(Calendar.DAY_OF_MONTH) + CommnMethod.getSurplusDays(calendarCurrent.get(Calendar.DAY_OF_MONTH), calendarCurrent.get(Calendar.MONTH), calendarCurrent.get(Calendar.YEAR));

                                if (daysCalTOGO >= 7)
                                {
                                    int weeks = daysCalTOGO / 7;
                                    if (weeks == 1) {
                                        duration = weeks + WEEK_AGO;
                                    } else {
                                        duration = weeks + WEEKS_AGO;
                                    }
                                }
                                else
                                {
                                    if (daysCal == 1)
                                    {
                                        duration = DAY_AGO;
                                    }
                                    else
                                    {
                                        duration = daysCal + DAYS_AGO;
                                    }
                                }
                            }
                            else
                            {
                                duration = months + MONTHS_AGO;
                            }
                        }
                    }
                    else {
                        if (months == 1)
                        {
                            duration = months + MONTH_AGO;
                        }
                        else
                        {
                            if (months==0)
                            {
                                int daysCalTOGO = calendar.get(Calendar.DAY_OF_MONTH) + CommnMethod.getSurplusDays(calendarCurrent.get(Calendar.DAY_OF_MONTH), calendarCurrent.get(Calendar.MONTH), calendarCurrent.get(Calendar.YEAR));

                                if (daysCalTOGO >= 7)
                                {
                                    int weeks = daysCalTOGO / 7;
                                    if (weeks == 1)
                                    {
                                        duration = weeks + WEEK_AGO;
                                    }
                                    else
                                    {
                                        duration = weeks + WEEKS_AGO;
                                    }
                                }
                                else
                                {
                                    if (daysCal == 1)
                                    {
                                        duration = DAY_AGO;
                                    }
                                    else
                                    {
                                        duration = daysCal + DAYS_AGO;
                                    }
                                }
                            }
                            else
                            {
                                duration = months + MONTHS_AGO;
                            }
                        }
                    }
                }
                else if (isMonthOrDayPassed)
                {
                    int daysCalTOGO = calendar.get(Calendar.DAY_OF_MONTH) + CommnMethod.getSurplusDays(calendarCurrent.get(Calendar.DAY_OF_MONTH), calendarCurrent.get(Calendar.MONTH), calendarCurrent.get(Calendar.YEAR));

                    if (daysCalTOGO >= 7
                            ) {
                        int weeks = daysCalTOGO / 7;
                        if (weeks == 1)
                        {
                            duration = weeks + WEEK_AGO;
                        }
                        else
                        {
                            duration = weeks + WEEKS_AGO;
                        }
                    }
                    else
                    {
                        if (daysCal == 1)
                        {
                            duration =DAY_AGO;
                        }
                        else
                        {
                            duration = daysCal + DAYS_AGO;
                        }
                    }
                }
                else if (isMonthOrDayNotPassed && months > 1)
                {
                    --months;
                    if (months == 1)
                    {
                        duration = months + MONTH_AGO;
                    }
                    else
                    {
                        duration = months + MONTHS_AGO;
                    }
                } else {
                    if (months == 1) {
                        duration = months + MONTH_AGO;
                    } else {
                        duration = months + MONTHS_AGO;
                    }
                }
            } else if (years < 1 && months < 0) {
                months = -(months);

                if (months == 1) {
                    duration = months + MONTH_TO_GO;
                } else {
                    duration = months + MONTHS_TO_GO;
                }
            } else if (isMonthOrDayPassed) {
                months = -(months);
                --months;
                if (months == 1) {
                    duration = months + MONTH_TO_GO;
                } else {
                    duration = months + MONTH_TO_GO;
                }
            } else if (years < 1 && months < 1 && days < 0) {
                days = -(days);

                if (days == 1) {
                    duration =  DAY_TO_GO;
                } else {
                    if (days >= 7) {
                        int weeks = days / 7;
                        if (weeks == 1) {
                            duration = weeks + WEEK_TO_GO;
                        } else {
                            duration = weeks + WEEKS_TO_GO;
                        }
                    } else {
                        duration = days + DAYS_TO_GO;
                    }
                }
            }

        }

        //___________________________________________________________________AGO___________________________________________________________________
        else
        {
            payment_date_color=mActivity.getResources().getColor(R.color.payment_red);
            if (isYearNOTPassed && years > 0) {
                if (isMonthOrDayNotPassed && years <= 1) {
                    int monthssCalc = calendarCurrent.get(Calendar.MONTH) + CommnMethod.getSurplusMonth(calendar.get(Calendar.MONTH));
                    if (isMonthPassed) {
                        duration = monthssCalc + MONTHS_AGO;
                    } else {
                        --monthssCalc;
                        if (monthssCalc == 1) {
                            duration = monthssCalc + MONTH_AGO;
                        } else {
                            duration = monthssCalc + MONTHS_AGO;
                        }
                    }
                } else if (isMonthOrDayNotPassed && years > 1) {
                    --years;
                    if (years == 1) {
                        duration = years + YEAR_AGO;
                    } else {
                        duration = years + YEARS_AGO;
                    }
                } else {
                    if (years == 1) {
                        duration = years + YEAR_AGO;
                    } else {
                        duration = years + YEARS_AGO;
                    }
                }
            } else if (isYearPassed && years > 0) {
                duration = years + YEARS_AGO;
            } else if (isMonthNOTPassed && months > 0) {
                if (isMonthOrDayNotPassed && isMonthLessThanOREq_1) {
                    if (isWeekAdjusted) {
                        int weeks = daysCal / 7;
                        if (weeks == 1) {
                            duration = weeks + WEEK_AGO;
                        } else {
                            duration = weeks + WEEKS_AGO;
                        }
                    } else {
                        if (daysCal == 1) {
                            duration =  DAY_AGO;
                        } else {
                            duration = daysCal + DAYS_AGO;
                        }
                    }
                } else if (isMonthOrDayNotPassed && months > 1) {
                    --months;
                    if (months == 1) {
                        duration = months + MONTH_AGO;
                    } else {
                        duration = months + MONTHS_AGO;
                    }
                } else {
                    if (months == 1) {
                        duration = months + MONTH_AGO;
                    } else {
                        duration = months + MONTHS_AGO;
                    }
                }
            } else if (isMonthPassed && isMonthPositive) {
                duration = months + MONTHS_AGO;
            } else if (isMonthOrDayNotPassed) {
                --months;
                if (months == 1) {
                    duration = months + MONTH_AGO;
                } else {
                    duration = months + MONTHS_AGO;
                }
            }
            else if (days > 0)
            {
                if (isWeek)
                {
                    int weeks = days / 7;
                    if (weeks == 1)
                    {
                        duration = weeks + WEEK_AGO;
                    }
                    else
                    {
                        duration = weeks + WEEKS_AGO;
                    }
                }
                else
                {
                    if (days == 1)
                    {
                        duration = DAY_AGO;
                    }
                    else
                    {
                        duration = days + DAYS_AGO;
                    }
                }
            }else {
           payment_date_color=mActivity.getResources().getColor(R.color.payment_green);
                duration=TODAY;
            }

        }

        return duration;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
            mActivity.showSnakbarMessage(getString(R.string.try_again));
            //generateCustomerListXcl();
        }
    }

}
