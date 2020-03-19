package com.normal.offline.networking;

import com.normal.offline.db.UserResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("users")
    Single<UserResponse> getUsers(@Query("page") int page);

}
