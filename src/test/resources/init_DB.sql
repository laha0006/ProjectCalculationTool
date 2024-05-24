CREATE DATABASE IF NOT EXISTS test_db;
USE test_db;

DROP TABLE IF EXISTS user_entity_role;
DROP TABLE IF EXISTS invitation;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS team;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS organisation;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS status;
DROP VIEW IF EXISTS hierarchy;

CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR(50)  NOT NULL PRIMARY KEY,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN,
    email    VARCHAR(255) DEFAULT NULL
    );

CREATE TABLE IF NOT EXISTS authorities
(
    username  VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    PRIMARY KEY (username, authority),
    FOREIGN KEY (username) REFERENCES users (username)
    );

CREATE TABLE IF NOT EXISTS role
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(255) NOT NULL,
    weight SMALLINT UNSIGNED
    );

CREATE TABLE IF NOT EXISTS permission
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS role_permission
(
    role_id INT,
    perm_id INT,
    FOREIGN KEY (role_id) REFERENCES role (id),
    FOREIGN KEY (perm_id) REFERENCES permission (id),
    PRIMARY KEY (role_id, perm_id)
    );

CREATE TABLE IF NOT EXISTS status
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS organisation
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archived     BOOLEAN   DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS department
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    organisation_id INT,
    date_created    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archived        BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (organisation_id) REFERENCES organisation (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS team
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    department_id INT,
    date_created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archived      BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS project
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(255) NOT NULL,
    team_id        INT,
    date_created   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline       TIMESTAMP,
    allotted_hours INT,
    status         INT       DEFAULT 1,
    parent_id      INT,
    archived       BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES project (id) ON DELETE CASCADE,
    FOREIGN KEY (status) REFERENCES status (id)
    );

CREATE TABLE IF NOT EXISTS task
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255) NOT NULL,
    project_id      INT          NOT NULL,
    date_created    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline        TIMESTAMP,
    estimated_hours INT,
    actual_hours    INT       DEFAULT 0,
    status          INT       DEFAULT 4,
    parent_id       INT,
    archived        BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES task (id) ON DELETE CASCADE,
    FOREIGN KEY (status) REFERENCES status (id)
    );

CREATE TABLE IF NOT EXISTS user_entity_role
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) NOT NULL,
    role_id         INT,
    task_id         INT,
    project_id      INT,
    team_id         INT,
    department_id   INT,
    organisation_id INT,
    FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES task (id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE,
    FOREIGN KEY (organisation_id) REFERENCES organisation (id) ON DELETE CASCADE
    );



CREATE TABLE invitation
(
    username        VARCHAR(50) NOT NULL,
    organisation_iu INT         NOT NULL,
    PRIMARY KEY (username, organisation_iu),
    FOREIGN KEY (organisation_iu) REFERENCES organisation (id) ON DELETE CASCADE,
    FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);

CREATE VIEW hierarchy AS
SELECT tsk.id        AS task_id,
       pjt.id        AS project_id,
       tm.id         AS team_id,
       dpt.id        AS department_id,
       org.id        AS organisation_id,
       pjt.parent_id AS project_parent_id,
       tsk.parent_id AS task_parent_id
FROM organisation org
         LEFT JOIN department dpt
                   ON org.id = dpt.organisation_id
         LEFT JOIN team tm
                   ON dpt.id = tm.department_id
         LEFT JOIN project pjt
                   ON tm.id = pjt.team_id
         LEFT JOIN task tsk
                   ON pjt.id = tsk.project_id;


USE test_db;

INSERT INTO permission(name)
VALUES ('ORGANISATION_CREATE'),
       ('ORGANISATION_DELETE'),
       ('ORGANISATION_READ'),
       ('ORGANISATION_EDIT'),
       ('ORGANISATION_KICK'),
       ('ORGANISATION_INVITE'),
       ('DEPARTMENT_CREATE'),
       ('DEPARTMENT_DELETE'),
       ('DEPARTMENT_READ'),
       ('DEPARTMENT_EDIT'),
       ('DEPARTMENT_KICK'),
       ('DEPARTMENT_INVITE'),
       ('TEAM_CREATE'),
       ('TEAM_DELETE'),
       ('TEAM_READ'),
       ('TEAM_EDIT'),
       ('TEAM_KICK'),
       ('TEAM_INVITE'),
       ('PROJECT_CREATE'),
       ('PROJECT_DELETE'),
       ('PROJECT_READ'),
       ('PROJECT_EDIT'),
       ('PROJECT_KICK'),
       ('PROJECT_INVITE'),
       ('TASK_CREATE'),
       ('TASK_DELETE'),
       ('TASK_READ'),
       ('TASK_EDIT'),
       ('TASK_KICK'),
       ('TASK_INVITE');

INSERT INTO role(name, weight)
VALUES ('ORGANISATION_OWNER', 255),
       ('ORGANISATION_ADMIN', 250),
       ('ORGANISATION_MEMBER', 100),
       ('ORGANISATION_USER', 95),
       ('DEPARTMENT_OWNER', 245),
       ('DEPARTMENT_ADMIN', 240),
       ('DEPARTMENT_MEMBER', 90),
       ('DEPARTMENT_USER', 85),
       ('TEAM_OWNER', 235),
       ('TEAM_ADMIN', 230),
       ('TEAM_MEMBER', 80),
       ('TEAM_USER', 75),
       ('PROJECT_OWNER', 225),
       ('PROJECT_ADMIN', 220),
       ('PROJECT_MEMBER', 70),
       ('PROJECT_USER', 65),
       ('TASK_OWNER', 215),
       ('TASK_ADMIN', 110),
       ('TASK_MEMBER', 60),
       ('TASK_USER', 55);

INSERT INTO role_permission(role_id, perm_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (1, 9),
       (1, 10),
       (1, 11),
       (1, 12),
       (1, 13),
       (1, 14),
       (1, 15),
       (1, 16),
       (1, 17),
       (1, 18),
       (1, 19),
       (1, 20),
       (1, 21),
       (1, 22),
       (1, 23),
       (1, 24),
       (1, 25),
       (1, 26),
       (1, 27),
       (1, 28),
       (1, 29),
       (1, 30),
       (2, 1),
       (2, 3),
       (2, 5),
       (2, 6),
       (2, 7),
       (2, 8),
       (2, 9),
       (2, 10),
       (2, 11),
       (2, 12),
       (2, 13),
       (2, 14),
       (2, 15),
       (2, 16),
       (2, 17),
       (2, 18),
       (2, 19),
       (2, 20),
       (2, 21),
       (2, 22),
       (2, 23),
       (2, 24),
       (2, 25),
       (2, 26),
       (2, 27),
       (2, 28),
       (2, 29),
       (2, 30),
       (3, 3);


INSERT INTO role_permission(role_id, perm_id)
VALUES (5, 8),
       (5, 9),
       (5, 10),
       (5, 11),
       (5, 12),
       (5, 13),
       (5, 14),
       (5, 15),
       (5, 16),
       (5, 17),
       (5, 18),
       (5, 19),
       (5, 20),
       (5, 21),
       (5, 22),
       (5, 23),
       (5, 24),
       (5, 25),
       (5, 26),
       (5, 27),
       (5, 28),
       (5, 29),
       (5, 30),
       (6, 9),
       (6, 11),
       (6, 12),
       (6, 13),
       (6, 14),
       (6, 15),
       (6, 16),
       (6, 17),
       (6, 18),
       (6, 19),
       (6, 20),
       (6, 21),
       (6, 22),
       (6, 23),
       (6, 24),
       (6, 25),
       (6, 26),
       (6, 27),
       (6, 28),
       (6, 29),
       (6, 30),
       (7, 9);

INSERT INTO role_permission(role_id, perm_id)
VALUES (9, 14),
       (9, 15),
       (9, 16),
       (9, 17),
       (9, 18),
       (9, 19),
       (9, 20),
       (9, 21),
       (9, 22),
       (9, 23),
       (9, 24),
       (9, 25),
       (9, 26),
       (9, 27),
       (9, 28),
       (9, 29),
       (9, 30),
       (10, 15),
       (10, 17),
       (10, 18),
       (10, 19),
       (10, 20),
       (10, 21),
       (10, 22),
       (10, 23),
       (10, 24),
       (10, 25),
       (10, 26),
       (10, 27),
       (10, 28),
       (10, 29),
       (10, 30),
       (11, 15);

INSERT INTO role_permission(role_id, perm_id)
VALUES (13, 19),
       (13, 20),
       (13, 21),
       (13, 22),
       (13, 23),
       (13, 24),
       (13, 25),
       (13, 26),
       (13, 27),
       (13, 28),
       (13, 29),
       (13, 30),
       (14, 19),
       (14, 21),
       (14, 22),
       (14, 23),
       (14, 24),
       (14, 25),
       (14, 26),
       (14, 27),
       (14, 28),
       (14, 29),
       (14, 30),
       (15, 21);

INSERT INTO role_permission(role_id, perm_id)
VALUES (17, 25),
       (17, 26),
       (17, 27),
       (17, 28),
       (17, 29),
       (17, 30),
       (18, 25),
       (18, 27),
       (18, 28),
       (18, 29),
       (18, 30),
       (19, 27);


INSERT INTO status(name)
VALUES ('IN_PROGRESS'),
       ('DONE'),
       ('IN_REVIEW'),
       ("TODO");

INSERT INTO organisation(name,description) VALUES('EvilCorp','Evil Organisation');
INSERT INTO organisation(name,description) VALUES('Apple','Fruit Organisation');
INSERT INTO organisation(name,description) VALUES('Microsoft','Window Organisation');
INSERT INTO organisation(name,description) VALUES('Alpha Solutions','Great Organisation');

INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',1);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',1);
INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',2);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',2);
INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',3);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',3);
INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',4);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',4);

INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',1);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',2);
INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',3);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',4);
INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',5);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',6);
INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',7);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',8);

INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Frontend Project','a project',1,100,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',2);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',3);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',4);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',5);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',6);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',7);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',8);
INSERT INTO project(name,description,team_id, parent_id,allotted_hours,deadline) VALUES('sub one','one',8, 1,100,now() + INTERVAL 1 WEEK);

INSERT INTO task(name,description,project_id,actual_hours, deadline) VALUES('Frontend Task','Task',1,10, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, deadline) VALUES('Frontend Task','Task',1,15, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, deadline) VALUES('Frontend Task','Task',1,8, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, deadline) VALUES('Frontend Task','Task',1,12, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, deadline) VALUES('Frontend Task','Task',1,7, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, deadline) VALUES('Frontend Task','Task',1,16, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, deadline) VALUES('Frontend Task','Task',1,26, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',2);
INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',3);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',4);
INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',5);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',6);
INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',7);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',8);

INSERT INTO users(username,password) VALUES('tolana','password');
INSERT INTO users(username,password) VALUES('vz','password');
INSERT INTO users(username,password) VALUES('masiomasu','password');
INSERT INTO users(username,password) VALUES('dosei','password');
INSERT INTO users(username,password) VALUES('TheNewGuy','password');
INSERT INTO users(username,password) VALUES('someguy','password');
INSERT INTO users(username,password) VALUES('userA','password');
INSERT INTO users(username,password) VALUES('userB','password');

INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('tolana',1,1);
INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('masiomasu',2,1);
INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('vz',3,2);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('vz',1,4);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('dosei',3,4);




