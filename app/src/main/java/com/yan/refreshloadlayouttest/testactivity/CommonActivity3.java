package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;

import com.yan.refreshloadlayouttest.R;

public class CommonActivity3 extends BaseActivity {
    private static final String TAG = "CommonActivity3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frg);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.cl,new ListViewFragment(), "ListView").commit();
    }
}
