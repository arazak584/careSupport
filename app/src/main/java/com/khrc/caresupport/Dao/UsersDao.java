package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.khrc.caresupport.entity.Users;

import java.util.List;

@Dao
public interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Users... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Users data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Users> users);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Users... momProfiles);

    @Query("DELETE FROM users")
    void deleteAll();

    @Update
    void update(Users data);

    @Delete
    void delete(Users data);

    @Query("SELECT * FROM users WHERE tel=:id")
    Users retrieves(String id);

    @Query("SELECT * FROM users WHERE tel=:id AND pin=:password AND ustatus=1")
    Users find(String id, String password);
}
