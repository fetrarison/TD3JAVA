package edu.restaurant.app.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class Order {
    private Long id;
    private String reference; // ✅ Ajoute ceci
    private LocalDateTime createdAt;
    private List<DishOrder> dishOrders = new ArrayList<>();
    private List<OrderStatusLog> statusLogs = new ArrayList<>();

    public Order() {
        this.createdAt = LocalDateTime.now();
    }

    public edu.restaurant.app.dao.entity.OrderStatus getActualStatus() {
        if (statusLogs.isEmpty()) return null;
        return statusLogs.getLast().getNewStatus();
    }

    public void addStatusLog(OrderStatus newStatus) {
        OrderStatus oldStatus = getActualStatus();
        OrderStatusLog log = new OrderStatusLog(this, oldStatus, newStatus, createdAt);
        statusLogs.add(log);
    }

    public double getTotalAmount() {
        return dishOrders.stream()
                .mapToDouble(d -> d.getDish().getPrice() * d.getQuantity())
                .sum();
    }

    public void addDishOrders(List<DishOrder> dishOrders) {
        if (dishOrders == null) return;
        for (DishOrder d : dishOrders) {
            if (d != null) {
                d.setOrderId(this.id);
                this.dishOrders.add(d);
            }
        }
    }

}
