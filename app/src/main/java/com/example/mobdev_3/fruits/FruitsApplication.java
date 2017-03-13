package com.example.mobdev_3.fruits;

import android.app.Application;
import android.content.Context;

import com.example.mobdev_3.fruits.service.fruits.FruitDownloader;
import com.example.mobdev_3.fruits.service.fruits.FruitsProvider;
import com.squareup.picasso.Picasso;

public final class FruitsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initPicasso(this);

        FruitsProvider.init(this);
    }

    private void initPicasso(final Context context) {
        final Picasso picasso = new Picasso.Builder(context)
                .downloader(new FruitDownloader(context))
                .indicatorsEnabled(BuildConfig.DEBUG)
                .loggingEnabled(BuildConfig.DEBUG)
                .build();

        Picasso.setSingletonInstance(picasso);
    }

}
