-- Создание таблицы binary_file, если она не существует
CREATE TABLE IF NOT EXISTS public.binary_file
(
    id        SERIAL PRIMARY KEY,
    file_name VARCHAR,
    file_data  BYTEA
);
