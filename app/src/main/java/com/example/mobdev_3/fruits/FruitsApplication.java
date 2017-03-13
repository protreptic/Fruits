package com.example.mobdev_3.fruits;

import android.app.Application;
import android.content.Context;

import com.example.mobdev_3.fruits.service.fruits.FruitDownloader;
import com.example.mobdev_3.fruits.service.fruits.FruitsProvider;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class FruitsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initPicasso(this);
        initRealm(this);

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

    private void initRealm(final Context context) {
        Realm.init(context);

        try {
            Realm.setDefaultConfiguration(
                    new RealmConfiguration.Builder()
                            .build());
            Realm.getDefaultInstance();
        } catch (Exception e) {
            //
        }
    }

}
