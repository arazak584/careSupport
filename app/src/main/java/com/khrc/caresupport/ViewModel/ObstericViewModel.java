package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.Repository.ObstericRepository;
import com.khrc.caresupport.entity.Obsteric;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ObstericViewModel extends AndroidViewModel {

    private final ObstericRepository obstericRepository;


    public ObstericViewModel(@NonNull Application application) {
        super(application);
        obstericRepository = new ObstericRepository(application);
    }

    public Obsteric finds(String id) throws ExecutionException, InterruptedException {
        return obstericRepository.finds(id);
    }

    public List<Obsteric> sync(String id) throws ExecutionException, InterruptedException {
        return obstericRepository.sync(id);
    }

    public void add(Obsteric data){
        obstericRepository.create(data);
    }

    public void add(Obsteric... data){
        obstericRepository.create(data);
    }

}
