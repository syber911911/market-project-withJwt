CREATE TABLE sales_item (
    id integer primary key autoincrement,
    title text,
    description text,
    image_url text,
    min_price_wanted integer,
    status text,
    writer text,
    password text
);