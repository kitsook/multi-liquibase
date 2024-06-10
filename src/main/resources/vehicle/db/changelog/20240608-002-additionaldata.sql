--liquibase formatted sql

--changeset user:20240608-002
INSERT INTO car (make, model, model_year, price) VALUES ('Toyota', 'RAV4', 2024, 30000);
INSERT INTO car (make, model, model_year, price) VALUES ('Mercedes', 'SLC', 2015, 50000);