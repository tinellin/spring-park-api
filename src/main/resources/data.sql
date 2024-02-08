INSERT INTO Users (id, username, password, role) VALUES (0, 'ana@email.com', '$2a$10$MlIKyizRtjh2ncWHLLES9.ocnszOenLL2TqRaRVGy4BaNl6bnkde2', 'ROLE_ADMIN');

INSERT INTO Users (id, username, password, role) VALUES (1, 'bia@email.com', '$2a$10$MlIKyizRtjh2ncWHLLES9.ocnszOenLL2TqRaRVGy4BaNl6bnkde2', 'ROLE_CLIENT');
INSERT INTO Users (id, username, password, role) VALUES (2, 'bob@email.com', '$2a$10$MlIKyizRtjh2ncWHLLES9.ocnszOenLL2TqRaRVGy4BaNl6bnkde2', 'ROLE_CLIENT');
INSERT INTO Users (id, username, password, role) VALUES (3, 'alex@email.com', '$2a$10$MlIKyizRtjh2ncWHLLES9.ocnszOenLL2TqRaRVGy4BaNl6bnkde2', 'ROLE_CLIENT');
INSERT INTO Users (id, username, password, role) VALUES (4, 'max@email.com', '$2a$10$MlIKyizRtjh2ncWHLLES9.ocnszOenLL2TqRaRVGy4BaNl6bnkde2', 'ROLE_CLIENT');
INSERT INTO Users (id, username, password, role) VALUES (5, 'will@email.com', '$2a$10$MlIKyizRtjh2ncWHLLES9.ocnszOenLL2TqRaRVGy4BaNl6bnkde2', 'ROLE_CLIENT');
INSERT INTO Users (id, username, password, role) VALUES (6, 'john@email.com', '$2a$10$MlIKyizRtjh2ncWHLLES9.ocnszOenLL2TqRaRVGy4BaNl6bnkde2', 'ROLE_CLIENT');

INSERT INTO Clients (id, name, cpf, user_id) VALUES (1, 'Bia A', '06545102028', 1);
INSERT INTO Clients (id, name, cpf, user_id) VALUES (2, 'Bob B', '11095382004', 2);
INSERT INTO Clients (id, name, cpf, user_id) VALUES (3, 'Alex X', '09304868033', 3);
INSERT INTO Clients (id, name, cpf, user_id) VALUES (4, 'Max X', '69893141095', 4);
INSERT INTO Clients (id, name, cpf, user_id) VALUES (5, 'Will L', '21294236040', 5);
INSERT INTO Clients (id, name, cpf, user_id) VALUES (6, 'John N', '30194352005', 6);