package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.entity.DailyCondition;

import java.util.List;

@Dao
public interface DailyConditionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(DailyCondition... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(DailyCondition data);

    @Query("DELETE FROM daily")
    void deleteAll();

    @Update
    void update(DailyCondition data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<DailyCondition> dailyConditions);

    @Delete
    void delete(DailyCondition data);


    @Query("SELECT * FROM daily WHERE tel=:id AND date(complaints_date/1000,'unixepoch')=:todayDate")
    DailyCondition retrieves(String id,String todayDate);

    @Query("SELECT * FROM daily WHERE tel=:id ")
    List<DailyCondition> search(String id);

    @Query("SELECT * FROM daily WHERE complete=1")
    List<DailyCondition> sync();

    @Query("SELECT * FROM daily WHERE " +
            " (complts LIKE:id OR complaints_date LIKE:id ) AND tel=:ids ")
    List<DailyCondition> searchs(String id, String ids);

    @Query("SELECT * FROM daily WHERE tel=:id ORDER BY complaints_date")
    List<DailyCondition> repo(String id);
}
