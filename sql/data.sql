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
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6), # owner
       (2, 1),
       (2, 2),
       (2, 3),
       (2, 4),
       (2, 5),
       (2, 6), # admin
       (3, 3) # member

INSERT INTO status(name)
VALUES ("IN_PROGRESS"),
       ("DONE"),
       ("IN_REVIEW")





