package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.Repository.MedHistoryRepository;
import com.khrc.caresupport.entity.MedHistory;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MedHistoryViewModel extends AndroidViewModel {

    private final MedHistoryRepository medHistoryRepository;


    public MedHistoryViewModel(@NonNull Application application) {
        super(application);
        medHistoryRepository = new MedHistoryRepository(application);
    }

    public MedHistory finds(String id) throws ExecutionException, InterruptedException {
        return medHistoryRepository.finds(id);
    }

    public List<MedHistory> sync() throws ExecutionException, InterruptedException {
        return medHistoryRepository.sync();
    }

    public void add(MedHistory data){
        medHistoryRepository.create(data);
    }

    public void add(MedHistory... data){
        medHistoryRepository.create(data);
    }

}
