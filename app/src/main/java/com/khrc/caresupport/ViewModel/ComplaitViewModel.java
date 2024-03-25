package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.entity.Complaints;
import com.khrc.caresupport.Repository.ComplaintRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ComplaitViewModel extends AndroidViewModel {

    private final ComplaintRepository complaintRepository;


    public ComplaitViewModel(@NonNull Application application) {
        super(application);
        complaintRepository = new ComplaintRepository(application);
    }

    public List<Complaints> search(String id) throws ExecutionException, InterruptedException {
        return complaintRepository.search("%" + id + "%");
    }

    public List<Complaints> searchs(String id,String ids) throws ExecutionException, InterruptedException {
        return complaintRepository.searchs("%" + id + "%",ids);
    }

    public List<Complaints> repo(String id) throws ExecutionException, InterruptedException {
        return complaintRepository.repo(id);
    }
    public Complaints retrieves(String id) throws ExecutionException, InterruptedException {
        return complaintRepository.retrieves(id);
    }

    public long reply(String id) throws ExecutionException, InterruptedException {
        return complaintRepository.reply(id);
    }

    public long replys(String id) throws ExecutionException, InterruptedException {
        return complaintRepository.replys(id);
    }

    public List<Complaints> sync() throws ExecutionException, InterruptedException {
        return complaintRepository.sync();
    }

    public List<Complaints> notdone() throws ExecutionException, InterruptedException {
        return complaintRepository.notdone();
    }

    public List<Complaints> not() throws ExecutionException, InterruptedException {
        return complaintRepository.not();
    }

    public void add(Complaints data){
        complaintRepository.create(data);
    }

    public void add(Complaints... data){
        complaintRepository.create(data);
    }

}
