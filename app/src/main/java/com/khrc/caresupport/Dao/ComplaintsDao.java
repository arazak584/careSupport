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

    @Query("SELECT * FROM Complaints WHERE " +
            " (complts LIKE:id OR complaints_date LIKE:id ) AND tel=:ids ")
    List<Complaints> searchs(String id,String ids);

    @Query("SELECT * FROM Complaints WHERE tel=:id ORDER BY complaints_date")
    List<Complaints> repo(String id);

    @Query("SELECT * FROM Complaints GROUP BY tel ORDER BY complaints_date DESC")
    List<Complaints> not();

    @Query("SELECT * FROM Complaints WHERE complete=1 AND response_txt IS NOT NULL AND response_txt != ''")
    List<Complaints> sync();

    @Query("SELECT * FROM Complaints WHERE id=:id ")
    Complaints retrieves(String id);

//    @Query("SELECT COUNT(DISTINCT c.tel) FROM (SELECT a.tel FROM Complaints AS a INNER JOIN chat AS b ON a.tel = b.tel " +
//            "GROUP BY a.tel HAVING MAX(a.complaints_date) > MAX(b.response_date)) AS c")
//    long reply();

    @Query("SELECT COUNT(DISTINCT c.tel) FROM (SELECT a.tel FROM Complaints AS a LEFT JOIN (SELECT tel, MAX(response_date) AS max_response_date FROM chat GROUP BY tel) AS b ON a.tel = b.tel WHERE a.tel=:id AND a.complaints_date > COALESCE(b.max_response_date, '1970-01-01')) AS c")
    long reply(String id);


    @Query("SELECT COUNT(DISTINCT c.tel) FROM (SELECT a.tel FROM chat AS a Inner JOIN (SELECT tel, MAX(complaints_date) AS max_comp_date FROM complaints GROUP BY tel) AS b ON a.tel = b.tel WHERE a.tel=:id AND a.response_date > b.max_comp_date) AS c")
    long replys(String id);


}
