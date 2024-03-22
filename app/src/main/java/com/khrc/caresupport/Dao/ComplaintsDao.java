package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.khrc.caresupport.entity.Complaints;

import java.util.List;

@Dao
public interface ComplaintsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Complaints... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(Complaints data);

    @Query("DELETE FROM Complaints")
    void deleteAll();

    @Update
    void update(Complaints data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Complaints> complaints);

    @Delete
    void delete(Complaints data);



    @Query("SELECT * FROM Complaints WHERE response_txt IS NULL OR response_txt= '' AND " +
            " (hfac LIKE:id OR mothn LIKE:id OR tel LIKE:id) ORDER BY complaints_date")
    List<Complaints> search(String id);

    @Query("SELECT * FROM Complaints WHERE response_txt IS NOT NULL AND response_txt != '' ORDER BY complaints_date")
    List<Complaints> notdone();

    @Query("SELECT * FROM Complaints WHERE response_txt IS NOT NULL AND response_txt != '' AND " +
            " (hfac LIKE:id OR mothn LIKE:id OR tel LIKE:id) ORDER BY complaints_date")
    List<Complaints> searchs(String id);

    @Query("SELECT a.id,a.tel,a.record_id,b.response_date as response_date,a.complaints_date,b.response_text as response_txt,complts " +
            " FROM Complaints as a LEFT JOIN chat as b ON a.tel=b.tel WHERE a.tel=:id ORDER BY a.complaints_date,b.response_date")
    List<Complaints> repo(String id);

    @Query("SELECT * FROM Complaints WHERE response_txt IS NULL OR response_txt= '' ORDER BY complaints_date")
    List<Complaints> not();

    @Query("SELECT * FROM Complaints WHERE complete=1 AND response_txt IS NOT NULL AND response_txt != ''")
    List<Complaints> sync();

    @Query("SELECT * FROM Complaints WHERE id=:id ")
    Complaints retrieves(String id);

}
