CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    username varchar not null ,
    email varchar not null unique,
    phone varchar not null unique
);

CREATE TABLE ticket (
    id SERIAL PRIMARY KEY,
    session_id int not null,
    row_ticket int,
    cell_ticket int,
    available boolean default true,
    account_id int not null,
    constraint account_id_fk foreign key (account_id) references account(id),
    constraint unique_ticket UNIQUE (session_id, row_ticket, cell_ticket)
);