package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.ProfileDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.MomProfile;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProfileRepository {

    private final ProfileDao dao;

    public ProfileRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.profileDao();
    }

    public void create(MomProfile... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(MomProfile data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public MomProfile finds(String id) throws ExecutionException, InterruptedException {
        Callable<MomProfile> callable = () -> dao.retrieves(id);

        Future<MomProfile> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public MomProfile find(String id, String password) throws ExecutionException, InterruptedException {

        Callable<MomProfile> callable = () -> dao.find(id, password);

        Future<MomProfile> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<MomProfile> sync() throws ExecutionException, InterruptedException {

        Callable<List<MomProfile>> callable = () -> dao.sync();

        Future<List<MomProfile>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
