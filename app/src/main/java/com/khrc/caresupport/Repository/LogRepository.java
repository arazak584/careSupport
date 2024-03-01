package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.LogDao;
import com.khrc.caresupport.entity.LogBook;
import com.khrc.caresupport.Utility.AppDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LogRepository {

    private final LogDao dao;

    public LogRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.logDao();
    }

    public void create(LogBook... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(LogBook data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<LogBook> search(String id) throws ExecutionException, InterruptedException {
        Callable<List<LogBook>> callable = () -> dao.search(id);

        Future<List<LogBook>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<LogBook> sync() throws ExecutionException, InterruptedException {
        Callable<List<LogBook>> callable = () -> dao.sync();

        Future<List<LogBook>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
