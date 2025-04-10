import edu.restaurant.app.dao.entity.DishOrder;
import edu.restaurant.app.dao.entity.DishOrderStatus;
import edu.restaurant.app.dao.operations.DishOrderDAOImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DishOrderTest {

    @Test
    public void test_dish_order_status_from_db() {
        DishOrder dishOrder = new DishOrderDAOImpl().findById(1L);

        assertNotNull(dishOrder, "DishOrder should not be null");
        assertNotNull(dishOrder.getStatusLogs(), "StatusLogs should not be null");
        assertFalse(dishOrder.getStatusLogs().isEmpty(), "StatusLogs should not be empty");
        assertEquals(DishOrderStatus.CREATED, dishOrder.getActualStatus(), "Status should be CREATED");
    }
}
