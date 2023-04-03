--liquibase formatted sql

--changeset maxdrv:create_student_table
create table if not exists student
(
    id      bigserial    primary key,
    name    text         not null
);
