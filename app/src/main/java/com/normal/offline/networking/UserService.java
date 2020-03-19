package com.normal.offline.networking;

import android.os.AsyncTask;

import com.normal.offline.db.User;
import com.normal.offline.db.UserDao;
import com.normal.offline.db.UserResponse;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserService {

    private ApiService apiService;
    private UserDao userDao;

    public UserService(ApiService apiService, UserDao userDao) {
        this.apiService = apiService;
        this.userDao = userDao;
    }


    public Single<UserResponse> syncUser(int page) {
        return apiService.getUsers(page)
                .map(userResponse -> {

                    List<User> userList = userResponse.getUser();

                    if (userList != null) {

                        AsyncTask.execute(() -> {

                            userDao.insert(userList);

//                            for (int i = 0; i < userList.size(); i++) {
//
//                                userDao.insert(userList.get(i));
//
//                            }


                        });

                    }

                    return userResponse;

                });
    }


    /**
     * Get issues from realm
     *
     * @return
     */
    public Flowable<List<User>> getUsers() {

        return userDao.getAllLocalUsers().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
