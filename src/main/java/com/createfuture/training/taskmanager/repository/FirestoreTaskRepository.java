package com.createfuture.training.taskmanager.repository;

import com.createfuture.training.taskmanager.model.Task;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Repository class for managing Task entities in Firestore.
 */
@Repository
public class FirestoreTaskRepository {

    private static final String COLLECTION_NAME = "tasks";
    private final Firestore db;

    /**
     * Constructs a FirestoreTaskRepository with the given Firestore instance.
     *
     * @param db the Firestore instance
     */
    public FirestoreTaskRepository(Firestore db) {
        this.db = db;
    }

    /**
     * Adds a new task with the given title to Firestore.
     *
     * @param title the title of the task
     * @return the newly created Task object with its Firestore document ID
     * @throws ExecutionException   if there's an error executing the Firestore
     *                              operation
     * @throws InterruptedException if the operation is interrupted
     */
    public Task addTask(String title) throws ExecutionException, InterruptedException {
        // Create a map to represent the task data
        Map<String, Object> taskData = new HashMap<>();
        taskData.put("title", title);
        // Add creation timestamp for potential ordering by creation date
        taskData.put("createdAt", FieldValue.serverTimestamp());

        // Add a new document with a generated ID
        DocumentReference docRef = db.collection(COLLECTION_NAME).document();
        ApiFuture<WriteResult> result = docRef.set(taskData);

        // Wait for the write to complete
        result.get();

        // Create a Task object to return, including the generated ID
        return new Task(docRef.getId(), title);
    }

    /**
     * Retrieves all tasks from Firestore.
     *
     * @return a list of all Task objects
     * @throws ExecutionException   if there's an error executing the Firestore
     *                              operation
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Task> getAllTasks() throws ExecutionException, InterruptedException {
        List<Task> tasks = new ArrayList<>();

        // Asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();

        // Wait for the query to complete
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        // Convert documents to Task objects
        for (DocumentSnapshot document : documents) {
            Task task = new Task(document.getId(), document.getString("title"));
            tasks.add(task);
        }

        return tasks;
    }

    /**
     * Finds a task by its document ID.
     *
     * @param id the document ID of the task to find
     * @return the found Task object, or null if no task exists with the given ID
     * @throws ExecutionException   if there's an error executing the Firestore
     *                              operation
     * @throws InterruptedException if the operation is interrupted
     */
    public Task findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return new Task(document.getId(), document.getString("title"));
        } else {
            return null;
        }
    }

    /**
     * Marks a task as done by deleting it from Firestore.
     *
     * @param id the document ID of the task to mark as done
     * @return true if the task was successfully marked as done, false otherwise
     * @throws ExecutionException   if there's an error executing the Firestore
     *                              operation
     * @throws InterruptedException if the operation is interrupted
     */
    public boolean markDone(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        // Check if document exists first
        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) {
            return false;
        }

        // Delete the document
        ApiFuture<WriteResult> writeResult = docRef.delete();
        writeResult.get(); // Wait for the delete to complete
        return true;
    }

    /**
     * Searches for tasks with titles containing the given term.
     *
     * @param term the search term
     * @return a list of Task objects that match the search criteria
     * @throws ExecutionException   if there's an error executing the Firestore
     *                              operation
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Task> searchTasksByTitle(String term) throws ExecutionException, InterruptedException {
        List<Task> tasks = new ArrayList<>();

        // Get all tasks - Firestore doesn't directly support contains/like queries
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        // Client-side filtering for titles containing the search term
        // (case-insensitive)
        String lowerTerm = term.toLowerCase();
        for (DocumentSnapshot document : documents) {
            String title = document.getString("title");
            if (title != null && title.toLowerCase().contains(lowerTerm)) {
                tasks.add(new Task(document.getId(), title));
            }
        }

        return tasks;
    }

    /**
     * Retrieves the top N tasks from Firestore.
     *
     * @param n the number of tasks to retrieve
     * @return a list of the top N Task objects
     * @throws ExecutionException   if there's an error executing the Firestore
     *                              operation
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Task> getTopNTasks(int n) throws ExecutionException, InterruptedException {
        List<Task> tasks = new ArrayList<>();

        // Query with a limit
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                .orderBy("createdAt", Query.Direction.DESCENDING) // Using createdAt timestamp for ordering
                .limit(n)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (DocumentSnapshot document : documents) {
            tasks.add(new Task(document.getId(), document.getString("title")));
        }

        return tasks;
    }

    /**
     * Deletes all tasks from Firestore.
     *
     * @throws ExecutionException   if there's an error executing the Firestore
     *                              operation
     * @throws InterruptedException if the operation is interrupted
     */
    public void resetTasks() throws ExecutionException, InterruptedException {
        // Get all documents
        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        // Delete each document
        for (QueryDocumentSnapshot document : documents) {
            document.getReference().delete().get();
        }
    }
}
