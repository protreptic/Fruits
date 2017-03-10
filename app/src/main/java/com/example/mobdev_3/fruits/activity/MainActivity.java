package com.example.mobdev_3.fruits.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.mobdev_3.fruits.R;
import com.example.mobdev_3.fruits.fragment.FruitsFragment;
import com.example.mobdev_3.fruits.service.fruits.FruitsProvider;
import com.example.mobdev_3.fruits.service.fruits.model.Fruit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static com.example.mobdev_3.fruits.fragment.FruitsFragment.FRAGMENT_TAG_FRUITS;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main, FruitsFragment.newInstance(), FRAGMENT_TAG_FRUITS)
                    .commit();
        }
    }
}
