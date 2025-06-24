package com.example.music_zengmeilian;

import android.app.Application;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AutoSize.initCompatMultiProcess(this);

        AutoSizeConfig.getInstance()
                .setCustomFragment(true)
                .setDesignWidthInDp(392)
                .setDesignHeightInDp(871)
                .setBaseOnWidth(true)
                .getUnitsManager()
                .setSupportDP(true)
                .setSupportSP(true)
                .setSupportSubunits(Subunits.MM);
    }
}