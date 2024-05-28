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
INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Backend Project','a project',2,50,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Frontend Project','a project',3,50,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Backend Project','a project',4,50,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Frontend Project','a project',5,50,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Backend Project','a project',6,50,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Frontend Project','a project',7,50,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id,allotted_hours,deadline) VALUES('Backend Project','a project',8,50,now() + INTERVAL 1 WEEK);
INSERT INTO project(name,description,team_id, parent_id,allotted_hours,deadline) VALUES('sub one','one',8, 1,100,now() + INTERVAL 1 WEEK);

INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task: 1','Task',1,10,14, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task: 2','Task',1,15,47, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task: 3','Task',1,8,65, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task: 4','Task',1,12,11, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task: 5','Task',1,7,71, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task: 6','Task',1,16,23, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task: 7','Task',1,26,11, now() + INTERVAL 1 WEEK);

INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Backend Task','Task',2,26,11, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task','Task',3,26,11, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Backend Task','Task',4,26,11, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task','Task',5,26,11, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Backend Task','Task',6,26,11, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Frontend Task','Task',7,26,11, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('Backend Task','Task',8,26,11, now() + INTERVAL 1 WEEK);

INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline) VALUES('MainTask Task','Task',1,26,41, now() + INTERVAL 1 WEEK);
INSERT INTO task(name,description,project_id,actual_hours, estimated_hours, deadline, parent_id) VALUES('SubTask Task','Task',1,12,26, now() + INTERVAL 1 WEEK, 15);

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