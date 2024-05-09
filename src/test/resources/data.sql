USE test_db;

INSERT INTO permission(name)
VALUES ('CREATE'),
       ('DELETE'),
       ('READ'),
       ('EDIT'),
       ('KICK'),
       ('INVITE');

INSERT INTO role(name, weight)
VALUES ('OWNER', 255),
       ('ADMIN', 200),
       ('MEMBER', 100),
       ('USER', 1);

INSERT INTO role_permission(role_id, perm_id)
VALUES (1, 1),(1, 2),(1, 3),(1, 4),(1, 5),(1, 6),
       (2, 1),(2, 2),(2, 3),(2, 4),(2, 5),(2, 6),
       (3, 3);

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

INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',1);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',2);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',3);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',4);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',5);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',6);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',7);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',8);

INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',1);
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

INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('tolana',1,1);
INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('masiomasu',2,1);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('masiomasu',1,2);
INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('vz',3,2);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('vz',1,4);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('dosei',3,4);




