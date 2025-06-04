# Plan: Add Search Endpoint for Todo Items (Partial Match)

## Goal
Add a new REST controller endpoint to return a list of todo items (tasks) based on a search term, matching tasks whose titles contain the term (case-insensitive, partial match).

---

## Steps

1. **[x] Update the repository layer to support searching tasks by partial title match.**
   - Add a method in `TaskRepository` to query tasks where the title contains a given substring.

2. **[x] Update the service layer to expose the search functionality.**
   - Add a method in `TaskService` that calls the new repository method.

3. **[x] Add a new endpoint in the controller.**
   - Add a `GET` endpoint (e.g., `/api/tasks/search?term=foo`) in `TaskRestController` that accepts a search term and returns matching tasks.

4. **[x] Test the new endpoint.**
   - Add or update tests to verify the endpoint returns correct results for various search terms (including case and partial matches).

5. **[x] Update documentation.**
   - Document the new endpoint in code comments and/or API docs.

---

## Progress Tracking
- [x] Step 1: Repository method for partial search
- [x] Step 2: Service method for partial search
- [x] Step 3: Controller endpoint for search
- [x] Step 4: Tests for search endpoint
- [x] Step 5: Documentation update

---

*As each step is completed, mark it as `[x]` in this file.*
