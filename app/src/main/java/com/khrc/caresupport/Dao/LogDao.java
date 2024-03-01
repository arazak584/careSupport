package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.khrc.caresupport.entity.LogBook;

import java.util.List;

@Dao
public interface LogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(LogBook... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(LogBook data);

    @Query("DELETE FROM logbook")
    void deleteAll();

    @Update
    void update(LogBook data);

    @Delete
    void delete(LogBook data);

    @Query("SELECT * FROM LogBook WHERE tel=:id")
    List<LogBook> search(String id);

    @Query("SELECT * FROM LogBook")
    List<LogBook> sync();
}
