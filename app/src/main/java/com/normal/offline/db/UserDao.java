package com.normal.offline.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.v7.widget.LinearLayoutManager;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User word);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<User> users);

    @Query("DELETE FROM user")
    void deleteAll();

    @Query("SELECT * from user ORDER BY firstName ASC")
    Flowable<List<User>> getAllLocalUsers();

}
