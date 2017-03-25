package name.peterbukhal.android.fruit.service.fruits;

import android.content.Context;

import name.peterbukhal.android.fruit.R;
import name.peterbukhal.android.fruit.service.fruits.interceptor.GzipInterceptor;
import name.peterbukhal.android.fruit.service.fruits.interceptor.RequestInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class FruitsProvider {

    private static Fruits sFruits;

    public static void init(Context context) {
        sFruits = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.fruit_service))
                .client(new OkHttpClient.Builder()
                        //.authenticator(new FruitAuthenticator())
                        .addInterceptor(new RequestInterceptor(1))
                        .addInterceptor(new GzipInterceptor())
                        .addInterceptor(new HttpLoggingInterceptor()
                                .setLevel(Level.BODY))
                                .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Fruits.class);
    }

    public static Fruits provide() {
        if (sFruits == null) {
            throw new RuntimeException("Fruits not initialized!");
        }

        return sFruits;
    }

}
