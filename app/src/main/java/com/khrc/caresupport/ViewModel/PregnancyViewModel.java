package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.Repository.PregnancyRepository;
import com.khrc.caresupport.entity.Pregnancy;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnancyViewModel extends AndroidViewModel {

    private final PregnancyRepository pregnancyRepository;


    public PregnancyViewModel(@NonNull Application application) {
        super(application);
        pregnancyRepository = new PregnancyRepository(application);
    }

    public Pregnancy finds(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.finds(id);
    }

    public long count(String id) throws ExecutionException, InterruptedException {
        return pregnancyRepository.count(id);
    }

    public List<Pregnancy> sync() throws ExecutionException, InterruptedException {
        return pregnancyRepository.sync();
    }

    public void add(Pregnancy data){
        pregnancyRepository.create(data);
    }

    public void add(Pregnancy... data){
        pregnancyRepository.create(data);
    }

}
