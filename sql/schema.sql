CREATE DATABASE IF NOT EXISTS project_calc_db;
USE project_calc_db;

CREATE TABLE IF NOT EXISTS users(
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(500) NOT NULL,
    enabled BOOLEAN,
    email VARCHAR(255) DEFAULT NULL
    );

CREATE TABLE IF NOT EXISTS authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    PRIMARY KEY (username,authority),
    FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE IF NOT EXISTS role(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    weight SMALLINT UNSIGNED
    );

CREATE TABLE IF NOT EXISTS permission(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS role_permission(
    role_id INT,
    perm_ID INT,
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (perm_id) REFERENCES permission(id),
    PRIMARY KEY (role_id, perm_id)
    );

CREATE TABLE IF NOT EXISTS organization(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archived BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS department(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    organization_id INT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archived BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (organization_id) REFERENCES organization(id)
    );

CREATE TABLE IF NOT EXISTS team(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    department_id INT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archived BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (department_id) REFERENCES department(id)
    );

CREATE TABLE IF NOT EXISTS project(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    team_id INT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP,
    allotted_hours INT,
    status INT,
    parent_id INT,
    archived BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (parent_id) REFERENCES project(id)
    );

CREATE TABLE IF NOT EXISTS task(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    project_id INT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP,
    estimated_hours INT,
    actual_hours INT,
    status INT,
    parent_id INT,
    archived BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (parent_id) REFERENCES task(id)
    );

CREATE TABLE IF NOT EXISTS user_entity_role(
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    role_id INT,
    task_id INT,
    project_id INT,
    team_id INT,
    department_id INT,
    organization_id INT,
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (task_id) REFERENCES task(id),
    FOREIGN KEY (project_id) REFERENCES project(id),
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (department_id) REFERENCES department(id),
    FOREIGN KEY (organization_id) REFERENCES organization(id)
    );