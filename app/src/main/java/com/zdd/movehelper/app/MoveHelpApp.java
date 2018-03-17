package com.zdd.movehelper.app;

import android.app.Application;

import com.zdd.movehelper.util.AssetsLoad;

/**
 * Project: MoveHelper
 * Created by Zdd on 2018/3/17.
 */

public class MoveHelpApp extends Application {

    private static MoveHelpApp app;

    private MoveHelpApp() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
