-- Insert sample order (1 client wants 2 hot dogs)
INSERT INTO customer_order (reference)
VALUES ('ORD-001')
ON CONFLICT DO NOTHING;

-- Insert dishes in the order
INSERT INTO dish_in_order (order_id, dish_id, quantity)
VALUES
    (1, 1, 2) -- 2 hot dogs in order 1
ON CONFLICT DO NOTHING;
