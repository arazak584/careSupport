package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.subentity.ChatItem;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ComplaintRepository {

    private final ComplaintsDao dao;

    public ComplaintRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.complaintsDao();
    }

    public void create(Complaints... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(Complaints data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<Complaints> not() throws ExecutionException, InterruptedException {
        Callable<List<Complaints>> callable = () -> dao.not();

        Future<List<Complaints>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Complaints> search(String id) throws ExecutionException, InterruptedException {
        Callable<List<Complaints>> callable = () -> dao.search(id);

        Future<List<Complaints>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Complaints> searchs(String id,String ids) throws ExecutionException, InterruptedException {
        Callable<List<Complaints>> callable = () -> dao.searchs(id,ids);

        Future<List<Complaints>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public List<Complaints> repo(String id) throws ExecutionException, InterruptedException {
        Callable<List<Complaints>> callable = () -> dao.repo(id);

        Future<List<Complaints>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public Complaints retrieves(String id) throws ExecutionException, InterruptedException {
        Callable<Complaints> callable = () -> dao.retrieves(id);

        Future<Complaints> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public long reply(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.reply(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long replys(String id) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.replys(id);
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

    public long count() throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> dao.cnt();
        Future<Long> future = Executors.newSingleThreadExecutor().submit(callable);
        return future.get();
    }

}
