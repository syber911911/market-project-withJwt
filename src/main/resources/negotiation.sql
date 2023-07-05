DROP TABLE negotiation;
CREATE TABLE negotiation (
    id              integer primary key autoincrement,
    item_id         integer,
    suggested_price INTEGER,
    status          text,
    writer          text,
    password        text
);