package name.peterbukhal.android.fruit;

import android.app.Application;
import android.content.Context;

import name.peterbukhal.android.fruit.service.fruits.FruitDownloader;
import name.peterbukhal.android.fruit.service.fruits.FruitsProvider;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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
