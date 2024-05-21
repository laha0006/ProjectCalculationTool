CREATE DATABASE IF NOT EXISTS project_calc_db;
USE project_calc_db;

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
    name         VARCHAR(50) NOT NULL,
    description  VARCHAR(100) NOT NULL,
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
    FOREIGN KEY (team_id) REFERENCES team (id) ON DELETE CASCADE ,
    FOREIGN KEY (parent_id) REFERENCES project (id) ON DELETE CASCADE ,
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