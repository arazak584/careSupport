package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.khrc.caresupport.entity.MomProfile;

import java.util.List;

@Dao
public interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(MomProfile... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(MomProfile data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MomProfile> momProfiles);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MomProfile... momProfiles);

    @Query("DELETE FROM profile")
    void deleteAll();

    @Update
    void update(MomProfile data);

    @Delete
    void delete(MomProfile data);

    @Query("SELECT * FROM profile where complete=1")
    List<MomProfile> sync();

    @Query("SELECT * FROM profile WHERE tel=:id")
    MomProfile retrieves(String id);

    @Query("SELECT * FROM profile WHERE tel=:id AND pin=:password")
    MomProfile find(String id, String password);

    @Query("SELECT COUNT(DISTINCT tel) FROM profile ")
    long count();
}
