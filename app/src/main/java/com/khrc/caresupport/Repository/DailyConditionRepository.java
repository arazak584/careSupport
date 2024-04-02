package com.khrc.caresupport.Repository;

import android.app.Application;


import com.khrc.caresupport.Dao.DailyConditionDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.DailyCondition;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DailyConditionRepository {

    private final DailyConditionDao dao;

    public DailyConditionRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.dailyConditionDao();
    }

    public void create(DailyCondition... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(DailyCondition data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public DailyCondition finds(String id,String todayDate) throws ExecutionException, InterruptedException {
        Callable<DailyCondition> callable = () -> dao.retrieves(id,todayDate);

        Future<DailyCondition> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<DailyCondition> search(String id) throws ExecutionException, InterruptedException {
        Callable<List<DailyCondition>> callable = () -> dao.search(id);

        Future<List<DailyCondition>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<DailyCondition> repo(String id) throws ExecutionException, InterruptedException {
        Callable<List<DailyCondition>> callable = () -> dao.repo(id);

        Future<List<DailyCondition>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<DailyCondition> searchs(String id, String ids) throws ExecutionException, InterruptedException {
        Callable<List<DailyCondition>> callable = () -> dao.searchs(id,ids);

        Future<List<DailyCondition>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<DailyCondition> sync() throws ExecutionException, InterruptedException {
        Callable<List<DailyCondition>> callable = () -> dao.sync();

        Future<List<DailyCondition>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
