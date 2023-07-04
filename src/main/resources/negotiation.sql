-- DROP TABLE sales_item;
CREATE TABLE negotiation (
    id              integer primary key autoincrement,
    item_id         integer,
    suggested_price INTEGER,
    status          text,
    content         text,
    reply           text
);