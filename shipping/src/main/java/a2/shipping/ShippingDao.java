package a2.shipping;

import a2.common.dao.BasicDao;
import a2.common.exception.DatabaseConnectionException;
import a2.common.ioc.AppBean;
import a2.common.model.OrderItem;
import a2.inventory.Inventory;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the orders table, mostly doing shipping related jobs.
 *
 * @since 1.0.0
 */
public class ShippingDao extends BasicDao implements AppBean {

    @Override
    protected String databaseName() {
        return "eep_leaftech";
    }

    /**
     * List all shipped or un-shipped orders.
     *
     * @param shipped
     * @return
     */
    public List<Order> queryOrders(boolean shipped) {
        List<Order> results = new ArrayList<>();

        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT * FROM `orders` WHERE `shipped` = %s", shipped);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("id"));
                order.setOrderDate(resultSet.getTimestamp("date"));
                order.setFirstName(resultSet.getString("first_name"));
                order.setLastName(resultSet.getString("last_name"));
                order.setAddress(resultSet.getString("address"));
                order.setPhone(resultSet.getString("phone"));
                order.setMessage(resultSet.getString("message"));
                order.setShipped(resultSet.getBoolean("shipped"));
                results.add(order);
            }

            return results;
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    /**
     * List all order items for a given order.
     *
     * @param orderId
     * @return
     */
    public List<OrderItem> queryOrderItems(int orderId) {
        List<OrderItem> results = new ArrayList<>();

        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("SELECT `p`.`id` AS `product_id`, " +
                    "`p`.`code` AS 'product_code', " +
                    "`p`.`description` AS `description`, " +
                    "`p`.`price` AS 'item_price' " +
                    "FROM `order_items` AS `oi` INNER JOIN `products` AS `p` ON `oi`.`product_id` = `p`.`id` " +
                    "WHERE `oi`.`order_id` = %s", orderId);
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(resultSet.getInt("product_id"));
                orderItem.setCode(resultSet.getString("product_code"));
                orderItem.setDescription(resultSet.getString("description"));
                orderItem.setUnitPrice(resultSet.getBigDecimal("item_price"));
                results.add(orderItem);
            }

            return results;
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }

    /**
     * Update order status to shipped or un-shipped.
     *
     * @param orderId
     * @param shipped
     */
    public void updateOrderShippingStatus(int orderId, boolean shipped) {
        try {
            Statement statement = getDatabaseConnection().createStatement();
            String query = String.format("UPDATE `orders` SET `shipped` = %s WHERE `id` = %s", shipped, orderId);
            statement.executeUpdate(query);
        } catch (Exception ex) {
            throw new DatabaseConnectionException(ex);
        }
    }
}
