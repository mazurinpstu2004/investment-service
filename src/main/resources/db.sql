create table "user"
(
    id       bigserial
        primary key,
    login    varchar(50)  not null
        unique,
    password varchar(255) not null
);

alter table "user"
    owner to postgres;

create table user_data
(
    id       bigserial
        primary key,
    user_id  bigint      not null
        references "user",
    fullname varchar(50) not null,
    email    varchar(50) not null,
    number   varchar(12) not null
);

alter table user_data
    owner to postgres;

create table stock
(
    id           bigserial
        primary key,
    company_name varchar(50) not null,
    price        bigint      not null
);

alter table stock
    owner to postgres;

create table user_stock
(
    id       bigserial
        primary key,
    user_id  bigint not null
        references "user",
    stock_id bigint not null
        references stock,
    count    bigint not null
);

alter table user_stock
    owner to postgres;

create table stock_transaction
(
    id        bigserial
        primary key,
    user_id   bigint not null
        references "user",
    stock_id  bigint not null
        references stock,
    count     bigint not null,
    total_sum bigint not null,
    date      date   not null,
    time      time   not null
);

alter table stock_transaction
    owner to postgres;

create table dividend
(
    id           bigserial
        primary key,
    stock_id     bigint not null
        references stock,
    amount       bigint not null,
    payment_date date   not null
);

alter table dividend
    owner to postgres;

create table account
(
    id      bigserial
        primary key,
    user_id bigint not null
        references "user",
    amount  bigint not null
);

alter table account
    owner to postgres;

create table account_transaction
(
    id         bigserial
        primary key,
    account_id bigint not null
        references account,
    amount     bigint not null,
    date       date   not null,
    time       time   not null
);

alter table account_transaction
    owner to postgres;

create view dividend_info(company_name, dividend_amount, dividend_yield_percent, payment_date) as
SELECT s.company_name,
       d.amount                                                      AS dividend_amount,
       round(d.amount::numeric / s.price::numeric * 100::numeric, 2) AS dividend_yield_percent,
       d.payment_date
FROM dividend d
         JOIN stock s ON d.stock_id = s.id;

alter table dividend_info
    owner to postgres;

create view user_stock_summary(user_id, stock_name, stock_count, total_price) as
SELECT u.id               AS user_id,
       s.company_name     AS stock_name,
       us.count           AS stock_count,
       us.count * s.price AS total_price
FROM "user" u
         JOIN user_stock us ON u.id = us.user_id
         JOIN stock s ON us.stock_id = s.id;

alter table user_stock_summary
    owner to postgres;

create view stock_transaction_info (user_id, stock_name, quantity, total_amount, transaction_date, transaction_time) as
SELECT st.user_id,
       s.company_name AS stock_name,
       st.count       AS quantity,
       st.total_sum   AS total_amount,
       st.date        AS transaction_date,
       st."time"      AS transaction_time
FROM stock_transaction st
         JOIN "user" u ON st.user_id = u.id
         JOIN stock s ON st.stock_id = s.id;

alter table stock_transaction_info
    owner to postgres;

create function validate_withdrawal_amount() returns trigger
    language plpgsql
as
$$
DECLARE
current_balance bigint;
BEGIN
SELECT amount INTO current_balance
FROM public.account
WHERE id = NEW.account_id;
IF NEW.amount < 0 AND ABS(NEW.amount) > current_balance THEN
        RAISE EXCEPTION 'Недостаточно средств для вывода. Текущий баланс: %', current_balance;
END IF;

RETURN NEW;
END;
$$;

alter function validate_withdrawal_amount() owner to postgres;

create trigger withdrawal_validation_trigger
    before insert
    on account_transaction
    for each row
    execute procedure validate_withdrawal_amount();

create function delete_user_stock() returns trigger
    language plpgsql
as
$$
BEGIN
    IF NEW.count <= 0 THEN
DELETE FROM user_stock WHERE id = NEW.id;
RETURN NULL;
END IF;
RETURN NEW;
END;
$$;

alter function delete_user_stock() owner to postgres;

create trigger delete_null_count_trigger
    after update
        of count
    on user_stock
    for each row
    execute procedure delete_user_stock();

create function create_account_for_new_user() returns trigger
    language plpgsql
as
$$
BEGIN
INSERT INTO public.account (user_id, amount)
VALUES (NEW.id, 0);
RETURN NEW;
END;
$$;

alter function create_account_for_new_user() owner to postgres;

create trigger trigger_create_account_after_user
    after insert
    on "user"
    for each row
    execute procedure create_account_for_new_user();

create function validate_stock_sale() returns trigger
    language plpgsql
as
$$
DECLARE
current_count BIGINT;
BEGIN
    -- Получаем текущее количество акций у пользователя
SELECT COALESCE(us.count, 0) INTO current_count
FROM public.user_stock us
WHERE us.user_id = NEW.user_id AND us.stock_id = NEW.stock_id;

-- Проверяем, что продается не больше, чем есть
IF NEW.count < 0 AND ABS(NEW.count) > current_count THEN
        RAISE EXCEPTION
            'Недостаточно акций для продажи. Попытка продать %, доступно %',
            ABS(NEW.count), current_count;
END IF;

RETURN NEW;
END;
$$;

alter function validate_stock_sale() owner to postgres;

create trigger trigger_validate_stock_sale
    before insert
    on stock_transaction
    for each row
    execute procedure validate_stock_sale();

create procedure calculate_user_stocks_summary(IN user_id bigint, OUT total_value bigint)
    language plpgsql
as
$$
BEGIN
SELECT COALESCE(SUM(us.count * s.price), 0)
INTO total_value
FROM public.user_stock us
         JOIN public.stock s ON us.stock_id = s.id
WHERE us.user_id = calculate_user_stocks_summarye.user_id;
END;
$$;

alter procedure calculate_user_stocks_summary(bigint, out bigint) owner to postgres;

