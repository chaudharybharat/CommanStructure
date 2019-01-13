package com.example.agc_linux.accounting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.agc_linux.accounting.uicustome.ValueBar;
import com.example.agc_linux.accounting.uicustome.ValueSelector;

public class CutomeLayoutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cutome_layout);

        final ValueSelector valueSelector = (ValueSelector) findViewById(R.id.valueSelector);
        valueSelector.setMinValue(0);
        valueSelector.setMaxValue(100);

        final ValueBar valueBar = (ValueBar) findViewById(R.id.valueBar);
        valueBar.setMaxValue(100);
        valueBar.setAnimated(true);

        valueBar.setAnimationDuration(4000l);

        Button updateButton = (Button) findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int value = valueSelector.getValue();
                if(value==50){
                    valueBar.setVisibility(View.GONE);
                    return;
                }
                valueBar.setValue(value);

                //code to use Object Animation instead of the built-in ValueBar animation
                //if you use this, be sure the call valueBar.setAnimated(false);
                /*
                ObjectAnimator anim = ObjectAnimator.ofInt(valueBar, "value", valueBar.getValue(), value);
                anim.setDuration(1000);
                anim.start();
                */
            }
        });
    }
}
