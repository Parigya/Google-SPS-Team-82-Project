package com.sps.hobbymatcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sps.hobbymatcher.domain.Hobby;
import com.sps.hobbymatcher.domain.User;
import com.sps.hobbymatcher.domain.Post;
import com.sps.hobbymatcher.repository.HobbyRepository;
import com.sps.hobbymatcher.repository.UserRepository;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.ReadPolicy.Consistency;

import java.util.*;

@Service
public class HobbyService {

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private UserRepository userRepository;

    public Hobby createHobby(String name, User user) {
        
        Hobby hobby = new Hobby();

        List<Hobby> hobbyOpt=hobbyRepository.findByName(name);
        if(hobbyOpt!=null) {
            if(hobbyOpt.size()==0) {
                hobby.setName(name);
                hobby.getUsers().add(user.getId());
                Hobby saved=hobbyRepository.save(hobby);
                DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
                try {
                    Entity userEntity = datastore.get(KeyFactory.createKey("users", user.getId()));
                    Set<Long> myHobbies = user.getMyHobbies();
                    myHobbies.add(saved.getId());
                    userEntity.setProperty("myHobbies", myHobbies);
                    datastore.put(userEntity);
                } catch (EntityNotFoundException e) {
                // This should never happen
                }
                return saved;
            }
            else {
                return hobbyOpt.get(0);
            }
        }
        return hobby;
    }

    public Hobby save(Hobby hobby) {
        return hobbyRepository.save(hobby);
    }
}