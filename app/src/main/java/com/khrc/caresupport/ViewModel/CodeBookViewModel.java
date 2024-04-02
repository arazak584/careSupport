package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.Repository.CodeBookRepository;
import com.khrc.caresupport.Utility.KeyValuePair;
import com.khrc.caresupport.entity.CodeBook;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CodeBookViewModel extends AndroidViewModel {

    private final CodeBookRepository repo;


    public CodeBookViewModel(@NonNull Application application) {
        super(application);
        repo = new CodeBookRepository(application);
    }

    public List<CodeBook> findAll() throws ExecutionException, InterruptedException {
        return repo.findAll();
    }

    public List<KeyValuePair> findCodesOfFeature(String codeFeature) throws ExecutionException, InterruptedException {
        return repo.findCodesOfFeature(codeFeature);
    }

    public CodeBook finds() throws ExecutionException, InterruptedException {
        return repo.finds();
    }

    public void add(CodeBook... data){
        repo.create(data);
    }

}
