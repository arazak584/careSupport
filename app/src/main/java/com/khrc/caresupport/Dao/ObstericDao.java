package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.khrc.caresupport.entity.MedHistory;
import com.khrc.caresupport.entity.Obsteric;

import java.util.List;

@Dao
public interface ObstericDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Obsteric... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Obsteric data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Obsteric> obsterics);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Obsteric... obsterics);

    @Query("DELETE FROM obs")
    void deleteAll();

    @Update
    void update(Obsteric data);

    @Delete
    void delete(Obsteric data);

    @Query("SELECT * FROM obs where complete=1")
    List<Obsteric> sync();

    @Query("SELECT * FROM obs WHERE tel=:id")
    Obsteric retrieves(String id);
}
