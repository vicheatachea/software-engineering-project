# STMS Project

## Description

STMS (Student Timetable Management System) is a desktop application that allows students to manage
their schedules. In the application, teachers can create, update, and delete subjects, locations and
groups, add and remove students from groups, and create and update assignments and classes for these
groups. Students can view their assignments and classes, and both teachers and students can manage
their individual timetables.

### Sequence diagram for creating an assignment:
![Create assignment sequence diagram](/docs/diagrams/images/STMS_Sequence_Diagram.png)

## Technologies

The STMS project was developed using the following technologies:

- Java 21
- JavaFX
- MariaDB
- Hibernate ORM

The project was built using the following tools:

- IntelliJ IDEA
- Scene Builder
- Jenkins
- Docker

## Installation

To install the STMS project, follow these steps:

1. Clone the repository to your local machine, with the following command:

    ```bash
    git clone https://github.com/vicheatachea/software-engineering-project.git
    ```

2. Open the project in IntelliJ IDEA or another Java IDE.

3. Create the database in MariaDB by running the db_init.sql script located in the `src/main/resources` directory.

4. Run the project by executing the `Main` class located in the `src/main/java` directory.