package edu.restaurant.app.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DishOrderStatusLog {
    private Long id;
    private DishOrder dishOrder;
    private DishOrderStatus oldStatus;
    private DishOrderStatus newStatus;
    private LocalDateTime changeDatetime;

    public DishOrderStatusLog() {}

    public DishOrderStatusLog(DishOrder dishOrder, DishOrderStatus oldStatus, DishOrderStatus newStatus) {
        this.dishOrder = dishOrder;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changeDatetime = LocalDateTime.now();
    }



}

