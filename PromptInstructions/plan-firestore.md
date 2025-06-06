# Plan: Integrate Cloud Firestore for Task Management

## Goal
Modify the Task Manager application to use Cloud Firestore for storing and managing tasks, replacing the current H2 in-memory database for this purpose. The application should be runnable with the Firestore emulator for local development. All changes will be tracked here.

---

## Steps

1.  **[x] Setup Firebase Project & Firestore Database (Manual Prerequisite)**
    * Status: Done
    * Agent Role: Guide the user through the manual Firebase setup steps and confirm completion of each sub-step. The agent will not perform these actions.
    * Actions (User to perform, Agent to confirm):
        * **1.1. Access Firebase Console:**
            * User: Go to [https://console.firebase.google.com/](https://console.firebase.google.com/).
            * Agent: "Please confirm you have accessed the Firebase console."
        * **1.2. Create or Select Project:**
            * User: Click "Add project" to create a new Firebase project or select an existing one. Follow the on-screen instructions.
            * Agent: "Have you created or selected your Firebase project? What is the Project ID?" (User should provide the Project ID, which might be needed later).
        * **1.3. Navigate to Firestore Database:**
            * User: In the Firebase console for your project, go to the "Build" section in the left navigation panel and click on "Firestore Database".
            * Agent: "Are you on the Firestore Database page?"
        * **1.4. Create Firestore Database:**
            * User: Click "Create database".
            * Agent: "Have you clicked 'Create database'?"
        * **1.5. Select Database Mode:**
            * User: Choose "Start in **production mode**" (recommended for server-side SDK access, security rules can be refined later) or "Start in **test mode**" (allows open access for initial development, but secure it later).
            * Agent: "Which mode did you select (production or test)?"
        * **1.6. Select Firestore Location:**
            * User: Select a location for your Cloud Firestore data. **Note:** This cannot be changed later. Choose a region appropriate for your application.
            * Agent: "Have you selected a location for your Firestore data? Which region did you choose?"
        * **1.7. Enable Firestore:**
            * User: Click "Enable" or "Create database" (button text might vary).
            * Agent: "Is your Firestore database now enabled/created?"
        * **1.8. Generate Service Account Key:**
            * User: In the Firebase console, go to "Project settings" (click the gear icon next to "Project Overview").
            * User: Navigate to the "Service accounts" tab.
            * User: Ensure the correct Firebase project is selected.
            * User: Click the "Create service account" button if no suitable one exists, or select an existing one. If creating, give it a name (e.g., "task-manager-backend") and assign it a role. For Firestore access from a backend, "Cloud Datastore User" or "Editor" are common roles. "Firebase Admin SDK Administrator" is broader if you plan to use other Admin SDK features.
            * User: Once the service account is ready (or if using an existing one), click on the options (three dots) for that service account and select "Manage keys".
            * User: Click "Add Key", then "Create new key".
            * User: Choose "JSON" as the key type and click "Create". A JSON file will be downloaded.
            * Agent: "Have you downloaded the service account JSON key file?"
        * **1.9. Rename and Store Key:** (Status: Done)
            * User: Rename the downloaded JSON file to `serviceAccountKey.json`.
            * User: (For a later step) Be prepared to place this file in the `src/main/resources/` directory of your Java project. **Do not commit this file to Git if it's a public repository.**
            * Agent: "Have you renamed the key file to `serviceAccountKey.json` and noted where to place it?"
    * Verification: User confirms that a service account key JSON file (renamed to `serviceAccountKey.json`) has been successfully downloaded and they understand where it will be placed. (Status: Done)

2.  **[x] Add Firebase Admin SDK Dependency and Basic Configuration**
    * Status: Complete
    * Actions:
        * Modify `build.gradle` to include `implementation 'com.google.firebase:firebase-admin:LATEST_VERSION'` (replace LATEST_VERSION with the most recent one).
        * Create `src/main/java/com/createfuture/training/taskmanager/config/FirebaseConfig.java`.
        * In `FirebaseConfig.java`, add Spring configuration (`@Configuration`) to initialize `FirebaseApp` on startup (e.g., using `@PostConstruct` in a method) and provide a `Firestore` bean.
    * Links & Snippets:
        * **Firebase Admin SDK Setup:** [https://firebase.google.com/docs/admin/setup](https://firebase.google.com/docs/admin/setup)
        * **build.gradle snippet:**
            ```gradle
            dependencies {
                // ... other dependencies
                implementation 'com.google.firebase:firebase-admin:9.2.0' // Example version, check for latest
            }
            ```
        * **FirebaseConfig.java initial structure:**
            ```java
            package com.createfuture.training.taskmanager.config;

            import com.google.cloud.firestore.Firestore;
            import com.google.firebase.FirebaseApp;
            import com.google.firebase.cloud.FirestoreClient;
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            import jakarta.annotation.PostConstruct;
            // ... other imports for credentials and options will be added in the next step

            @Configuration
            public class FirebaseConfig {

                @PostConstruct
                public void initialize() {
                    // FirebaseApp initialization logic will go here in the next step
                }

                @Bean
                public Firestore getFirestore() {
                    if (FirebaseApp.getApps().isEmpty()) {
                        // This check is a safeguard, initialization should happen in @PostConstruct
                        // Consider logging a warning if initialization hasn't run
                        // initialize(); // Or handle error appropriately
                        throw new IllegalStateException("FirebaseApp has not been initialized. Check FirebaseConfig.");
                    }
                    return FirestoreClient.getFirestore();
                }
            }
            ```
        * **Agent Prompting Best Practice:** "Please add the Firebase Admin SDK dependency to your `build.gradle` file. Then, create the `FirebaseConfig.java` file with the basic structure for `@Configuration`, a `@PostConstruct` method for initialization (leave it empty for now), and a `@Bean` method to provide a `Firestore` instance. Ensure the `getFirestore` bean checks if `FirebaseApp` has been initialized."
    * Verification: Project builds successfully using `./gradlew build`. `plan-firestore-integration.md` updated.

3.  **[x] Configure Service Account Key and Firestore Emulator**
    * Status: Complete
    * Actions:
        * Place the downloaded `serviceAccountKey.json` into the `src/main/resources/` directory.
        * Update `FirebaseConfig.java` to load this service account key for `FirebaseOptions.Builder().setCredentials()`.
        * Add `serviceAccountKey.json` and `firestore-debug.log` to your `.gitignore` file.
        * Create `firebase.json` in the project root.
        * Document that to run locally, the Firestore emulator needs to be started (`firebase emulators:start --only firestore`) and the Spring Boot app needs the `FIRESTORE_EMULATOR_HOST` environment variable set (e.g., `FIRESTORE_EMULATOR_HOST="localhost:8080"`).
    * Links & Snippets:
        * **Initialize Firebase Admin SDK:** [https://firebase.google.com/docs/admin/setup#initialize-sdk](https://firebase.google.com/docs/admin/setup#initialize-sdk)
        * **Firebase Emulator Suite:** [https://firebase.google.com/docs/emulator-suite/install_and_configure](https://firebase.google.com/docs/emulator-suite/install_and_configure)
        * **FirebaseConfig.java `initialize()` method snippet:**
            ```java
            // Inside FirebaseConfig.java
            import com.google.auth.oauth2.GoogleCredentials;
            import com.google.firebase.FirebaseOptions;
            import java.io.IOException;
            import java.io.InputStream;

            // ...
            @PostConstruct
            public void initialize() {
                try {
                    if (FirebaseApp.getApps().isEmpty()) { // Prevent re-initialization
                        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
                        if (serviceAccount == null) {
                            throw new IOException("Cannot find serviceAccountKey.json in classpath. " +
                                                  "Ensure it's in src/main/resources and the project is built.");
                        }

                        FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            // .setProjectId("YOUR_PROJECT_ID_FROM_STEP_1") // Set if auto-detection fails or for clarity
                            .build();
                        FirebaseApp.initializeApp(options);
                        System.out.println("Firebase Admin SDK initialized successfully.");
                    }
                } catch (IOException e) {
                    // Log this error appropriately in a real app
                    System.err.println("Error initializing Firebase Admin SDK: " + e.getMessage());
                    // Consider re-throwing as a runtime exception if initialization is critical
                    // throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
                }
            }
            ```
        * **.gitignore entries:**
            ```
            # Firebase
            serviceAccountKey.json
            firestore-debug.log
            firebase-debug.log
            *.log
            .firebase/
            ```
        * **firebase.json content:**
            ```json
            {
              "emulators": {
                "firestore": {
                  "port": 8080
                },
                "ui": {
                  "enabled": true,
                  "port": 4000
                }
              }
            }
            ```
        * **Agent Prompting Best Practice:** "First, ensure `serviceAccountKey.json` is in `src/main/resources/`. Then, update the `initialize()` method in `FirebaseConfig.java` to load the service account key and initialize `FirebaseApp`. Add `serviceAccountKey.json` and `firestore-debug.log` to your `.gitignore`. Finally, create `firebase.json` in the project root with the emulator configuration. Remind the user about running the emulator and setting the `FIRESTORE_EMULATOR_HOST` environment variable for local development."
    * Verification: Project builds. `plan-firestore-integration.md` updated.

4.  **[x] Adapt Task Model for Firestore**
    * Status: Complete
    * Actions:
        * Modify `src/main/java/com/createfuture/training/taskmanager/model/Task.java`:
            * Remove JPA annotations: `@Entity`, `@Table`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
            * Change the `id` field type from `Long` to `String`.
            * Update the constructor `public Task(long id, String title)` to `public Task(String id, String title)`.
            * Update getter `public Long getId()` to `public String getId()`.
            * Update setter `public void setId(Long id)` to `public void setId(String id)`.
            * Add a no-argument constructor if one doesn't exist (Firestore needs it for deserialization).
            * Ensure getters and setters exist for all fields you want to store in Firestore.
    * Links & Snippets:
        * **Firestore Data Model (Custom Objects):** [https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects](https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects)
        * **Task.java changes:**
            ```java
            package com.createfuture.training.taskmanager.model;

            // Remove JPA imports: jakarta.persistence.*

            public class Task {
                private String id; // Changed from Long
                private String title;

                // Firestore needs a no-arg constructor for deserialization
                public Task() {}

                public Task(String title) {
                    this.title = title;
                }

                // Constructor used for creating Task object after fetching from Firestore
                public Task(String id, String title) { // Changed id type
                    this.id = id;
                    this.title = title;
                }

                public String getId() { // Changed return type
                    return id;
                }

                public void setId(String id) { // Changed parameter type
                    this.id = id;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                @Override
                public String toString() {
                    return "Task{" +
                            "id='" + id + '\'' + // Include ID for clarity
                            ", title='" + title + '\'' +
                            '}';
                }
            }
            ```
        * **Agent Prompting Best Practice:** "Please modify the `Task.java` model. Remove all JPA annotations. Change the `id` field from `Long` to `String`, and update its constructor, getter, and setter accordingly. Ensure a public no-argument constructor exists. Verify all fields to be stored in Firestore have public getters and setters."
    * Verification: Project builds successfully. `plan-firestore-integration.md` updated.

5.  **[x] Create `FirestoreTaskRepository.java` (Initial Implementation)**
    * Status: Complete
    * Actions:
        * Create a new class `src/main/java/com/createfuture/training/taskmanager/repository/FirestoreTaskRepository.java`.
        * Annotate with `@Repository`.
        * Inject the `Firestore` bean.
        * Define a constant for the collection name: `private static final String COLLECTION_NAME = "tasks";`
        * Implement `public Task addTask(String title)` to add a new task and return it with its Firestore ID.
        * Implement `public List<Task> getAllTasks()` to retrieve all tasks.
    * Links & Snippets:
        * **Add Data to Firestore:** [https://firebase.google.com/docs/firestore/manage-data/add-data#java_5](https://firebase.google.com/docs/firestore/manage-data/add-data#java_5)
        * **Get Data from Firestore:** [https://firebase.google.com/docs/firestore/query-data/get-data#java_5](https://firebase.google.com/docs/firestore/query-data/get-data#java_5)
        * **`FirestoreTaskRepository.java` structure and `addTask` / `getAllTasks`:**
            ```java
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

            @Repository
            public class FirestoreTaskRepository {

                private static final String COLLECTION_NAME = "tasks";
                private final Firestore db;

                public FirestoreTaskRepository(Firestore db) {
                    this.db = db;
                }

                public Task addTask(String title) throws ExecutionException, InterruptedException {
                    // Create a Map or a POJO for the data to be added
                    Map<String, Object> taskData = new HashMap<>();
                    taskData.put("title", title);
                    // Optionally add a timestamp for creation if needed for ordering later
                    // taskData.put("createdAt", FieldValue.serverTimestamp());


                    // Add a new document with a generated ID
                    DocumentReference docRef = db.collection(COLLECTION_NAME).document();
                    ApiFuture<WriteResult> result = docRef.set(taskData);

                    // Block on response for simplicity in this example, consider async handling in real apps
                    result.get(); // Ensures the write is complete

                    // Create a Task object to return, including the generated ID
                    return new Task(docRef.getId(), title);
                }

                public List<Task> getAllTasks() throws ExecutionException, InterruptedException {
                    List<Task> tasks = new ArrayList<>();
                    // Asynchronously retrieve all documents
                    ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
                    // future.get() blocks on response
                    List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                    for (DocumentSnapshot document : documents) {
                        // For each document, create a Task object
                        // Assumes Task class has a no-arg constructor and setters, or a constructor that matches fields
                        Task task = new Task(document.getId(), document.getString("title"));
                        // If using POJO mapping directly: Task task = document.toObject(Task.class);
                        // Ensure Task class has id field correctly mapped if using toObject()
                        tasks.add(task);
                    }
                    return tasks;
                }
            }
            ```
        * **Agent Prompting Best Practice:** "Create `FirestoreTaskRepository.java`. Annotate it with `@Repository` and inject the `Firestore` bean. Implement the `addTask` method to create a new document in the 'tasks' collection, storing the title, and return a `Task` object with the Firestore-generated ID. Then, implement `getAllTasks` to retrieve all documents from the 'tasks' collection and map them to a list of `Task` objects. Handle potential `ExecutionException` and `InterruptedException`."
    * Verification: Project builds successfully. `plan-firestore-integration.md` updated.

6.  **[x] Integrate `FirestoreTaskRepository` into `TaskService.java` (Initial)**
    * Status: Complete
    * Actions:
        * Modify `src/main/java/com/createfuture/training/taskmanager/service/TaskService.java`:
            * Inject `FirestoreTaskRepository`.
            * Comment out the H2 `TaskRepository` field and its usage in the constructor related to tasks.
            * Update `public Task addTask(String title)` to call `firestoreTaskRepository.addTask(title)`.
            * Update `public List<Task> getAllTasks()` to call `firestoreTaskRepository.getAllTasks()`.
    * Links & Snippets:
        * **TaskService.java changes:**
            ```java
            package com.createfuture.training.taskmanager.service;

            import com.createfuture.training.taskmanager.model.Task;
            // import com.createfuture.training.taskmanager.repository.TaskRepository; // Old H2 repo
            import com.createfuture.training.taskmanager.repository.FirestoreTaskRepository; // New Firestore repo
            import org.springframework.stereotype.Service;

            import java.util.List;
            import java.util.concurrent.ExecutionException; // Add if addTask/getAllTasks throw it

            @Service
            public class TaskService {
                // private final TaskRepository taskRepository; // Old H2 repo
                private final FirestoreTaskRepository firestoreTaskRepository; // New

                // public TaskService(TaskRepository taskRepository) { // Old constructor
                //    this.taskRepository = taskRepository;
                // }

                public TaskService(FirestoreTaskRepository firestoreTaskRepository) { // New constructor
                    this.firestoreTaskRepository = firestoreTaskRepository;
                }

                public Task addTask(String title) {
                    try {
                        return firestoreTaskRepository.addTask(title);
                    } catch (ExecutionException | InterruptedException e) {
                        // Handle exception appropriately - e.g., log and rethrow as runtime or custom exception
                        Thread.currentThread().interrupt(); // Restore interruption status
                        throw new RuntimeException("Error adding task to Firestore", e);
                    }
                }

                public List<Task> getAllTasks() {
                    try {
                        return firestoreTaskRepository.getAllTasks();
                    } catch (ExecutionException | InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Error getting all tasks from Firestore", e);
                    }
                }

                // ... other methods will be updated later
                public List<Task> getTopNTasks(int n) {
                    // return taskRepository.findTopN(n); // Old
                    // TODO: Implement with FirestoreTaskRepository in a later step
                    throw new UnsupportedOperationException("getTopNTasks not yet implemented for Firestore");
                }

                public boolean markDone(Long id) { // Keep Long for now, will change to String later
                    // return taskRepository.deleteById(id) > 0; // Old
                    // TODO: Implement with FirestoreTaskRepository in a later step
                    throw new UnsupportedOperationException("markDone not yet implemented for Firestore");
                }

                public void resetTasks() {
                    // taskRepository.reset(); // Old
                    // TODO: Implement with FirestoreTaskRepository in a later step or remove
                     throw new UnsupportedOperationException("resetTasks not yet implemented for Firestore or consider removal");
                }

                public List<Task> searchTasksByTitle(String term) {
                    // return taskRepository.searchByTitle(term); // Old
                    // TODO: Implement with FirestoreTaskRepository in a later step
                     throw new UnsupportedOperationException("searchTasksByTitle not yet implemented for Firestore");
                }
            }
            ```
        * **Agent Prompting Best Practice:** "Modify `TaskService.java`. Change the constructor to inject `FirestoreTaskRepository` instead of the old `TaskRepository`. Update the `addTask` and `getAllTasks` methods to call the corresponding methods in `FirestoreTaskRepository`. Ensure proper exception handling for `ExecutionException` and `InterruptedException` from Firestore calls. For now, other methods like `getTopNTasks`, `markDone`, etc., can throw `UnsupportedOperationException` or be commented out if they rely on the old repository."
    * Verification: Project builds. `plan-firestore-integration.md` updated.

7.  **[x] Implement Remaining CRUD in `FirestoreTaskRepository` and `TaskService`
    * Status: Complete
    * Actions:
        * In `FirestoreTaskRepository.java`:
            * Implement `public Task findById(String id)`.
            * Implement `public boolean markDone(String id)`.
            * Implement `public List<Task> searchTasksByTitle(String term)`.
            * Implement `public List<Task> getTopNTasks(int n)`.
        * In `TaskService.java`:
            * Update `markDone` to take `String id` and call the new repository method.
            * Update `searchTasksByTitle` and `getTopNTasks` to call their respective new repository methods.
            * Add `public Task getTaskById(String id)` if needed.
    * Links & Snippets:
        * **Get a Document:** [https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document](https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document)
        * **Delete Data:** [https://firebase.google.com/docs/firestore/manage-data/delete-data#java_2](https://firebase.google.com/docs/firestore/manage-data/delete-data#java_2)
        * **Simple Queries (for `limit` in `getTopNTasks`):** [https://firebase.google.com/docs/firestore/query-data/simple-queries#java_4](https://firebase.google.com/docs/firestore/query-data/simple-queries#java_4)
        * **Query Operators (for `searchTasksByTitle` - basic "startsWith" example):** [https://firebase.google.com/docs/firestore/query-data/queries#java_3](https://firebase.google.com/docs/firestore/query-data/queries#java_3) (Note: True partial/contains search is complex. A "startsWith" is simpler with Firestore directly.)
        * **`FirestoreTaskRepository.java` - `findById` example:**
            ```java
            public Task findById(String id) throws ExecutionException, InterruptedException {
                DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                DocumentSnapshot document = future.get();
                if (document.exists()) {
                    return new Task(document.getId(), document.getString("title"));
                    // Or: return document.toObject(Task.class); if Task is set up for it
                } else {
                    return null;
                }
            }
            ```
        * **`FirestoreTaskRepository.java` - `markDone` example:**
            ```java
            public boolean markDone(String id) {
                try {
                    ApiFuture<WriteResult> writeResult = db.collection(COLLECTION_NAME).document(id).delete();
                    writeResult.get(); // Block until done
                    return true; // Or check writeResult for more details if needed
                } catch (Exception e) {
                    // Log error
                    return false;
                }
            }
            ```
        * **`FirestoreTaskRepository.java` - `getTopNTasks` example:**
             ```java
            public List<Task> getTopNTasks(int n) throws ExecutionException, InterruptedException {
                List<Task> tasks = new ArrayList<>();
                ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).limit(n).get();
                // Add .orderBy("createdAt", Query.Direction.ASCENDING) if you have a timestamp
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (DocumentSnapshot document : documents) {
                    tasks.add(new Task(document.getId(), document.getString("title")));
                }
                return tasks;
            }
            ```
        * **`FirestoreTaskRepository.java` - `searchTasksByTitle` (basic "startsWith" example):**
            ```java
            public List<Task> searchTasksByTitle(String term) throws ExecutionException, InterruptedException {
                List<Task> tasks = new ArrayList<>();
                // Firestore is case-sensitive for startsWith. For case-insensitive, you'd typically store a lowercase version.
                // This is a simple "startsWith" example.
                Query query = db.collection(COLLECTION_NAME)
                                .orderBy("title") // orderBy is often needed for range queries
                                .startAt(term)
                                .endAt(term + "\uf8ff"); // \uf8ff is a high Unicode character for range matching

                ApiFuture<QuerySnapshot> future = query.get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                for (DocumentSnapshot document : documents) {
                    tasks.add(new Task(document.getId(), document.getString("title")));
                }
                return tasks;
            }
            // For true case-insensitive partial search, consider client-side filtering for small datasets
            // or a more advanced solution like a search service (Algolia) integrated with Firestore.
            ```
        * **Agent Prompting Best Practice:** "Implement `findById`, `markDone` (using String ID), `getTopNTasks`, and a basic `searchTasksByTitle` (e.g., startsWith) in `FirestoreTaskRepository.java`. Then, update `TaskService.java` to use these new repository methods, ensuring method signatures (like `markDone` taking a String ID) are consistent. Handle exceptions from Firestore calls."
    * Verification: Project builds. `plan-firestore-integration.md` updated.

8.  **[x] Update Controllers for `String` ID and Service Changes**
    * Status: Complete
    * Actions:
        * Modify `src/main/java/com/createfuture/training/taskmanager/controller/TaskController.java`:
            * Update `markTaskDone(@RequestParam Long id)` to `@RequestParam String id`.
        * Modify `src/main/java/com/createfuture/training/taskmanager/controller/TaskRestController.java`:
            * Update `deleteTask(@PathVariable Long id)` to `@PathVariable String id`.
    * Links & Snippets:
        * **TaskController.java `markTaskDone` change:**
            ```java
            // In TaskController.java
            @PostMapping("/tasks/done")
            public String markTaskDone(@RequestParam String id) { // Changed from Long to String
                taskService.markDone(id); // Service method should now accept String
                return "redirect:/";
            }
            ```
        * **TaskRestController.java `deleteTask` change:**
            ```java
            // In TaskRestController.java
            @DeleteMapping("/{id}")
            public ResponseEntity<Void> deleteTask(@PathVariable String id) { // Changed from Long to String
                boolean removed = taskService.markDone(id); // Service method should now accept String
                return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
            }
            ```
        * **Agent Prompting Best Practice:** "Update the `markTaskDone` method in `TaskController.java` and the `deleteTask` method in `TaskRestController.java` to accept `String` IDs instead of `Long`. Ensure they call the updated `taskService.markDone(String id)` method."
    * Verification: Project builds. `plan-firestore-integration.md` updated. UI and API should now mostly work with String IDs.

9.  **[x] Update Unit and Integration Tests**
    * Status: Complete
    * Actions:
        * `TaskControllerTest.java`: Update `postMarkTaskDone_ShouldCallService` for String ID.
        * `TaskRestControllerTest.java`: Update delete-related tests for String ID.
        * `TaskServiceTest.java`: Major refactor. Mock `FirestoreTaskRepository`. Remove `JdbcTemplate` usage for task setup. Update all test logic. Decide on `resetTasks` handling.
    * Links & Snippets:
        * **Mockito `verify` and `when`:** [https://site.mockito.org/](https://site.mockito.org/)
        * **Example `TaskServiceTest.java` structure (partial):**
            ```java
            // In TaskServiceTest.java
            import com.createfuture.training.taskmanager.repository.FirestoreTaskRepository;
            import org.junit.jupiter.api.BeforeEach;
            import org.junit.jupiter.api.Test;
            import org.mockito.InjectMocks;
            import org.mockito.Mock;
            import org.mockito.MockitoAnnotations; // Or use @ExtendWith(MockitoExtension.class)
            // Remove @SpringBootTest, @ActiveProfiles, @Transactional, @Autowired JdbcTemplate if no longer needed for this test class

            // @SpringBootTest // Remove if not testing Spring context loading here
            public class TaskServiceTest {

                @Mock // Mocks FirestoreTaskRepository
                private FirestoreTaskRepository firestoreTaskRepository;

                @InjectMocks // Creates an instance of TaskService and injects @Mock fields into it
                private TaskService taskService;

                @BeforeEach
                void setUp() {
                    MockitoAnnotations.openMocks(this); // Initializes mocks
                    // No more JdbcTemplate setup here for tasks
                }

                @Test
                void addTask_ShouldCallRepository() throws Exception { // Handle or declare exceptions from repo
                    String title = "Task 1";
                    Task mockTask = new Task("generatedId", title);
                    when(firestoreTaskRepository.addTask(title)).thenReturn(mockTask);

                    Task addedTask = taskService.addTask(title);

                    assertNotNull(addedTask);
                    assertEquals(title, addedTask.getTitle());
                    assertEquals("generatedId", addedTask.getId());
                    verify(firestoreTaskRepository).addTask(title);
                }

                @Test
                void markDone_ShouldCallRepositoryWithCorrectId() throws Exception {
                    String taskId = "someFirestoreId";
                    when(firestoreTaskRepository.markDone(taskId)).thenReturn(true);

                    boolean result = taskService.markDone(taskId);

                    assertTrue(result);
                    verify(firestoreTaskRepository).markDone(taskId);
                }
                // ... other tests refactored similarly ...
            }
            ```
        * **Agent Prompting Best Practice:** "Refactor `TaskServiceTest.java` to use Mockito for `FirestoreTaskRepository`. Remove direct H2/JdbcTemplate interactions for task data. Update `TaskControllerTest.java` and `TaskRestControllerTest.java` to reflect String ID changes in service calls and endpoint parameters. For `TaskServiceTest`, ensure the `resetTasks` method is either mocked, implemented for Firestore emulator (e.g., by clearing the collection), or its test is removed if the functionality is deprecated."
    * Verification: All tests pass after modifications. Project builds. `plan-firestore-integration.md` updated.

10. **[✓] Clean Up H2/JPA Configuration Related to Tasks**
    * Status: Complete
    * Actions:
        * Delete `src/main/java/com/createfuture/training/taskmanager/repository/TaskRepository.java` (the old H2 based repository). ✅
        * In `Task.java`, ensure all JPA entity annotations are removed. ✅
        * Review `application.properties` for H2/JPA settings related to tasks. ✅
        * Update app for dual storage (Firestore for tasks, H2 for secrets). ✅
        * Add clarifying documentation in code and comments. ✅
    * Changes Made:
        * Updated `application.properties` to clearly separate H2 configuration (for secrets) and Firestore configuration (for tasks)
        * Enhanced comments in `DataLoader.java` to clarify that H2 is only used for secrets
        * Added explanatory comments to `TaskManagerApplication.java` documenting the storage strategy
        * Confirmed that `Task.java` has no remaining JPA annotations
        * All tests pass after the changes
    * Verification: Project builds and runs correctly using Firestore for tasks. `DataLoader.java` functionality is unaffected. Documentation updated to reflect storage strategy changes.

11. **[x] Update Documentation (README)**
    * Status: Complete
    * Actions:
        * Modify `README.md`:
            * Update "Requirements" for Firestore.
            * Add "Firebase Setup" section (project, service key, emulator, env var).
            * Adjust H2 database references.
    * Links & Snippets:
        * **Agent Prompting Best Practice:** "Update `README.md`. In 'Requirements', state that tasks are now stored in Cloud Firestore. Add a new 'Firebase Setup' section detailing the manual Firebase project creation, the need for `serviceAccountKey.json` in `src/main/resources`, instructions to run the Firestore emulator (`firebase emulators:start --only firestore`), and the necessity of setting the `FIRESTORE_EMULATOR_HOST` environment variable. Clarify H2's role if it's still used by `DataLoader.java`."
    * Verification: `README.md` accurately reflects the new setup. `plan-firestore-integration.md` updated.


