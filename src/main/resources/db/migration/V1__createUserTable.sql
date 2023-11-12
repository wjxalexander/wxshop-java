CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    tel        varchar(20) unique,
    avatar_url varchar(1024),
    created_at timestamp default current_timestamp,
    updated_at timestamp
);