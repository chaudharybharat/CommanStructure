package com.example.agc_linux.accounting.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.commonmodule.mi.Activity.MIActivity;
import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.model.Customer;
import com.example.agc_linux.accounting.model.UploadImage;
import com.example.agc_linux.accounting.util.CommnMethod;
import com.example.agc_linux.accounting.util.ImageUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCustomerFragment extends BaseFragment implements View.OnClickListener{


    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT =122 ;
    private static final int CONTACT_READ =111 ;
    @BindView(R.id.iv_profile)
    SimpleDraweeView iv_profile;
    @BindView(R.id.edt_name)
    EditText edt_name;
    @BindView(R.id.edt_mobile)
    EditText edt_mobile;
    @BindView(R.id.edt_address)
    EditText edt_address;
    @BindView(R.id.btn_profile)
    TextView btn_profile;
    String custome_profile_bitmap_path ="";
    private static final int PICK_IMAGE = 1994;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addcustomer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        initComponset();
        setHeader();
    }

    private void setHeader() {
        mActivity.setToolbar(StaticConfig.ADD_CUSTOMER);
    }

    private void initComponset() {
        edt_mobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (edt_mobile.getRight() - edt_mobile.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if(CommnMethod.is_marshmallow()){
                            readContactNumber();
                        }else {
                            opencontactActivity();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @OnClick({R.id.btn_profile,R.id.iv_profile,R.id.btn_clear,R.id.btn_save})
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_profile:
            case R.id.iv_profile:
                 if(CommnMethod.is_marshmallow()){

                     int read = ContextCompat.checkSelfPermission(mActivity,
                             Manifest.permission.READ_EXTERNAL_STORAGE);
                     int write = ContextCompat.checkSelfPermission(mActivity,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE);

                     if (read != PackageManager.PERMISSION_GRANTED &&write != PackageManager.PERMISSION_GRANTED) {

                         ActivityCompat.requestPermissions(mActivity,
                                 new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                                 EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                     }else {
                        // fetchUserData();
                         openDialogPicture();
                     }
                 }else {
                     openDialogPicture();
                 }

                break;
            case R.id.btn_clear:
                clearTextData();
                break;
            case R.id.btn_save:
                UploadCustomeDetail();
                break;
            /*case R.id.iv_contact:
              readContactNumber();
                break;*/

        }

    }

    private void readContactNumber() {
        if (CommnMethod.is_marshmallow()) {
            String[] permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
            if (CommnMethod.isPermissionNotGranted(mActivity, permissions)) {
                requestPermissions(permissions, StaticConfig.RESULT_CODE);
                return;
            }else {
                opencontactActivity();
            }
        }else {
            opencontactActivity();

        }
    }

    private void opencontactActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_READ);
    }

    private void fetchUserData() {
        new AsyncTask<Void, Void, UploadImage>() {
            @Override
            protected UploadImage doInBackground(Void... voids) {

                UploadImage uploadImage=new UploadImage();

                int count=0;
                Cursor cursor = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns.ORIENTATION}, MediaStore.Images.Media.DATE_ADDED, null, "date_added ASC");
                if(cursor != null && cursor.moveToFirst())
                {
                    do {
                        Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                        Bitmap imgBitmap = BitmapFactory.decodeFile(String.valueOf(uri));

                        String path= ImageUtils.encodeBase64(imgBitmap);
                       if(count==5){
                          Log.e("test","==>count finish");
                           cursor.moveToLast();
                       }
                       count=count+1;
                        uploadImage.setImage(path);

                        String photoPath = uri.toString();
                        Log.e("test","==>>>"+photoPath);

                    }while(cursor.moveToNext());


                    cursor.close();
                }else {
                    Log.e("test","==>>>nullllllll");
                }
                return uploadImage;
            }

            @Override
            protected void onPostExecute(UploadImage uploadImage) {
                super.onPostExecute(uploadImage);
                FirebaseDatabase.getInstance().getReference().child("Upload/").push().setValue(uploadImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      //  Toast.makeText(mActivity,"Success fully upload detail",Toast.LENGTH_SHORT).show();
                        mActivity.showSnakbarMessage(getString(R.string.customer_add_success_full));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mActivity.showSnakbarMessage("Failure please try again");
                                Toast.makeText(mActivity,"Failure ",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }.execute();
    }

    private void UploadCustomeDetail() {
        String name=edt_name.getText().toString().trim();
        String address=edt_address.getText().toString().trim();
        String mobile=edt_mobile.getText().toString().trim();
        if(name.isEmpty()){
            edt_name.setError(getString(R.string.customer_required));
            edt_name.requestFocus();
        }else {
               name=CommnMethod.firstlaterCapse(name);
            Customer customer_existe = Customer.getCustomerNameExists(name);
            if (customer_existe == null) {


                Customer customer = new Customer();
                customer.setName(name);
                if (Validation.isRequiredField(address)) {
                    customer.setAddress(address);
                }
                if (Validation.isRequiredField(mobile)) {
                    if (mobile.length() < 10 || mobile.length() > 15) {
                        edt_mobile.setError(getString(R.string.mobie_number_not_current));
                        edt_mobile.requestFocus();
                        return;
                    }
                    customer.setMobile(mobile);
                }
                if (Validation.isRequiredField(custome_profile_bitmap_path)) {
                    customer.setProfile_pic(custome_profile_bitmap_path);
                }
                boolean is_save = customer.save();
                if (is_save) {
                    mActivity.pushFragmentDontIgnoreCurrent(new CustomerListFragment(), MIActivity.FRAGMENT_JUST_REPLACE);
                    mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.success_message_customer_add));
                } else {
                    mActivity.showSnakbarMessage(mActivity.getResources().getString(R.string.failure_try_again));
                }

            }else {
                mActivity.showSnakbarMessage(getString(R.string.customer_name_existe_message));

            }
        }
    }



    private void clearTextData() {
        edt_address.setText("");
        edt_mobile.setText("");
        edt_name.setText("");

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
                InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);
                if(imgBitmap!=null){
                    Uri uri=CommnMethod.getImageUri(mActivity,imgBitmap);
                    uploadImageStoreInside(uri);
                    iv_profile.setImageURI(uri);
                }
                custome_profile_bitmap_path = ImageUtils.encodeBase64(liteImage);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(requestCode==CONTACT_READ){
        if (resultCode == Activity.RESULT_OK) {
                  /*  Uri contactData = imageReturnedIntent.getData();
                    Cursor cursor = managedQuery(contactData, null, null, null, null);
                    cursor.moveToFirst();

                    String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //contactName.setText(name);
//                    contactNumber.setText(number);
                    //contactEmail.setText(email);
                    edt_member.setText(number);*/

            Uri contactData = data.getData();

            Cursor c = mActivity.managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {


                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = mActivity.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    String cNumber = phones.getString(phones.getColumnIndex("data1"));
//                            System.out.println("number is:"+cNumber);
                    edt_mobile.setText(""+cNumber);

                }
//                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


            }
        }

        }
    }

    private void uploadImageStoreInside(Uri uri) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://accounting-10a6e.appspot.com");
        StorageReference stoe = storageRef.child("image.png");
       UploadTask uploadTask = stoe.putFile(uri);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]
            permissions, int[] grantResults) {
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
        {
          openDialogPicture();
        }
        if (requestCode == StaticConfig.RESULT_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            opencontactActivity();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            setHeader();
        }
    }
}
