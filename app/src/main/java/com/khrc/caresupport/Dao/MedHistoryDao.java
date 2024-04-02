package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.khrc.caresupport.entity.DailyCondition;
import com.khrc.caresupport.entity.MedHistory;

import java.util.List;

@Dao
public interface MedHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(MedHistory... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(MedHistory data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MedHistory> medHistories);

    @Query("DELETE FROM medhistory")
    void deleteAll();

    @Update
    void update(MedHistory data);

    @Delete
    void delete(MedHistory data);

    @Query("SELECT * FROM medhistory where complete=1")
    List<MedHistory> sync();

    @Query("SELECT * FROM medhistory WHERE tel=:id")
    MedHistory retrieves(String id);
}
