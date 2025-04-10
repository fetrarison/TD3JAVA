package edu.restaurant.app.dao.operations;

import edu.restaurant.app.dao.entity.DishOrder;

import java.util.List;

public interface DishOrderDAO {
    DishOrder findById(Long id);
    List<DishOrder> findByOrderId(Long orderId);
    void save(DishOrder dishOrder);
}
