package com.example.agc_linux.accounting.application;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by agc-linux on 14/8/17.
 */

public class App  extends Application{

   private static App getApp;

    @Override
    public void onCreate() {
        super.onCreate();
        getApp = this;
        Fresco.initialize(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // This instantiates DBFlow
        FlowManager.init(new FlowConfig.Builder(this).build());

    }

    public static App getGetApp() {
        return getApp;
    }
}
