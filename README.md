# Task Manager

This is a simple task management web application built with Spring Boot and Thymeleaf. 
It includes a user interface for adding and completing tasks.

## Requirements

* Java 21+
* Gradle
* H2 Database (embedded, runs in-memory for secrets storage)
* Cloud Firestore (for task data storage)
* Firebase CLI (for running the Firestore emulator locally)
* Node.js and npm (for running end-to-end tests)

## Running the Application

1. Clone the repository:

   ```sh
   git clone <repository-url>
   cd task-manager
   ```

## Firebase Setup

1. Create a Firebase project and Firestore database:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one
   - Enable Firestore for the project

2. Configure the service account key:
   - Download the service account key from Firebase console (Project Settings → Service Accounts)
   - Rename it to `serviceAccountKey.json` and place it in `src/main/resources/`

3. Local development with the Firestore emulator:
   - Install Firebase CLI if you haven't already: `npm install -g firebase-tools`
   - Login to Firebase: `firebase login`
   - List the projects to obtain project id: `firebase projects:list`
   - Set default project: `firebase use <projectID>`
   - Run the Firestore emulator in one terminal window:
   ```sh
   firebase emulators:start --only firestore
   ```
   - Set the environment variable and run the Spring Boot application in another terminal:
   ```sh
   # macOS/Linux
   export FIRESTORE_EMULATOR_HOST="localhost:8080" && ./gradlew bootRun
   
   # Windows (PowerShell)
   # $env:FIRESTORE_EMULATOR_HOST="localhost:8080"; ./gradlew bootRun
   ```

4. Open your browser and visit:

   ```
   http://localhost:8080
   ```

## Running Tests

### Running Unit Tests

To run all unit tests:

```sh
./gradlew test
```

### Running End-to-End Tests with Playwright

This project includes Playwright end-to-end tests that simulate user interactions with the application.

#### Prerequisites

1. Install Node.js dependencies:

   ```sh
   cd tests
   npm install
   ```

2. Install Playwright browsers:

   ```sh
   npx playwright install
   ```

3. Install the GitHub Copilot MCP VS Code extension:
   - Open VS Code
   - Go to Extensions (Ctrl+Shift+X or Cmd+Shift+X)
   - Search for "GitHub Copilot MCP"
   - Install the extension by Automata Labs (`automatalabs.copilot-mcp`)

#### Running Tests

To run all Playwright tests:

```sh
cd tests
npm test
```

To run tests with UI mode (for debugging):

```sh
cd tests
npm run test:ui
```

To run a specific test file:

```sh
cd tests
npx playwright test e2e/garfield-search.spec.ts
```

#### Using Model Context Protocol (MCP) for Test Development

To create new tests using the Model Context Protocol (MCP):

1. Start the MCP server:

   ```sh
   cd tests
   npx @playwright/test run-server --port=3000
   ```

2. Create a client script similar to `mcp-client.js` to test your scenario
3. Run the client to verify the steps work:

   ```sh
   node mcp-client.js
   ```

4. Once verified, create a Playwright test file in the `tests/e2e` directory

#### Creating New Tests

Follow this workflow to create new end-to-end tests:

1. Define your test scenario (e.g., "Navigate to website X, perform action Y, verify result Z")
2. Run all steps using the Playwright MCP to validate they work
3. Create a TypeScript test file in the `tests/e2e` directory following the `.spec.ts` format
4. Execute and refine the test until it passes reliably

You can also use the prompt template in `tests/prompts/generate_e2e_test.md` with GitHub Copilot in agent mode to help generate tests.

## Project Structure

### Backend (Spring Boot)

* `Task` – The model representing a single task.
* `FirestoreTaskRepository` – Repository for storing tasks in Cloud Firestore.
* `FirebaseConfig` - Configuration for Firebase initialization and Firestore access.
* `TaskService` – The service layer for managing task logic.
* `TaskController` – MVC controller for the web UI.
* `TaskRestController` – REST API endpoints for interacting with tasks.
* `templates/tasks.html` – The Thymeleaf front-end.
* `static/style.css` – Styling for the UI.

### Tests

* `src/test/` – Unit and integration tests for the Spring Boot application.
* `tests/` – End-to-end tests using Playwright:
  * `e2e/` – Contains the Playwright test files (`.spec.ts`)
  * `playwright.config.ts` – Configuration for Playwright tests
  * `mcp-client.js` – Example MCP client for test development


## Notes

* Task data is stored in Google Cloud Firestore.
* For local development, the Firestore emulator should be used as described in the Firebase Setup section.
* H2 in-memory database is still used for storing secrets, but tasks have been migrated to Firestore.
* You can view the H2 console at `http://localhost:8080/h2-console` for debugging other application data if needed.
