package com.khrc.caresupport.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.khrc.caresupport.Utility.KeyValuePair;
import com.khrc.caresupport.entity.CodeBook;

import java.util.List;


@Dao
public interface CodeBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(CodeBook... data);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<CodeBook> codeBooks);

    @Query("SELECT * FROM codebook")
    List<CodeBook> retrieve();

    @Query("DELETE FROM codebook")
    void deleteAll();

    @Query("SELECT codeValue,codeLabel FROM CodeBook WHERE codeFeature=:codeFeature")
    List<KeyValuePair> retrieveCodesOfFeature(String codeFeature);

    @Query("SELECT * FROM codebook WHERE id=1 ")
    CodeBook retrieves();

}
