package com.example.mobdev_3.fruits.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.mobdev_3.fruits.R;

import static com.example.mobdev_3.fruits.fragment.ChatFragment.FRAGMENT_TAG_CHAT;
import static com.example.mobdev_3.fruits.fragment.ChatFragment.newInstance;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main, newInstance(), FRAGMENT_TAG_CHAT)
                    .commit();
        }
    }

}