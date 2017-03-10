package com.example.mobdev_3.fruits;

import android.app.Application;

import com.example.mobdev_3.fruits.service.fruits.FruitsProvider;

public final class FruitsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FruitsProvider.init(this);
    }

}
