package com.aikaload.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

//@Service
public class FireBaseService {

  /*  @PostConstruct
    private void initDB() throws IOException {
        InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream("./aikaloadspremium-firebase-adminsdk-wo89o-1411431e54.json");

         FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://aikaloadspremium-default-rtdb.firebaseio.com")
                .build();

        if(FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(options);
         }
    }

    public Firestore getFireBase(){
        return FirestoreClient.getFirestore();
    }*/
}
