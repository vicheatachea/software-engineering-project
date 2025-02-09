CREATE TABLE subject
(
    subject_id INT          NOT NULL,
    name       VARCHAR(255) NOT NULL,
    code       VARCHAR(255) NOT NULL,
    PRIMARY KEY (subject_id)
);

CREATE TABLE location
(
    location_id INT          NOT NULL,
    name        VARCHAR(255) NOT NULL,
    campus      VARCHAR(255) NOT NULL,
    building    VARCHAR(255) NOT NULL,
    PRIMARY KEY (location_id)
);

CREATE TABLE timetable
(
    timetable_id INT NOT NULL,
    PRIMARY KEY (timetable_id)
);

CREATE TABLE assignment
(
    assignment_id   INT          NOT NULL,
    type            VARCHAR(255) NOT NULL,
    publishing_date DATE         NOT NULL,
    deadline        DATE         NOT NULL,
    timetable_id    INT          NOT NULL,
    subject_id      INT          NOT NULL,
    PRIMARY KEY (assignment_id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (timetable_id),
    FOREIGN KEY (subject_id) REFERENCES subject (subject_id)
);

CREATE TABLE user
(
    user_id       INT          NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    date_of_birth DATE         NOT NULL,
    role          VARCHAR(255) NOT NULL,
    social_number VARCHAR(255) NOT NULL,
    timetable_id  INT          NOT NULL,
    PRIMARY KEY (user_id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (timetable_id)
);

CREATE TABLE user_group
(
    group_id     INT          NOT NULL,
    name         VARCHAR(255) NOT NULL,
    code         VARCHAR(255) NOT NULL,
    capacity     INT          NOT NULL,
    user_id      INT          NOT NULL,
    timetable_id INT          NOT NULL,
    PRIMARY KEY (group_id),
    FOREIGN KEY (user_id) REFERENCES user (user_id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (timetable_id)
);

CREATE TABLE teaching_session
(
    teaching_session_id INT  NOT NULL,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    subject_id          INT  NOT NULL,
    location_id         INT,
    timetable_id        INT  NOT NULL,
    PRIMARY KEY (teaching_session_id),
    FOREIGN KEY (subject_id) REFERENCES subject (subject_id),
    FOREIGN KEY (location_id) REFERENCES location (location_id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (timetable_id)
);

CREATE TABLE belongs_to
(
    user_id  INT NOT NULL,
    group_id INT NOT NULL,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES user (user_id),
    FOREIGN KEY (group_id) REFERENCES user_group (group_id)
);

CREATE TABLE teaches
(
    user_id  INT NOT NULL,
    class_id INT NOT NULL,
    PRIMARY KEY (user_id, class_id),
    FOREIGN KEY (user_id) REFERENCES user (user_id),
    FOREIGN KEY (class_id) REFERENCES teaching_session (teaching_session_id)
);