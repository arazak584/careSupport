package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.khrc.caresupport.entity.Pregnancy;

import java.util.List;

@Dao
public interface PregnancyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Pregnancy... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Pregnancy data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Pregnancy> pregnancies);

    @Query("DELETE FROM Pregnancy")
    void deleteAll();

    @Update
    void update(Pregnancy data);

    @Delete
    void delete(Pregnancy data);


    @Query("SELECT * FROM Pregnancy WHERE tel=:id AND outcome_date IS NULL ORDER BY FIRST_GA_DATE DESC ")
    Pregnancy retrieves(String id);

    @Query("SELECT count(*) FROM Pregnancy WHERE tel=:id")
    long count(String id);

    @Query("SELECT * FROM pregnancy where complete=1")
    List<Pregnancy> sync();
}
