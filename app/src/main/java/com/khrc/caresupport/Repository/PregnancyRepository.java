package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.PregnancyDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.Pregnancy;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PregnancyRepository {

    private final PregnancyDao dao;

    public PregnancyRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.pregnancyDao();
    }

    public void create(Pregnancy... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Pregnancy data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public Pregnancy finds(String id) throws ExecutionException, InterruptedException {
        Callable<Pregnancy> callable = () -> dao.retrieves(id);

        Future<Pregnancy> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long count(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.count(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public List<Pregnancy> sync() throws ExecutionException, InterruptedException {
        Callable<List<Pregnancy>> callable = () -> dao.sync();

        Future<List<Pregnancy>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
