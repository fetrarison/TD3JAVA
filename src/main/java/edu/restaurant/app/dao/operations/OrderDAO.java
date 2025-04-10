package edu.restaurant.app.dao.operations;
import edu.restaurant.app.dao.entity.Order;

import java.util.List;

public interface OrderDAO {
    Order findById(Long id);
    Order findByReference(String ref);
    void save(Order order);
}

