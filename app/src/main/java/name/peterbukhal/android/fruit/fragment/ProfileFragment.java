package name.peterbukhal.android.fruit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import name.peterbukhal.android.fruit.R;

/**
 * TODO Доработать документацию
 *
 * @author Peter Bukhal (peter.bukhal@gmail.com)
 */
public final class ProfileFragment extends Fragment {

    public static final String FRAGMENT_TAG_PROFILE = "fragment_tag_profile";

    public static Fragment newInstance() {
        final Bundle arguments = new Bundle();

        final Fragment fragment = new ProfileFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.m_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save: {
                final String nickname = mEtNickname.getText().toString();

                if (nickname.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.message1004, Toast.LENGTH_SHORT).show();

                    return false;
                }

                if (nickname.length() < 3 && nickname.length() > 80) {
                    Toast.makeText(getActivity(), R.string.message1005, Toast.LENGTH_SHORT).show();

                    return false;
                }

                getActivity()
                        .getSharedPreferences("profile", Context.MODE_PRIVATE)
                        .edit()
                        .putString("profile_nickname", nickname)
                        .apply();

                getActivity()
                        .getSupportFragmentManager()
                        .popBackStack();
            } break;
        }

        return super.onOptionsItemSelected(item);
    }

    private EditText mEtNickname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.f_profile, container, false);

        if (contentView != null) {
            mEtNickname = (EditText) contentView.findViewById(R.id.nickname);
        }

        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mEtNickname.setText(getActivity()
                .getSharedPreferences("profile", Context.MODE_PRIVATE)
                .getString("profile_nickname", "noname"));
    }
}
