package com.example.agc_linux.accounting.fragment;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.example.agc_linux.accounting.util.MyPreferences;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.io.File;
import java.util.Locale;

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
public class GenrateXclFileFragment extends BaseFragment implements View.OnClickListener{

    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT =122 ;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        setHeader();
    }

    private void setHeader() {
        mActivity.setToolbar(StaticConfig.GENRATEXCLFILE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genrate_xcl_file, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick({R.id.tv_customer_list,R.id.tv_transcation,R.id.tv_show_xlc})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_customer_list:
                takePermmistion(true);
                break;
            case R.id.tv_transcation:
                takePermmistion(false);
            break;
            case R.id.tv_show_xlc:
                takePermmistion();
                break;
            default:
                break;
        }
    }
    private void takePermmistion() {
        if(CommnMethod.is_marshmallow()){
            int permission_read = ContextCompat.checkSelfPermission(mActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int permission_write = ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission_read != PackageManager.PERMISSION_GRANTED && permission_write != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }else {
                openExcelFile();
            }
        }else {
            openExcelFile();
        }
    }
    private void openExcelFile() {


        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            if(files.length==0){
                mActivity.showSnakbarMessage(getString(R.string.not_found_excel_file));
                return;
            }

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity,R.layout.spinnet_textview);
            for (File file : files)
                if(file.getName().endsWith(".xls")){
                    arrayAdapter.add(file.getName());
                }

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(mActivity);
            builderSingle.setTitle(R.string.excel_file);
            builderSingle.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                openXlcFile(files[which].getPath());
                                Log.e("test","==>"+which);
                            } catch (Exception e) {
                                mActivity.showSnakbarMessage(getString(R.string.unable_try_again));
                            }
                        }
                    });
            builderSingle.show();
        } else
            mActivity.showSnakbarMessage(getString(R.string.folder_error));
    }

    private void openXlcFile(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "application/vnd.ms-excel");
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
           mActivity.showSnakbarMessage(getString(R.string.file_is_not_support_excle));
        }
    }

    private void takePermmistion(boolean is_value) {
        if(CommnMethod.is_marshmallow()){
            int permission_read = ContextCompat.checkSelfPermission(mActivity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int permission_write = ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission_read != PackageManager.PERMISSION_GRANTED && permission_write != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }else {
                if(is_value){
                    generateCustomerListXcl();
                }else {
                    generateTransactionXcl();
                }
            }
        }else {
            if(is_value){
                generateCustomerListXcl();
            }else {
                generateTransactionXcl();
            }
        }
    }
    private void generateCustomerListXcl() {
        
        FlowCursor query= SQLite.select().from(Customer.class).query();
        final Cursor wrappedCursor = query.getWrappedCursor();

        int log= wrappedCursor.getCount();
        Log.e("test","==>"+log);

        if(wrappedCursor!=null){
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
                input.setText("Customer_list"+CommnMethod.getCurrentDate());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton(mActivity.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        String out = m_Text + ".xls";
                        generateCustomerXcl(directory,out,wrappedCursor);
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
    private void generateTransactionXcl() {

        FlowCursor query= SQLite.select().from(CustomerTranscation.class).query();
        final Cursor wrappedCursor = query.getWrappedCursor();

        int log= wrappedCursor.getCount();
        Log.e("test","==>"+log);

        if(wrappedCursor!=null){
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
                input.setText("Transction_list"+CommnMethod.getCurrentDate());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton(mActivity.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        String out =m_Text + ".xls";
                        generateTransactionListXcl(directory,out,wrappedCursor);
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

    public void generateCustomerXcl(File directory,String file_name,Cursor cursor){
        
        
        try {

            File file = new File(directory, file_name);
            WorkbookSettings wbSettings = new WorkbookSettings();

            String langaue=  MyPreferences.getPref(mActivity,StaticConfig.LANGAUGE);
            Locale locale = new Locale(langaue);
            wbSettings.setLocale(locale);
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Customer List", 0);
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
           // AllFormat.setAlignment(Alignment.CENTRE);
            // Lets automatically wrap the cells


               /* Label label = new Label(0, 0, "Test Count", cFormat);
                sheet.addCell(label);
               // Number number = new Number(0, 1, 1);
                label = new Label(1, 0, "Result", cFormat);
                sheet.addCell(label);
*/
             /*   WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
                cellFormat.setBackground(Colour.ORANGE);
                cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
*/

              /* WritableFont times10ptBoldUnderline = new WritableFont(
                        WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                       null);
            WritableCellFormat  timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
                // Lets automatically wrap the cells
                timesBoldUnderline.setWrap(true);*/

            int col=0;
            sheet.addCell(new Label(col, 0, getString(R.string.no_index),cFormat));
            col=1;
            sheet.setColumnView(col,20);
            sheet.addCell(new Label(col, 0, getString(R.string.name),cFormat));
            col=2;
            sheet.setColumnView(col,20);
            sheet.addCell(new Label(col, 0, getString(R.string.mobile),cFormat));
            col=3;
            sheet.setColumnView(col,50);
            sheet.addCell(new Label(col, 0, getString(R.string.address),cFormat));

         
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String phone = cursor.getString(cursor.getColumnIndex("mobile"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));

                    int col_number = cursor.getPosition() + 1;
                    sheet.addCell(new Label(0,col_number,""+col_number,AllFormat));
                    sheet.addCell(new Label(1, col_number, name,AllFormat));
                    sheet.addCell(new Label(2, col_number,phone,AllFormat));
                    sheet.addCell(new Label(3, col_number, address,AllFormat));
                } while (cursor.moveToNext());
            }
            //closing cursor
            cursor.close();
            workbook.write();
            workbook.close();
            Toast.makeText(mActivity, R.string.excle_file_create_success_messge, Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void generateTransactionListXcl(File directory,String file_name,Cursor cursor){


        try {

            File file = new File(directory, file_name);
            WorkbookSettings wbSettings = new WorkbookSettings();

            String langaue=  MyPreferences.getPref(mActivity,StaticConfig.LANGAUGE);
            Locale locale = new Locale(langaue);
            wbSettings.setLocale(locale);
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("Transaction List", 0);
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
           /// AllFormat.setAlignment(Alignment.CENTRE);
            // Lets automatically wrap the cells


               /* Label label = new Label(0, 0, "Test Count", cFormat);
                sheet.addCell(label);
               // Number number = new Number(0, 1, 1);
                label = new Label(1, 0, "Result", cFormat);
                sheet.addCell(label);
*/
             /*   WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
                cellFormat.setBackground(Colour.ORANGE);
                cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
*/

              /* WritableFont times10ptBoldUnderline = new WritableFont(
                        WritableFont.TIMES, 10, WritableFont.NO_BOLD, false,
                       null);
            WritableCellFormat  timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
                // Lets automatically wrap the cells
                timesBoldUnderline.setWrap(true);*/

            int col=0;
            sheet.addCell(new Label(col, 0, getString(R.string.no_index),cFormat));
            col=1;
            sheet.setColumnView(col,20);
            sheet.addCell(new Label(col, 0, getString(R.string.name),cFormat));
            col=2;
            sheet.setColumnView(col,20);
            sheet.addCell(new Label(col, 0, getString(R.string.mobile),cFormat));
            col=3;
            sheet.setColumnView(col,50);
            sheet.addCell(new Label(col, 0, getString(R.string.address),cFormat));


            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String phone = cursor.getString(cursor.getColumnIndex("mobile"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));

                    int col_number = cursor.getPosition() + 1;
                    sheet.addCell(new Label(0,col_number,""+col_number,AllFormat));
                    sheet.addCell(new Label(1, col_number, name,AllFormat));
                    sheet.addCell(new Label(2, col_number,phone,AllFormat));
                    sheet.addCell(new Label(3, col_number, address,AllFormat));
                } while (cursor.moveToNext());
            }
            //closing cursor
            cursor.close();
            workbook.write();
            workbook.close();
            Toast.makeText(mActivity, R.string.excle_file_create_success_messge, Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
        {
          mActivity.showSnakbarMessage(getString(R.string.try_again));
            //generateCustomerListXcl();
        }
    }
}
