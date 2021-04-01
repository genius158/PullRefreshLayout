package com.yan.refreshloadlayouttest.testactivity;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by yan on 2017/9/20.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        App.getRefWatcher(getApplicationContext()).watch(this);
    }
}
