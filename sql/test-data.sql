INSERT INTO organisation(name,description) VALUES('EvilCorp','Evil Organisation');
INSERT INTO organisation(name,description) VALUES('Apple','Fruit Organisation');
INSERT INTO organisation(name,description) VALUES('Microsoft','Window Organisation');
INSERT INTO organisation(name,description) VALUES('Alpha Solutions','Great Organisation');
INSERT INTO organisation(name,description) VALUES('Guinea Pig','Farm');

INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',1);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',1);
INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',2);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',2);
INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',3);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',3);
INSERT INTO department(name,description,organisation_id) VALUES('Frontend','Frontend dept',4);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',4);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',5);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',5);
INSERT INTO department(name,description,organisation_id) VALUES('Backend','Backend dept',5);

INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',1);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',2);
INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',3);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',4);
INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',5);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',6);
INSERT INTO team(name,description,department_id) VALUES('Frontend Team','Frontend Team',7);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',8);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',9);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',10);
INSERT INTO team(name,description,department_id) VALUES('Backend Team','Backend Team',11);

INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',1);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',2);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',3);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',4);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',5);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',6);
INSERT INTO project(name,description,team_id) VALUES('Frontend Project','a project',7);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',8);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',9);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',10);
INSERT INTO project(name,description,team_id) VALUES('Backend Project','a project',11);

INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',1);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',2);
INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',3);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',4);
INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',5);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',6);
INSERT INTO task(name,description,project_id) VALUES('Frontend Task','Task',7);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',8);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',9);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',9);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',10);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',10);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',11);
INSERT INTO task(name,description,project_id) VALUES('Backend Task','Task',11);

INSERT INTO users(username,password) VALUES('tolana','password');
INSERT INTO users(username,password) VALUES('vz','password');
INSERT INTO users(username,password) VALUES('masiomasu','password');
INSERT INTO users(username,password) VALUES('dosei','password');
INSERT INTO users(username,password) VALUES('pig','password');

INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('tolana',1,1);
INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('masiomasu',2,1);
INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('vz',3,2);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('vz',1,4);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('dosei',3,4);

INSERT INTO user_entity_role(username,role_id,organisation_id) VALUES('pig',3,5);

INSERT INTO user_entity_role(username,role_id,department_id) VALUES('pig',3,9);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('pig',3,10);
INSERT INTO user_entity_role(username,role_id,department_id) VALUES('pig',3,11);

INSERT INTO user_entity_role(username,role_id,team_id) VALUES('pig',3,11);
INSERT INTO user_entity_role(username,role_id,team_id) VALUES('pig',3,11);
INSERT INTO user_entity_role(username,role_id,team_id) VALUES('pig',3,11);

INSERT INTO user_entity_role(username,role_id,project_id) VALUES('pig',3,9);
INSERT INTO user_entity_role(username,role_id,project_id) VALUES('pig',3,10);
INSERT INTO user_entity_role(username,role_id,project_id) VALUES('pig',3,11);

INSERT INTO user_entity_role(username,role_id,task_id) VALUES('pig',3,9);
INSERT INTO user_entity_role(username,role_id,task_id) VALUES('pig',3,9);
INSERT INTO user_entity_role(username,role_id,task_id) VALUES('pig',3,10);
INSERT INTO user_entity_role(username,role_id,task_id) VALUES('pig',3,10);
INSERT INTO user_entity_role(username,role_id,task_id) VALUES('pig',3,11);
INSERT INTO user_entity_role(username,role_id,task_id) VALUES('pig',3,11);
