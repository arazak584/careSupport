package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.Repository.ProfileRepository;
import com.khrc.caresupport.entity.MomProfile;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepository profileRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new ProfileRepository(application);
    }

    public MomProfile finds(String id) throws ExecutionException, InterruptedException {
        return profileRepository.finds(id);
    }

    public MomProfile find(String id, String password) throws ExecutionException, InterruptedException {
        return profileRepository.find(id, password);
    }

    public List<MomProfile> sync() throws ExecutionException, InterruptedException {
        return profileRepository.sync();
    }

    public void add(MomProfile data){
        profileRepository.create(data);
    }

    public void add(MomProfile... data){
        profileRepository.create(data);
    }

}
