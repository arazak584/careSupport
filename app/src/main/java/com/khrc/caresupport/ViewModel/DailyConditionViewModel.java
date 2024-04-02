package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.Repository.DailyConditionRepository;
import com.khrc.caresupport.entity.DailyCondition;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DailyConditionViewModel extends AndroidViewModel {

    private final DailyConditionRepository dailyConditionRepository;


    public DailyConditionViewModel(@NonNull Application application) {
        super(application);
        dailyConditionRepository = new DailyConditionRepository(application);
    }

    public DailyCondition finds(String id, String todayDate) throws ExecutionException, InterruptedException {
        return dailyConditionRepository.finds(id,todayDate);
    }

    public List<DailyCondition> search(String id) throws ExecutionException, InterruptedException {
        return dailyConditionRepository.search(id);
    }

    public List<DailyCondition> repo(String id) throws ExecutionException, InterruptedException {
        return dailyConditionRepository.repo(id);
    }

    public List<DailyCondition> searchs(String id,String ids) throws ExecutionException, InterruptedException {
        return dailyConditionRepository.searchs(id,ids);
    }

    public List<DailyCondition> sync() throws ExecutionException, InterruptedException {
        return dailyConditionRepository.sync();
    }

    public void add(DailyCondition data){
        dailyConditionRepository.create(data);
    }

    public void add(DailyCondition... data){
        dailyConditionRepository.create(data);
    }

}
