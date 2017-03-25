package name.peterbukhal.android.fruit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import name.peterbukhal.android.fruit.R;

import static name.peterbukhal.android.fruit.fragment.ChatFragment.FRAGMENT_TAG_CHAT;
import static name.peterbukhal.android.fruit.fragment.ChatFragment.newInstance;

public final class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.a_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content, newInstance(), FRAGMENT_TAG_CHAT)
                    .commit();
        }
    }

}