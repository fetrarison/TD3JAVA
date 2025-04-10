import edu.restaurant.app.dao.entity.*;
import edu.restaurant.app.dao.entity.DishOrderStatus;
import edu.restaurant.app.dao.entity.OrderStatus;
import edu.restaurant.app.dao.operations.DishCrudOperations;
import edu.restaurant.app.dao.operations.OrderDAOImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderSaveTest {

    private final OrderDAOImpl subject = new OrderDAOImpl();
    private final DishCrudOperations dishCrudOperations = new DishCrudOperations();

    @Test
    public void save_order() {
        // 1. Préparation
        Order order = new Order();
        order.setReference("ORD-" + System.currentTimeMillis()); // éviter les doublons
        order.addStatusLog(OrderStatus.CREATED); // statut de la commande

        Dish dish = dishCrudOperations.findById(1L);
        DishOrder dishOrder = new DishOrder(dish, 1); // 1 plat

        // ✅ Ajout du statut pour chaque plat dans la commande
        dishOrder.addStatusLog(DishOrderStatus.CREATED);
        order.addDishOrders(List.of(dishOrder));

        // 2. Exécution
        subject.save(order);

        // 3. Vérifications
        assertNotNull(order.getId(), "Order should be saved and get ID");
        assertEquals(OrderStatus.CREATED, order.getActualStatus());

        List<DishOrder> dishOrders = order.getDishOrders();
        assertFalse(dishOrders.isEmpty(), "Dish orders should be saved");

        for (DishOrder d : dishOrders) {
            assertEquals(DishOrderStatus.CREATED, d.getActualStatus(), "DishOrder should have CREATED status");
        }

        // Vérification en rechargeant depuis la base
        Order reloaded = subject.findById(order.getId());
        assertNotNull(reloaded);
        assertEquals(order.getReference(), reloaded.getReference());
        assertEquals(OrderStatus.CREATED, reloaded.getActualStatus());
    }
}
