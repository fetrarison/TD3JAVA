package edu.restaurant.app.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class OrderStatusLog {

    private Long id;
    private Order order; // ✅ champ à ajouter
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private LocalDateTime changeDatetime;

    public OrderStatusLog() {}

    public OrderStatusLog(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        this.order = order;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changeDatetime = LocalDateTime.now();
    }

    public OrderStatusLog(Order order, OrderStatus oldStatus, OrderStatus newStatus, LocalDateTime changeDatetime) {
        this.order = order;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changeDatetime = changeDatetime;
    }
}

