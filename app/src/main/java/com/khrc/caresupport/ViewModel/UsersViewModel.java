package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


import com.khrc.caresupport.entity.Users;
import com.khrc.caresupport.Repository.UsersRepository;

import java.util.concurrent.ExecutionException;

public class UsersViewModel extends AndroidViewModel {

    private final UsersRepository usersRepository;

    public UsersViewModel(@NonNull Application application) {
        super(application);
        usersRepository = new UsersRepository(application);
    }

    public Users finds(String id) throws ExecutionException, InterruptedException {
        return usersRepository.finds(id);
    }

    public Users find(String id, String password) throws ExecutionException, InterruptedException {
        return usersRepository.find(id, password);
    }

    public long count() throws ExecutionException, InterruptedException {
        return usersRepository.count();
    }

    public void add(Users data){
        usersRepository.create(data);
    }

    public void add(Users... data){
        usersRepository.create(data);
    }

}
