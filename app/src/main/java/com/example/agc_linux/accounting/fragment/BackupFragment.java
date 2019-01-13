package com.example.agc_linux.accounting.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
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

import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.db.MyDatabase;
import com.example.agc_linux.accounting.dialog.SweetAlertDialog;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.CustomerTranscation;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.example.agc_linux.accounting.util.Connectivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

import static com.example.agc_linux.accounting.dialog.SweetAlertDialog.WARNING_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackupFragment extends BaseFragment implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "Google Drive Activity";
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT =122 ;

    private static final int REQUEST_CODE_OPENER = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    //variable for decide if i need to do a backup or a restore.
    //True stands for backup, False for restore
    private boolean bckORrst = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.backup_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initComponet();
        setHeader();
    }

    private void setHeader() {
        mActivity.setToolbar(StaticConfig.BACKUP_FRAGMENT);
    }

    private void initComponet() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setHeader();
        }
    }
    @OnClick({R.id.btn_backup,R.id.btn_import,R.id.btn_could_import,R.id.btn_clound_backup,R.id.btn_clear_database})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backup:
                String outFileName = Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;
                if (CommnMethod.is_marshmallow()) {
                    int permission = ContextCompat.checkSelfPermission(mActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    int permission2 = ContextCompat.checkSelfPermission(mActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);


                    if (permission != PackageManager.PERMISSION_GRANTED && permission2 != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    } else {
                        performBackup(outFileName);
                    }
                } else {
                    performBackup(outFileName);
                }
                break;
            case R.id.btn_import:
                performRestore();
                break;
            case R.id.btn_clound_backup:
                if(Connectivity.isConnectedMobile(mActivity) || Connectivity.isConnectedWifi(mActivity)){
                    bckORrst = true;
                    if (mGoogleApiClient != null)
                        mGoogleApiClient.disconnect();
                    mGoogleApiClient = gApiCLient(mGoogleApiClient);
                    mGoogleApiClient.connect();
                }else {
                    mActivity.showSnakbarMessage(getString(R.string.not_internate));
                }

                break;
            case R.id.btn_could_import:
                if(Connectivity.isConnectedMobile(mActivity) || Connectivity.isConnectedWifi(mActivity)){

                    bckORrst = false;
                    if (mGoogleApiClient != null)
                        mGoogleApiClient.disconnect();
                    mGoogleApiClient = gApiCLient(mGoogleApiClient);
                    mGoogleApiClient.connect();
                }else {
                    mActivity.showSnakbarMessage(getString(R.string.not_internate));
                }

                break;
            case R.id.btn_clear_database:
                openDilogDatabaseClearAlert();

                break;
            default:
                break;
        }
    }
    private void openDilogDatabaseClearAlert() {
        new SweetAlertDialog(mActivity, WARNING_TYPE)
                .setTitleText(getString(R.string.confirm_message_for_database_clear))
                .setConfirmText(getString(R.string.yes))
                .setCancelText(getString(R.string.no_))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                         againOpenConfimeDialog();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();
    }

    private void againOpenConfimeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(R.string.enter_password_one_time));
        builder.setCancelable(true);
        final EditText input = new EditText(mActivity);
        input.setHint(mActivity.getResources().getString(R.string.hint_enter_password));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(mActivity.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                if(Validation.isRequiredField(m_Text)){
                    if(m_Text.equalsIgnoreCase("bharat12345")){
                        Delete.tables(Customer.class, CustomerTranscation.class);
                        mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.database_clear));

                    }else {
                        mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.password_wrong_contact_addmin));
                    }
                }else {
                    mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.hint_enter_password));
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



    }


    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //check permissions.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
        //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    private void performBackup(final String outFileName) {


        //graidline cell link
        //http://bethecoder.com/applications/tutorials/excel/jexcel-api/how-to-set-excel-column-width.html




      //  verifyStoragePermissions(mActivity);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + StaticConfig.APP_NAME);

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.back_up_file_name);
            final EditText input = new EditText(mActivity);
            String file_name="backup_"+CommnMethod.getCurrentDate();
            input.setText(file_name);
            input.setSelection(file_name.length());
            input.setCursorVisible(false);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton(mActivity.getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    String out = outFileName + m_Text + ".db";
                    backup(out);
                }
            });
            builder.setNegativeButton(mActivity.getResources().getString(R.string.no_), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        } else
           mActivity.showSnakbarMessage(getString(R.string.unable_creat_dirrectory));
    }

    public void backup(String outFileName) {

        //database path
        final File inFileName = FlowManager.getContext().getDatabasePath(MyDatabase.NAME+".db");



        // final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        Log.e("test", "backup:==>> "+inFileName );

        try {

            FileInputStream fis = new FileInputStream(inFileName);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    //ask to the user what backup to restore
    private void performRestore() {


        verifyStoragePermissions(mActivity);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name));
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, android.R.layout.select_dialog_item);
            for (File file : files)
                if(file.getName().endsWith(".db")){
                    arrayAdapter.add(file.getName());
                }
            if(files!=null){
                if(files.length==0){
                  mActivity.showSnakbarMessage(getString(R.string.not_found_excel));
                    return;
                }
            }
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(mActivity);
            builderSingle.setTitle(R.string.restore_dataabse);
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
                                importDB(files[which].getPath());
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

    public void importDB(String inFileName) {

        // final String outFileName = mContext.getDatagbasePath(DATABASE_NAME).toString();
        final File file_path = FlowManager.getContext().getDatabasePath(MyDatabase.NAME+".db");

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);
            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(file_path);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void
    saveFileToDrive() {
        //database path on the device
        //final String inFileName = mActivity.getDatabasePath(DATABASE_NAME).toString();
      //final String inFileName = mActivity.getDatabasePath(DATABASE_NAME).toString();
      final File inFileName = FlowManager.getContext().getDatabasePath(MyDatabase.NAME+".db");


        Log.e("test","==>"+inFileName);

        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {

                   mActivity.showSnakbarMessage(getString(R.string.error_google_drive_time));
                    return;
                }
                try {
                  //  File dbFile = new File(inFileName);

                    FileInputStream fis = new FileInputStream(inFileName);
                    OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                    outputStream.flush();
                    outputStream.close();
                    fis.close();
                    //drive file metadata
                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setTitle(CommnMethod.getCurrentDate()+"_backup.db")
                            .setMimeType("application/db")
                            .build();

                    // Create an intent for the file chooser, and start it.
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(driveContentsResult.getDriveContents())
                            .build(mGoogleApiClient);

                  startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0,null);

                    Log.e("test","=============??????");

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("test","========================>>"+e.getMessage());
                }
            }
        });
    }



    private void importFromDrive(DriveFile dbFile) {

        final File inFileName = FlowManager.getContext().getDatabasePath(MyDatabase.NAME+".db");

        //database path on the device
      //  final String inFileName = mActivity.getDatabasePath(DATABASE_NAME).toString();
      //  final String inFileName = mContext.getApplicationContext().getDatabasePath(MyDatabase.NAME).toString();

        dbFile.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {

                if (!driveContentsResult.getStatus().isSuccess()) {

                    mActivity.showSnakbarMessage(getString(R.string.error_google_drive_time));
                    return;
                }
                // DriveContents object contains pointers to the actual byte stream
                DriveContents contents = driveContentsResult.getDriveContents();
                try {

                    ParcelFileDescriptor parcelFileDescriptor = contents.getParcelFileDescriptor();
                    FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());

                    // Open the empty db as the output stream
                    OutputStream output = new FileOutputStream(inFileName);

                    // Transfer bytes from the inputfile to the outputfile
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }

                    // Close the streams
                    output.flush();
                    output.close();
                    fileInputStream.close();

                   mActivity.showSnakbarMessage(getString(R.string.import_complet));

                } catch (Exception e) {
                    e.printStackTrace();
                    mActivity.showSnakbarMessage(getString(R.string.error_laoding));
                }
            }
        });
    }

    //Connect the Client
    private GoogleApiClient gApiCLient(GoogleApiClient mGoogleApiClient) {

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        return mGoogleApiClient;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(mActivity, result.getErrorCode(), 0).show();
            return;
        }

        try {
            result.startResolutionForResult(mActivity, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");

        //when the client is connected i have two possibility: backup (bckORrst -> true) or restore (bckORrst -> false)
        if (bckORrst){
            saveFileToDrive();
        }
        else {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{"application/db"})
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0,null);
                Log.i(TAG, "Open File Intent send");
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send Open File Intent", e);
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {

                   mActivity.showSnakbarMessage(getString(R.string.backup_successfully_save_in_google_drive));
                }
                break;

            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    //Toast.makeText(this, driveId.toString(), Toast.LENGTH_SHORT).show();
                    DriveFile file = driveId.asDriveFile();
                    importFromDrive(file);
                }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
           mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.try_again));
        }

    }
}
