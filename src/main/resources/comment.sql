DROP TABLE comments;
CREATE TABLE comments (
    id      integer primary key autoincrement,
    item_id integer,
    writer text,
    password text,
    content text,
    reply text
);