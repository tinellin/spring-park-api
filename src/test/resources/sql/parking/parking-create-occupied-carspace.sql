insert into Users (id, username, password, role)
    values (100, 'ana@email.com.br', '$2a$10$AtWo422MdyRQ1RgPzmJNnuDB7xN0GW38sXT4rnBFBqGnMyVmVEf4O', 'ROLE_ADMIN');
insert into Users (id, username, password, role)
    values (101, 'bia@email.com.br', '$2a$10$AtWo422MdyRQ1RgPzmJNnuDB7xN0GW38sXT4rnBFBqGnMyVmVEf4O', 'ROLE_CLIENT');
insert into Users (id, username, password, role)
    values (102, 'bob@email.com.br', '$2a$10$AtWo422MdyRQ1RgPzmJNnuDB7xN0GW38sXT4rnBFBqGnMyVmVEf4O', 'ROLE_CLIENT');

insert into Clients (id, name, cpf, user_id)values (21, 'Biatriz Rodrigues', '09191773016', 101);
insert into Clients (id, name, cpf, user_id) values (22, 'Rodrigo Silva', '98401203015', 102);

insert into Car_Space (id, code, status) values (100, 'A-01', 'OCCUPIED');
insert into Car_Space (id, code, status) values (200, 'A-02', 'OCCUPIED');
insert into Car_Space (id, code, status) values (300, 'A-03', 'OCCUPIED');
insert into Car_Space (id, code, status) values (400, 'A-04', 'OCCUPIED');
insert into Car_Space (id, code, status) values (500, 'A-05', 'OCCUPIED');

insert into Clients_has_Carspaces (receipt_code, license_plate, car_brand, car_model, car_color, entry_date, id_client, id_carspace)
    values ('20230313-101300', 'UJB2S76', 'FIAT', 'PALIO', 'VERDE', '2023-03-13 10:15:00', 22, 100);
insert into Clients_has_Carspaces (receipt_code, license_plate, car_brand, car_model, car_color, entry_date, id_client, id_carspace)
    values ('20230314-101400', 'HVJ4K87', 'FIAT', 'SIENA', 'BRANCO', '2023-03-14 10:15:00', 21, 200);
insert into Clients_has_Carspaces (receipt_code, license_plate, car_brand, car_model, car_color, entry_date, id_client, id_carspace)
    values ('20230315-101500', 'AOZ2O72', 'FIAT', 'PALIO', 'VERDE', '2023-03-14 10:15:00', 22, 300);
insert into Clients_has_Carspaces (receipt_code, license_plate, car_brand, car_model, car_color, entry_date, id_client, id_carspace)
    values ('20230316-101600', 'WGQ8F11', 'FIAT', 'SIENA', 'VERDE', '2023-03-14 10:15:00', 21, 400);
insert into Clients_has_Carspaces (receipt_code, license_plate, car_brand, car_model, car_color, entry_date, id_client, id_carspace)
    values ('20230317-101700', 'ONE0W12', 'FIAT', 'SIENA', 'VERDE', '2023-03-14 10:15:00', 22, 500);