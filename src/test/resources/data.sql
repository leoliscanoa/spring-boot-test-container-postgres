--- PUBLIC SCHEMA

create table if not exists public.users
(
    id serial not null,
    firstname varchar(100) not null,
    gender   varchar(100) not null,
    lastname varchar(100) not null,
    primary key (id)
);
