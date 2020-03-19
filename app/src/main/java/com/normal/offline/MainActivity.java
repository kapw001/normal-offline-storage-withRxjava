package com.normal.offline;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.normal.offline.adapter.Adapter;
import com.normal.offline.db.User;
import com.normal.offline.db.UserDao;
import com.normal.offline.db.UserResponse;
import com.normal.offline.db.UserRoomDatabase;
import com.normal.offline.networking.ApiService;
import com.normal.offline.networking.UserService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Adapter mAdapter;
    private UserDao userDao;
    private ApiService apiService;

    private UserService userService;

    private CompositeDisposable disposable = new CompositeDisposable();

    private PublishProcessor<Integer> paginator = PublishProcessor.create();
    private boolean loading = false;
    private int pageNumber = 1;
    private final int VISIBLE_THRESHOLD = 1;
    private int lastVisibleItem, totalItemCount;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        layoutManager = new LinearLayoutManager(this);

        apiService = new Retrofit.Builder().baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(ApiService.class);

        userDao = UserRoomDatabase.getDatabase(this).userDao();

//        AsyncTask.execute(() -> userDao.deleteAll());

        userService = new UserService(apiService, userDao);

        mAdapter = new Adapter(new ArrayList<User>());

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mAdapter);

        getLocalDbUser();

        setUpLoadMoreListener();
        subscribeForData();

    }


    /**
     * setting listener to get callback for load more
     */
    private void setUpLoadMoreListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                totalItemCount = layoutManager.getItemCount();
//                lastVisibleItem = layoutManager
//                        .findLastVisibleItemPosition();

                if (!loading
                        && pageNumber < totalItemCount) {
                    pageNumber++;
                    paginator.onNext(pageNumber);
                    loading = true;

                    Log.e(TAG, "onScrolled: " + pageNumber + " " + totalItemCount + " " + (pageNumber < totalItemCount));
                }

//                if (!loading
//                        && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
//                    pageNumber++;
//                    paginator.onNext(pageNumber);
//                    loading = true;
//                }
            }
        });
    }

    /**
     * subscribing for data
     */
    private void subscribeForData() {

        disposable.add(paginator
                .onBackpressureDrop()
                .doOnNext(page -> loading = true)
                .concatMapSingle(page -> userService.syncUser(page)
                        .subscribeOn(Schedulers.io())
                        .doOnError(throwable -> {
                            // handle error
                            loading = false;
                            Log.e(TAG, "subscribeForData: " + throwable.getMessage());
                        })
                        // continue emission in case of error also
                        .onErrorReturn(throwable -> new UserResponse()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {

                    if (items != null && items.getTotalPages() != null) {
//                    mAdapter.addItem(items.getUser());
                        totalItemCount = items.getTotalPages();
                        Log.e(TAG, "subscribeForData: " + totalItemCount + " " + pageNumber + " " + items.getUser().size());

                    }
                    loading = false;
                }));

        paginator.onNext(pageNumber);

    }


    private void getLocalDbUser() {

        disposable.add(userService.getUsers().subscribeWith(new DisposableSubscriber<List<User>>() {
            @Override
            public void onNext(List<User> users) {

                mAdapter.addItem(users);

            }

            @Override
            public void onError(Throwable t) {

                Log.e(TAG, "onError: " + t.getMessage());
            }

            @Override
            public void onComplete() {

            }
        }));

    }


}
