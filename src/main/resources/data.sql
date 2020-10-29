-- passwords can be created using the PasswordEncoder groovy script
-- pwd: testpwd
merge into users(username, password, enabled) values ('test', '$2a$10$d27ULWd8iGYywmtAmFc31ev25topU0qfe5bhy8/DTZvJ4y/uzdipO', true);
-- pwd: analystpwd
merge into users(username, password, enabled) values ('analyst', '$2a$10$kKOXumKtHWFKCGRCyZAPlezOjE13J5t/qTmVUxKYiSnBIXveDzT12', true);
-- pwd: adminpwd
merge into users(username, password, enabled) values ('admin', '$2a$10$g0dzD.RxTWxYlT4nbcl0jO5Co2.osKLPlvUOdgbxWqPMFUSobeN6S', true);


merge into authorities(username, authority) values
('test', 'ROLE_USER'),
('analyst', 'ROLE_USER'),
('analyst', 'ROLE_ANALYST'),
('admin', 'ROLE_ADMIN');

