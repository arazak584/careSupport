package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.MedHistoryDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.MedHistory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MedHistoryRepository {

    private final MedHistoryDao dao;

    public MedHistoryRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.medHistoryDao();
    }

    public void create(MedHistory... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(MedHistory data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public MedHistory finds(String id) throws ExecutionException, InterruptedException {
        Callable<MedHistory> callable = () -> dao.retrieves(id);

        Future<MedHistory> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<MedHistory> sync() throws ExecutionException, InterruptedException {
        Callable<List<MedHistory>> callable = () -> dao.sync();

        Future<List<MedHistory>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
