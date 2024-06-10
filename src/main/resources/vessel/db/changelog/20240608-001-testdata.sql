--liquibase formatted sql

--changeset user:20240608-001
INSERT INTO frigate (country, vessel_class, max_speed) VALUES ('US', 'Constellation', 26.0);