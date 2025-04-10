package edu.restaurant.app.dao.operations;

import edu.restaurant.app.dao.DataSource;
import edu.restaurant.app.dao.entity.Dish;
import edu.restaurant.app.dao.entity.DishOrder;
import edu.restaurant.app.dao.entity.DishOrderStatus;
import edu.restaurant.app.dao.entity.DishOrderStatusLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishOrderDAOImpl implements DishOrderDAO {

    private final DataSource dataSource = new DataSource();

    @Override
    public DishOrder findById(Long id) {
        String sql = "SELECT * FROM dish_in_order WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                DishOrder dishOrder = new DishOrder();
                dishOrder.setId(rs.getLong("id"));
                dishOrder.setQuantity(rs.getInt("quantity"));
                dishOrder.setOrderId(rs.getLong("order_id"));

                DishCrudOperations dishOps = new DishCrudOperations();
                Dish dish = dishOps.findById(rs.getLong("dish_id"));
                dishOrder.setDish(dish);

                // Load status logs
                List<DishOrderStatusLog> logs = findStatusLogsByDishOrderId(dishOrder.getId());
                dishOrder.setStatusLogs(logs);

                return dishOrder;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<DishOrder> findByOrderId(Long orderId) {
        List<DishOrder> results = new ArrayList<>();
        String sql = "SELECT * FROM dish_in_order WHERE order_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();

            DishCrudOperations dishOps = new DishCrudOperations();

            while (rs.next()) {
                DishOrder dishOrder = new DishOrder();
                dishOrder.setId(rs.getLong("id"));
                dishOrder.setQuantity(rs.getInt("quantity"));
                dishOrder.setOrderId(orderId);

                Dish dish = dishOps.findById(rs.getLong("dish_id"));
                dishOrder.setDish(dish);

                // Load status logs
                List<DishOrderStatusLog> logs = findStatusLogsByDishOrderId(dishOrder.getId());
                dishOrder.setStatusLogs(logs);

                results.add(dishOrder);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void save(DishOrder dishOrder) {
        String sql = "INSERT INTO dish_in_order (order_id, dish_id, quantity, status) VALUES (?, ?, ?, ?::dish_order_status) RETURNING id";


        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, dishOrder.getOrderId());
            stmt.setLong(2, dishOrder.getDish().getId());
            stmt.setInt(3, dishOrder.getQuantity());
            stmt.setString(4, dishOrder.getActualStatus().name()); // Use last status

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dishOrder.setId(rs.getLong("id"));
                }
            }

            // Save status log
            if (!dishOrder.getStatusLogs().isEmpty()) {
                saveStatusLog(dishOrder.getId(), dishOrder.getStatusLogs().getLast());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveStatusLog(Long dishOrderId, DishOrderStatusLog log) {
        String sql = """
    INSERT INTO dish_order_status_log (dish_order_id, old_status, new_status, change_datetime)
    VALUES (?, ?::dish_order_status, ?::dish_order_status, ?)
    """;


        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, dishOrderId);
            stmt.setString(2, log.getOldStatus() != null ? log.getOldStatus().name() : null);
            stmt.setString(3, log.getNewStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(log.getChangeDatetime()));

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<DishOrderStatusLog> findStatusLogsByDishOrderId(Long dishOrderId) {
        List<DishOrderStatusLog> logs = new ArrayList<>();

        String sql = "SELECT * FROM dish_order_status_log WHERE dish_order_id = ? ORDER BY change_datetime ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, dishOrderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DishOrderStatusLog log = new DishOrderStatusLog();
                log.setOldStatus(rs.getString("old_status") != null ? DishOrderStatus.valueOf(rs.getString("old_status")) : null);
                log.setNewStatus(DishOrderStatus.valueOf(rs.getString("new_status")));
                log.setChangeDatetime(rs.getTimestamp("change_datetime").toLocalDateTime());
                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }
}
