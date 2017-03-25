package name.peterbukhal.android.fruit.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import name.peterbukhal.android.fruit.R;

import static name.peterbukhal.android.fruit.fragment.ChatFragment.FRAGMENT_TAG_CHAT;
import static name.peterbukhal.android.fruit.fragment.ChatFragment.newInstance;

public final class MainActivity extends AppCompatActivity {

    private Toolbar initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_fruit);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_main);

        initToolbar();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, newInstance(), FRAGMENT_TAG_CHAT)
                    .commit();
        }
    }

}