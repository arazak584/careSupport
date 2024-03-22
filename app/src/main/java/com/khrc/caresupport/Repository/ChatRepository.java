package com.khrc.caresupport.Repository;

import android.app.Application;

import com.khrc.caresupport.Dao.ChatDao;
import com.khrc.caresupport.Dao.ComplaintsDao;
import com.khrc.caresupport.Utility.AppDatabase;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChatRepository {

    private final ChatDao dao;

    public ChatRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.chatDao();
    }

    public void create(ChatResponse... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(ChatResponse data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }


    public List<ChatResponse> sync() throws ExecutionException, InterruptedException {
        Callable<List<ChatResponse>> callable = () -> dao.sync();

        Future<List<ChatResponse>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    public ChatResponse retrieves(String id) throws ExecutionException, InterruptedException {
        Callable<ChatResponse> callable = () -> dao.retrieves(id);

        Future<ChatResponse> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
