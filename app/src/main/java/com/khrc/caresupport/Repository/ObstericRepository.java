package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.ObstericDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Obsteric;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ObstericRepository {

    private final ObstericDao dao;

    public ObstericRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.obstericDao();
    }

    public void create(Obsteric... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Obsteric data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public Obsteric finds(String id) throws ExecutionException, InterruptedException {
        Callable<Obsteric> callable = () -> dao.retrieves(id);

        Future<Obsteric> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Obsteric> sync(String id) throws ExecutionException, InterruptedException {
        Callable<List<Obsteric>> callable = () -> dao.sync();

        Future<List<Obsteric>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
