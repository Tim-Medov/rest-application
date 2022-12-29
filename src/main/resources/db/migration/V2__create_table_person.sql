create table person (
                        id int auto_increment not null,
                        name varchar(255) not null,
                        age int not null,
                        email varchar(255) not null,
                        created_time datetime(6) not null,
                        primary key (id));
