package com.khrc.caresupport.Repository;

import android.app.Application;


import com.khrc.caresupport.Dao.CodeBookDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.Utility.KeyValuePair;
import com.khrc.caresupport.entity.CodeBook;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CodeBookRepository {

    private final CodeBookDao dao;

    public CodeBookRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.codeBookDao();
    }

    public void create(CodeBook... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<CodeBook> findAll() throws ExecutionException, InterruptedException {

        Callable<List<CodeBook>> callable = () -> dao.retrieve();

        Future<List<CodeBook>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<KeyValuePair> findCodesOfFeature(String codeFeature) throws ExecutionException, InterruptedException {

        Callable<List<KeyValuePair>> callable = () -> dao.retrieveCodesOfFeature(codeFeature);

        Future<List<KeyValuePair>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public CodeBook finds() throws ExecutionException, InterruptedException {
        Callable<CodeBook> callable = () -> dao.retrieves();

        Future<CodeBook> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
