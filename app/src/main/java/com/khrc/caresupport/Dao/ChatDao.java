package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;

import java.util.List;

@Dao
public interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(ChatResponse... data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(ChatResponse data);

    @Query("DELETE FROM chat")
    void deleteAll();

    @Update
    void update(ChatResponse data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ChatResponse> complaints);

    @Delete
    void delete(ChatResponse data);


    @Query("SELECT * FROM chat WHERE complete=1")
    List<ChatResponse> sync();

    @Query("SELECT * FROM chat WHERE id=:id ")
    ChatResponse retrieves(String id);

    @Query("SELECT * FROM chat WHERE tel=:id ORDER BY response_date")
    List<ChatResponse> repo(String id);

    @Query("SELECT * FROM chat WHERE " +
            " (response_text LIKE:id OR response_date LIKE:id ) AND tel=:ids ")
    List<ChatResponse> searchs(String id,String ids);
}
