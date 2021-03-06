package name.peterbukhal.android.fruit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import name.peterbukhal.android.fruit.R;
import name.peterbukhal.android.fruit.service.fruits.FruitsProvider;
import name.peterbukhal.android.fruit.service.fruits.model.Fruit;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.widget.Toast.makeText;

/**
 * @author Peter Bukhal petr.bukhal <at> doconcall.ru
 */
public final class FruitFragment extends Fragment {

    public static final String FRAGMENT_TAG_FRUIT = "fragment_tag_fruit";
    public static final String ARG_FRUIT = "arg_fruit";

    public static Fragment newInstance(Fruit fruit) {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARG_FRUIT, fruit);

        final Fragment fragment = new FruitFragment();
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

        inflater.inflate(R.menu.menu_fruit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_fruit: {
                removeFruit();
            } break;
        }

        return super.onOptionsItemSelected(item);
    }

    private DisposableObserver<Response<Void>> createRemoveFruitObserver() {
        return new DisposableObserver<Response<Void>>() {

            @Override
            public void onNext(Response<Void> response) {
                if (response.isSuccessful()) {
                    getFragmentManager()
                            .popBackStack();

                    makeText(getActivity(), R.string.message1002, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        makeText(getActivity(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                makeText(getActivity(), R.string.message1001, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {}

        };
    }

    private ImageView ivFruitImage;
    private TextView tvFruitName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.l_fruit, container, false);

        if (view != null) {
            ivFruitImage = (ImageView) view.findViewById(R.id.fruitImage);
            tvFruitName = (TextView) view.findViewById(R.id.fruitName);
        }

        return view;
    }

    private Fruit fruit;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ARG_FRUIT, fruit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().containsKey(ARG_FRUIT)) {
            fruit = getArguments().getParcelable(ARG_FRUIT);
        } else if (savedInstanceState != null) {
            fruit = savedInstanceState.getParcelable(ARG_FRUIT);
        } else {
            fruit = new Fruit(0, "");
        }

        tvFruitName.setText(fruit.getName());

        Picasso
                .with(getActivity())
                .load(getString(R.string.fruit_service) + "asset/picture/fruit/" + fruit.getId())
                .resize(64, 64)
                .centerCrop()
                .into(ivFruitImage);
    }

    private void removeFruit() {
        FruitsProvider.provide()
                .removeFruit(fruit.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createRemoveFruitObserver());
    }

}
