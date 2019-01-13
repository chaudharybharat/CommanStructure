package com.example.agc_linux.accounting.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.commonmodule.mi.utils.Validation;
import com.example.agc_linux.accounting.LoginActivity;
import com.example.agc_linux.accounting.MainActivity;
import com.example.agc_linux.accounting.R;
import com.example.agc_linux.accounting.StaticConfig;
import com.example.agc_linux.accounting.util.MyPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeetingFragment extends BaseFragment implements View.OnClickListener{

    @BindView(R.id.check_onetim_paaword)
    CheckBox check_onetime_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.seeting_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        setHeader();
        intiComponet();
    }

    private void intiComponet() {

       String is_value= MyPreferences.getPref(mActivity,StaticConfig.ONE_TIME_PASS_SETTING_PR);

        if(is_value.equalsIgnoreCase(StaticConfig.TRUE)){
            check_onetime_password.setChecked(true);
        }else {
            check_onetime_password.setChecked(false);
        }



        check_onetime_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean is_check) {

                if(is_check){
                    MyPreferences.setPref(mActivity,StaticConfig.ONE_TIME_PASS_SETTING_PR,StaticConfig.TRUE);
                }else {
                    MyPreferences.setPref(mActivity,StaticConfig.ONE_TIME_PASS_SETTING_PR,StaticConfig.FALSE);
                }
            }
        });
    }

    private void setHeader() {
        mActivity.setToolbar(StaticConfig.SEETING);
    }

    @OnClick({R.id.tv_change_onetime_password})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_change_onetime_password:
                setOpenTimePassword();
                break;
                default:
                    break;
        }
    }
    private void setOpenTimePassword() {
        final Dialog dialog=new Dialog(mActivity);
        dialog.setContentView(R.layout.open_time_password);
        dialog.setCancelable(false);
        final EditText editText_old=(EditText) dialog.findViewById(R.id.edt_oldpassword);
        TextView title=(TextView) dialog.findViewById(R.id.tv_title);
        title.setText(R.string.change_password);
        editText_old.setVisibility(View.VISIBLE);

        final EditText edt_paswword=(EditText) dialog.findViewById(R.id.edt_password);
        edt_paswword.setHint(R.string.old_pasworrd);
        final EditText  edt_paswword_confirm=(EditText) dialog.findViewById(R.id.edt_password_confirm);

        Button btn_go=(Button) dialog.findViewById(R.id.btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=edt_paswword.getText().toString().trim();
                String password_confirm=edt_paswword_confirm.getText().toString().trim();
                String old=editText_old.getText().toString().trim();
               String old_password= MyPreferences.getPref(mActivity,StaticConfig.ONE_TIME_PASS);
                if(!Validation.isRequiredField(old)){
                    Toast.makeText(mActivity,R.string.old_password_empty,Toast.LENGTH_LONG).show();
                    return;
                }else {
                    if(!old.equalsIgnoreCase(old_password)){
                        Toast.makeText(mActivity,R.string.old_password_not_match,Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if(!Validation.isRequiredField(password)){
                    Toast.makeText(mActivity,R.string.password_empty,Toast.LENGTH_LONG).show();
                    return;
                }else {
                    if(password.length()<4){
                        Toast.makeText(mActivity, R.string.minimum_four_digit,Toast.LENGTH_LONG).show();

                    }
                }
                if(!Validation.isRequiredField(password_confirm)){
                    Toast.makeText(mActivity,R.string.enter_pass_confirm,Toast.LENGTH_LONG).show();
                    return;
                }
                if(!password.matches(password_confirm)){
                    Toast.makeText(mActivity,R.string.not_match,Toast.LENGTH_LONG).show();
                    return;
                }
                MyPreferences.setPref(mActivity,StaticConfig.ONE_TIME_PASS,password);
               mActivity.showSnakbarMessage(getString(R.string.one_time_password_change));
                dialog.dismiss();

            }
        });
        // Toast.makeText(mActivity,"Please enter one time password",Toast.LENGTH_LONG).show();
        dialog.show();
    }
}
