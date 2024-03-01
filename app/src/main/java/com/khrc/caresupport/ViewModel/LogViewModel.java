package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.entity.LogBook;
import com.khrc.caresupport.Repository.LogRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class LogViewModel extends AndroidViewModel {

    private final LogRepository logRepository;


    public LogViewModel(@NonNull Application application) {
        super(application);
        logRepository = new LogRepository(application);
    }

    public List<LogBook> search(String id) throws ExecutionException, InterruptedException {
        return logRepository.search(id);
    }

    public List<LogBook> sync() throws ExecutionException, InterruptedException {
        return logRepository.sync();
    }

    public void add(LogBook data){
        logRepository.create(data);
    }

    public void add(LogBook... data){
        logRepository.create(data);
    }

}
