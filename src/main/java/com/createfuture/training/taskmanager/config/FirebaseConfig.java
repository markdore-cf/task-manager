package com.createfuture.training.taskmanager.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * Configuration class for Firebase and Firestore integration.
 * Initializes Firebase on startup and provides the Firestore database bean.
 */
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) { // Prevent re-initialization
                InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
                if (serviceAccount == null) {
                    throw new IOException("Cannot find serviceAccountKey.json in classpath. " +
                            "Ensure it's in src/main/resources and the project is built.");
                }

                // Check if we should use the Firestore emulator
                String firestoreEmulatorHost = System.getenv("FIRESTORE_EMULATOR_HOST");
                if (firestoreEmulatorHost != null && !firestoreEmulatorHost.isEmpty()) {
                    System.out.println("Using Firestore emulator at: " + firestoreEmulatorHost);
                } else {
                    System.out.println("Connecting to cloud Firestore for project: mark-task-manager-fork-db");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId("mark-task-manager-fork-db")
                        .setConnectTimeout(30) // Increase timeout
                        .setHttpTransport(new NetHttpTransport()) // Explicit transport
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully.");

                // Test the connection
                Firestore db = FirestoreClient.getFirestore();
                ApiFuture<QuerySnapshot> future = db.collection("connection_test").limit(1).get();
                future.get(10, TimeUnit.SECONDS); // Wait up to 10 seconds
                System.out.println("Successfully connected to Firestore!");
            }
        } catch (IOException e) {
            System.err.println("Error initializing Firebase Admin SDK: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.err.println("Failed to connect to Firestore: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to Firestore", e);
        }
    }

    @Bean
    public Firestore getFirestore() {
        if (FirebaseApp.getApps().isEmpty()) {
            throw new IllegalStateException("FirebaseApp has not been initialized. Check FirebaseConfig.");
        }
        return FirestoreClient.getFirestore();
    }
}
