# Acceptance Plan

## Requirements

| Requirement                     | Description                                                               |
|---------------------------------|---------------------------------------------------------------------------|
| User Registration and Login     | Users can register and login to the system.                               |
| Personalized Timetable          | Users can view their classes and assignments in a personalized timetable. |
| Class and Assignment Management | Users can add, edit, and delete classes and assignments.                  |
| Alerts/Notifications            | Users receive notifications for upcoming classes and assignments.         |
| Usability                       | The system should be user-friendly and easy to navigate.                  |

## Test Cases

### Functional Tests

| ID  | Description            | Expected Result                                             |
|-----|------------------------|-------------------------------------------------------------|
| F1  | Valid Registration     | New users can register with valid credentials               |
| F2  | Duplicate Username     | System prevents registration with existing username         |
| F3  | Login Verification     | Registered users can login successfully                     |
| F4  | Password Recovery      | Users can recover their password using the recovery option  |
| F5  | Logout Functionality   | Users can logout successfully                               |
| F6  | Timetable Display      | User can view their personal timetable upon login           |
| F7  | Overlapping Events     | System identifies and correctly displays overlapping events |
| F8  | Filter Functionality   | Users can filter timetable by class/assignment types        |
| F9  | Class Addition         | Users can add new classes with required details             |
| F10 | Assignment Creation    | Users can create new assignments linked to classes          |
| F11 | Edit Functionality     | Users can modify existing class/assignment details          |
| F12 | Deletion Process       | Users can remove classes/assignments with confirmation      |
| F13 | Deadline Notifications | System sends alerts for upcoming assignment deadlines       |
| F14 | Class Reminders        | Users receive reminders before scheduled classes            |
| F15 | Error Handling         | System handles errors gracefully and provides feedback      |

### Performance Tests

| ID | Description          | Expected Result                                     |
|----|----------------------|-----------------------------------------------------|
| P1 | Response Time        | System responds to user actions within 2 seconds    |
| P2 | Data Retrieval Speed | Timetable data loads within 3 seconds               |
| P3 | Notification Speed   | Alerts are sent within 1 minute of the event        |
| P4 | Resource Utilization | System uses less than 70% CPU and memory under load |
| P5 | Database Performance | Database queries return results within 1 second     |

### Usability Tests

| ID | Description        | Expected Result                                               |
|----|--------------------|---------------------------------------------------------------|
| U1 | User Interface     | UI is intuitive and easy to navigate                          |
| U2 | Accessibility      | System is accessible to users with disabilities               |
| U3 | Invalid input      | System handles invalid input gracefully and provides feedback |
| U4 | User Satisfaction  | Users report satisfaction with the system                     |
| U5 | Onboarding Process | New users can easily understand how to use the system         |

