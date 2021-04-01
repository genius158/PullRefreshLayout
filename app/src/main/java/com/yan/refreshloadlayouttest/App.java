package com.yan.refreshloadlayouttest;

import android.app.Application;
import android.content.Context;

//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;


/**
 * Created by yan on 2017/8/10.
 */

public class App extends Application {
    private static Context appContext;

    //Application为整个应用保存全局的RefWatcher
//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        refWatcher = LeakCanary.install(this);
    }

//    public static RefWatcher getRefWatcher(Context context) {
//        App application = (App) context.getApplicationContext();
//        return application.refWatcher;
//    }

    public static Context getAppContext() {
        return appContext;
    }
}
