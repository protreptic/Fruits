package name.peterbukhal.android.fruit.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import name.peterbukhal.android.fruit.R;
import name.peterbukhal.android.fruit.service.fruits.FruitsProvider;
import name.peterbukhal.android.fruit.service.fruits.model.Fruit;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.widget.Toast.makeText;
import static name.peterbukhal.android.fruit.fragment.FruitFragment.FRAGMENT_TAG_FRUIT;

/**
 * @author Peter Bukhal petr.bukhal <at> doconcall.ru
 */
public final class FruitsFragment extends ListFragment {

    public static final String FRAGMENT_TAG_FRUITS = "fragment_tag_fruits";

    public static Fragment newInstance() {
        final Bundle arguments = new Bundle();

        final Fragment fragment = new FruitsFragment();
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

        inflater.inflate(R.menu.menu_fruits, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_fruit: {
                addFruit();
            } break;
        }

        return super.onOptionsItemSelected(item);
    }

    private final CompositeDisposable disposables = new CompositeDisposable();

    private DisposableObserver<Response<List<Fruit>>> createGetAllFruitsObserver() {
        return new DisposableObserver<Response<List<Fruit>>>() {

            @Override
            public void onNext(Response<List<Fruit>> response) {
                if (response.isSuccessful()) {
                    fruits.clear();
                    fruits.addAll(response.body());
                } else {
                    makeText(getActivity(), response.errorBody().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                makeText(getActivity(), R.string.message1001, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                fruitAdapter.notifyDataSetChanged();
            }

        };
    }

    private DisposableObserver<Response<Fruit>> createAddFruitObserver() {
        return new DisposableObserver<Response<Fruit>>() {

            @Override
            public void onNext(Response<Fruit> response) {
                if (response.isSuccessful()) {
                    updateFruits();
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
            public void onComplete() {
                fruitAdapter.notifyDataSetChanged();
            }

        };
    }

    private final List<Fruit> fruits = new ArrayList<>();
    private final FruitAdapter fruitAdapter = new FruitAdapter();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setDivider(null);

        if (savedInstanceState == null) {
            setListAdapter(fruitAdapter);

            updateFruits();
        }
    }

    private void updateFruits() {
        FruitsProvider.provide()
                .getAllFruits()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createGetAllFruitsObserver());
    }

    public void addFruit() {
        FruitsProvider.provide()
                .addFruit(10, new Fruit(10, "Мандарин"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createAddFruitObserver());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        disposables.dispose();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        final Fruit fruit = (Fruit) listView.getItemAtPosition(position);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main, FruitFragment.newInstance(fruit), FRAGMENT_TAG_FRUIT)
                .addToBackStack(FRAGMENT_TAG_FRUIT)
                .commit();
    }

    private class FruitAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fruits.size();
        }

        @Override
        public Fruit getItem(int position) {
            return fruits.get(position);
        }

        @Override
        public long getItemId(int position) {
            return fruits.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_fruit, parent, false);

                final FruitViewHolder newHolder = new FruitViewHolder();
                newHolder.ivFruitImage = (ImageView) convertView.findViewById(R.id.fruitImage);
                newHolder.tvFruitName = (TextView) convertView.findViewById(R.id.fruitName);

                convertView.setTag(newHolder);
            }

            final FruitViewHolder holder = (FruitViewHolder) convertView.getTag();
            final Fruit fruit = getItem(position);

            holder.tvFruitName.setText(fruit.getName());

            Picasso
                    .with(getActivity())
                    .load(getString(R.string.fruit_service) + "asset/picture/fruit/" + fruit.getId())
                    .resize(64, 64)
                    .centerCrop()
                    .into(holder.ivFruitImage);

            return convertView;
        }

        class FruitViewHolder {

            ImageView ivFruitImage;
            TextView tvFruitName;

        }

    }

}
