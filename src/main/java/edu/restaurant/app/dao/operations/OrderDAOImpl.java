package edu.restaurant.app.dao.operations;

import edu.restaurant.app.dao.DataSource;
import edu.restaurant.app.dao.entity.DishOrder;
import edu.restaurant.app.dao.entity.Order;
import edu.restaurant.app.dao.entity.OrderStatus;
import edu.restaurant.app.dao.entity.OrderStatusLog;
import edu.restaurant.app.dao.operations.DishOrderDAOImpl;
import edu.restaurant.app.dao.operations.OrderDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    private final DataSource dataSource = new DataSource();
    private final DishOrderDAO dishOrderDAO = new DishOrderDAOImpl();

    @Override
    public Order findById(Long id) {
        String sql = "SELECT * FROM customer_order WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setCreatedAt(rs.getTimestamp("order_datetime").toLocalDateTime());
                order.setReference(rs.getString("reference"));

                // Récupère le statut depuis la colonne "status" et crée un log
                OrderStatus status = OrderStatus.valueOf(rs.getString("status"));
                OrderStatusLog log = new OrderStatusLog(order, null, status, order.getCreatedAt());
                order.getStatusLogs().add(log);

                // Charger les plats associés
                List<DishOrder> dishOrders = dishOrderDAO.findByOrderId(id);
                order.setDishOrders(dishOrders);

                return order;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Order findByReference(String ref) {
        String sql = "SELECT * FROM customer_order WHERE reference = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ref);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return findById(rs.getLong("id")); // on réutilise findById
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO customer_order (reference, order_datetime, status) " +
                "VALUES (?, ?, ?::order_status) RETURNING id";




        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getReference());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));
            stmt.setString(3, order.getActualStatus().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    order.setId(rs.getLong("id"));
                }
            }

            // Sauvegarde des plats associés
            if (order.getDishOrders() != null) {
                for (DishOrder d : order.getDishOrders()) {
                    d.setOrderId(order.getId());
                    dishOrderDAO.save(d);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
