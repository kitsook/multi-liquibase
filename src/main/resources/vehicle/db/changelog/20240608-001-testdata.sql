--liquibase formatted sql

--changeset user:20240608-001
INSERT INTO car (make, model, model_year, price) VALUES ('Ford', 'Explorer', 2018, 30000);
INSERT INTO car (make, model, model_year, price) VALUES ('Ford', 'Explorer', 2019, 31000);
INSERT INTO car (make, model, model_year, price) VALUES ('Ford', 'Explorer', 2020, 33000);
INSERT INTO car (make, model, model_year, price) VALUES ('Ford', 'Escape', 2018, 25000);
INSERT INTO car (make, model, model_year, price) VALUES ('Ford', 'Escape', 2019, 26000);
INSERT INTO car (make, model, model_year, price) VALUES ('Ford', 'Escape', 2020, 28000);