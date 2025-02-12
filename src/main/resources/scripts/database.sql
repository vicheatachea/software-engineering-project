USE stms;

CREATE TABLE subject
(
    id   INT          NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE location
(
    id       INT          NOT NULL AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL,
    campus   VARCHAR(255) NOT NULL,
    building VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE timetable
(
    id INT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id)
);

CREATE TABLE assignment
(
    id              INT          NOT NULL AUTO_INCREMENT,
    type            VARCHAR(255) NOT NULL,
    publishing_date TIMESTAMP    NOT NULL,
    deadline        TIMESTAMP    NOT NULL,
    timetable_id    INT          NOT NULL,
    subject_id      INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (id),
    FOREIGN KEY (subject_id) REFERENCES subject (id)
);

CREATE TABLE user
(
    id            INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    salt          VARCHAR(255) NOT NULL,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    date_of_birth TIMESTAMP    NOT NULL,
    role          VARCHAR(255) NOT NULL,
    social_number VARCHAR(255) NOT NULL,
    timetable_id  INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (id)
);

CREATE TABLE user_group
(
    id           INT          NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    code         VARCHAR(255) NOT NULL,
    capacity     INT          NOT NULL,
    user_id      INT          NOT NULL,
    timetable_id INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (id)
);

CREATE TABLE teaching_session
(
    id           INT       NOT NULL AUTO_INCREMENT,
    start_date   TIMESTAMP NOT NULL,
    end_date     TIMESTAMP NOT NULL,
    subject_id   INT       NOT NULL,
    location_id  INT,
    timetable_id INT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (subject_id) REFERENCES subject (id),
    FOREIGN KEY (location_id) REFERENCES location (id),
    FOREIGN KEY (timetable_id) REFERENCES timetable (id)
);

CREATE TABLE belongs_to
(
    user_id  INT NOT NULL,
    group_id INT NOT NULL,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (group_id) REFERENCES user_group (id)
);

CREATE TABLE teaches
(
    user_id  INT NOT NULL,
    class_id INT NOT NULL,
    PRIMARY KEY (user_id, class_id),
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (class_id) REFERENCES teaching_session (id)
);