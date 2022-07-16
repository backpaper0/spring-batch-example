create table file_and_db_output_example (
    id int primary key,
    name varchar(100) not null,
    flag boolean not null default(false)
);
