package edu.restaurant.app.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DishOrder {
    private Long id;
    private Long orderId; // ✅ À ajouter
    private Dish dish;
    private int quantity;
    private List<DishOrderStatusLog> statusLogs = new ArrayList<>();

    public DishOrder() {}

    public DishOrder(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }

    public DishOrderStatus getActualStatus() {
        if (statusLogs.isEmpty()) return null;
        return statusLogs.getLast().getNewStatus();
    }

    public void addStatusLog(DishOrderStatus newStatus) {
        DishOrderStatus oldStatus = getActualStatus();
        DishOrderStatusLog log = new DishOrderStatusLog(this, oldStatus, newStatus);
        statusLogs.add(log);
    }
}
