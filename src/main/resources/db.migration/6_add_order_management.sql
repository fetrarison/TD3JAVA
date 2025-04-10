-- Enum for order status
DO $$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_type WHERE typname = 'order_status') THEN
            CREATE TYPE order_status AS ENUM ('CREATED', 'CONFIRMED', 'IN_PREPARATION', 'COMPLETED', 'SERVED');
        END IF;

        IF NOT EXISTS (SELECT FROM pg_type WHERE typname = 'dish_order_status') THEN
            CREATE TYPE dish_order_status AS ENUM ('CREATED', 'CONFIRMED', 'IN_PREPARATION', 'COMPLETED', 'SERVED');
        END IF;
    END $$;

-- Table for orders
CREATE TABLE IF NOT EXISTS customer_order (
                                              id BIGSERIAL PRIMARY KEY,
                                              reference VARCHAR UNIQUE NOT NULL,
                                              order_datetime TIMESTAMP NOT NULL DEFAULT now(),
                                              status order_status NOT NULL DEFAULT 'CREATED'
);

-- Table for order status logs
CREATE TABLE IF NOT EXISTS customer_order_status_log (
                                                         id BIGSERIAL PRIMARY KEY,
                                                         order_id BIGINT REFERENCES customer_order(id),
                                                         old_status order_status,
                                                         new_status order_status,
                                                         change_datetime TIMESTAMP NOT NULL DEFAULT now()
);

-- Table for dishes in orders
CREATE TABLE IF NOT EXISTS dish_in_order (
                                             id BIGSERIAL PRIMARY KEY,
                                             order_id BIGINT REFERENCES customer_order(id),
                                             dish_id BIGINT REFERENCES dish(id),
                                             quantity INTEGER NOT NULL,
                                             status dish_order_status NOT NULL DEFAULT 'CREATED'
);

-- Table for dish status logs
CREATE TABLE IF NOT EXISTS dish_order_status_log (
                                                     id BIGSERIAL PRIMARY KEY,
                                                     dish_order_id BIGINT REFERENCES dish_in_order(id),
                                                     old_status dish_order_status,
                                                     new_status dish_order_status,
                                                     change_datetime TIMESTAMP NOT NULL DEFAULT now()
);
