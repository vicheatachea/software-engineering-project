# Acceptance Plan

## Requirements

| Requirement            | Description                                                               |
|------------------------|---------------------------------------------------------------------------|
| Personalized Timetable | Users can view their classes and assignments in a personalized timetable. |
| Event Management       | Users can add, edit, and delete classes and assignments.                  |
| Alerts/Notifications   | Users can receive notifications for upcoming classes and assignments.     |
| Language Selection     | Users can select different languages for the interface.                   |
| Event Localisation     | Users can create classes and assignments in different languages.          |

## Test Cases

| ID  | Description            | Expected Result                                                          |
|-----|------------------------|--------------------------------------------------------------------------|
| F1  | Credentials Validation | The program correctly validates user input when they register or log in. |
| F2  | Timetable Display      | Users can view and interact with their personal timetable upon login.    |
| F3  | Language Selection     | Users can select a language for the entire interface.                    |
| F4  | Resource Management    | Teachers can add, edit or delete locations, subjects and groups.         |
| F5  | Personal Events        | Users can add, edit and delete personal assignments and classes.         |
| F6  | Group Events           | Teachers can add, edit and delete assignments and classes for groups     |
| F7  | Overlapping Events     | System identifies and correctly displays overlapping events.             |
| F8  | Notifications/Alerts   | Users receive notifications when classes start or assignments are due.   |
| F9  | Input Handling         | The application correctly handles invalid input and provides feedback.   |
| F10 | Action Confirmation    | The application prompts the user to confirm actions like deletion.       |