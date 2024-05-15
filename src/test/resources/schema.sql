CREATE DATABASE IF NOT EXISTS test_db;
USE test_db;

DROP TABLE IF EXISTS user_entity_role;
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
    FOREIGN KEY (organisation_id) REFERENCES organisation (id)
);

CREATE TABLE IF NOT EXISTS team
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    department_id INT,
    date_created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archived      BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (department_id) REFERENCES department (id)
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
    status         INT,
    parent_id      INT,
    archived       BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (team_id) REFERENCES team (id),
    FOREIGN KEY (parent_id) REFERENCES project (id),
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
    status          INT,
    parent_id       INT,
    archived        BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (project_id) REFERENCES project (id),
    FOREIGN KEY (parent_id) REFERENCES task (id),
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
    FOREIGN KEY (username) REFERENCES users (username),
    FOREIGN KEY (role_id) REFERENCES role (id),
    FOREIGN KEY (task_id) REFERENCES task (id),
    FOREIGN KEY (project_id) REFERENCES project (id),
    FOREIGN KEY (team_id) REFERENCES team (id),
    FOREIGN KEY (department_id) REFERENCES department (id),
    FOREIGN KEY (organisation_id) REFERENCES organisation (id)
);



CREATE VIEW hierarchy AS
SELECT tsk.id AS task_id,
       pjt.id AS project_id,
       tm.id  AS team_id,
       dpt.id AS department_id,
       org.id AS organisation_id
FROM organisation org
     LEFT JOIN department dpt
               ON org.id = dpt.organisation_id
     LEFT JOIN team tm
               ON dpt.id = tm.department_id
     LEFT JOIN project pjt
               ON tm.id = pjt.team_id
     LEFT JOIN task tsk
               ON pjt.id = tsk.project_id;

