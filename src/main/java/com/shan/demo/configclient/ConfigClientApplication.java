package com.shan.demo.configclient;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@Slf4j
@EnableCaching
public class ConfigClientApplication {

    @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
    private String jsonPath;


    @Autowired
    CacheManager cacheManager;

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

    @Bean
    public Firestore firestore() {

        Firestore firestore = null;
        log.info(" config client: initializing firestore with key " + jsonPath);
        GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            // Create an authorized Datastore service using Application Default Credentials.
            firestore = FirestoreOptions.newBuilder().setCredentials(credentials).build().getService();

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        addListenerToCollection(firestore);

        return firestore;

    }

    public void addListenerToCollection(Firestore firestore) {

        firestore.listCollections().forEach(collection -> log.info("  config client: reterieved firestore collection  " + collection.getId()));

        firestore.listCollections().forEach(collectionReference -> {
                    collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirestoreException e) {
                            log.info("  config client: listener invoked for " + collectionReference + " in the pod " + System.getenv("HOSTNAME"));
                            if (e != null) {
                                log.error("config client: Listen failed: " + e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
//                                    log.info("   config client:  New Doc added for collection:  " + collectionReference.getId() + " -  Doc ID:  " + dc.getDocument().getId() +
//                                            " doc Data: " + dc.getDocument().getData());
                                } else if (dc.getType() == DocumentChange.Type.MODIFIED) {
//                                    log.info("  config client: Doc modified for collection: " + collectionReference.getId() + " -   Doc ID:  " + dc.getDocument().getId() +
//                                            "  config client: doc Data: " + dc.getDocument().getData());
                                } else if (dc.getType() == DocumentChange.Type.REMOVED) {
//                                    log.info("  config client: Doc removed for collection:  " + collectionReference.getId() + " - Doc ID:  " + dc.getDocument().getId() +
//                                            " doc Data: " + dc.getDocument().getData());
                                } else {
//                                    log.info("  config client: document change invoked for unknown type : " + dc.getType());
                                }
                                if(collectionReference.getId().equalsIgnoreCase("profiles")){
                                    updateProfilesCache(dc.getDocument().getId(), dc.getDocument().getData());
                                    log.info("Config client: DocumentChange.Type " + dc.getType() + " changed/adde `cache` value   " + " in the pod " +
                                            System.getenv("HOSTNAME") + cacheManager.getCache(collectionReference.getId()).get(dc.getDocument().getId()).get());
                                }
                            }
                        }
                    });
                    log.info("  config client: listener added for " + collectionReference.get() + " in the pod " + System.getenv("HOSTNAME"));
                }

        );

    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache cache = new ConcurrentMapCache("profiles");
        cacheManager.setCaches(Arrays.asList(cache));
        return cacheManager;
    }

    private void updateProfilesCache(String id, Map<String, Object> data) {
        data.put("id", id);
        cacheManager.getCache("profiles").put(id, data);
    }

}
