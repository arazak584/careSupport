package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.UsersDao;
import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.Utility.AppDatabase;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UsersRepository {

    private final UsersDao dao;

    public UsersRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.usersDao();
    }

    public void create(Users... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Users data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public Users finds(String id) throws ExecutionException, InterruptedException {
        Callable<Users> callable = () -> dao.retrieves(id);

        Future<Users> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Users find(String id, String password) throws ExecutionException, InterruptedException {

        Callable<Users> callable = () -> dao.find(id, password);

        Future<Users> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
