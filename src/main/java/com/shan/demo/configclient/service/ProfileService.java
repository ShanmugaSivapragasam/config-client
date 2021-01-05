package com.shan.demo.configclient.service;


import com.shan.demo.configclient.util.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ProfileService {

    @Autowired
    CacheManager cacheManager;

    public final static String id = "id";

    public Map<String, Object> getProfileDetails(String profileId) throws ExecutionException, InterruptedException {

        return (Map<String, Object>)cacheManager.getCache("profiles").get(profileId).get();
    }


}
