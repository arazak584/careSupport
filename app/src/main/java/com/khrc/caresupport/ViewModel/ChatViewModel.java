package com.khrc.caresupport.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.khrc.caresupport.Repository.ChatRepository;
import com.khrc.caresupport.Repository.ComplaintRepository;
import com.khrc.caresupport.entity.ChatResponse;
import com.khrc.caresupport.entity.Complaints;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatViewModel extends AndroidViewModel {

    private final ChatRepository chatRepository;


    public ChatViewModel(@NonNull Application application) {
        super(application);
        chatRepository = new ChatRepository(application);
    }


    public ChatResponse retrieves(String id) throws ExecutionException, InterruptedException {
        return chatRepository.retrieves(id);
    }

    public String retrieveMaxTel(String id) throws ExecutionException, InterruptedException {
        return chatRepository.retrieveMaxTel(id);
    }

    public ChatResponse retrieveMaxTels(String id) throws ExecutionException, InterruptedException {
        return chatRepository.retrieveMaxTels(id);
    }

    public String retrieve(String id) throws ExecutionException, InterruptedException {
        return chatRepository.retrieve(id);
    }

    public List<ChatResponse> repo(String id) throws ExecutionException, InterruptedException {
        return chatRepository.repo(id);
    }

    public List<ChatResponse> searchs(String id,String ids) throws ExecutionException, InterruptedException {
        return chatRepository.searchs("%" + id + "%",ids);
    }
    public List<ChatResponse> sync() throws ExecutionException, InterruptedException {
        return chatRepository.sync();
    }
    public void add(ChatResponse data){
        chatRepository.create(data);
    }

    public void add(ChatResponse... data){
        chatRepository.create(data);
    }

}
