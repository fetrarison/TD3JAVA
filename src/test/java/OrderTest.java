import edu.restaurant.app.dao.entity.Order;
import edu.restaurant.app.dao.entity.OrderStatus;
import edu.restaurant.app.dao.operations.OrderDAOImpl;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testOrderCreation() {
        // Arrange
        Order order = new Order();
        String uniqueReference = "ORD-" + UUID.randomUUID().toString().substring(0, 8); // <-- Référence unique
        order.setReference(uniqueReference);
        order.addStatusLog(OrderStatus.CREATED); // Statut initial

        // Act
        new OrderDAOImpl().save(order);

        // Assert
        assertNotNull(order.getId(), "Order ID should not be null after saving");
        assertEquals(OrderStatus.CREATED, order.getActualStatus());

        // Reload from DB
        Order reloaded = new OrderDAOImpl().findById(order.getId());
        assertNotNull(reloaded);
        assertEquals(uniqueReference, reloaded.getReference());
        assertEquals(OrderStatus.CREATED, reloaded.getActualStatus());
    }
}
