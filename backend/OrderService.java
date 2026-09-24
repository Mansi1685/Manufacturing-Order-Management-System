import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderService {

    public static void main(String[] args) {

        String query = """
                SELECT
                    o.order_id,
                    c.customer_name,
                    c.country,
                    m.machine_name,
                    o.quantity,
                    o.status,
                    o.expected_delivery
                FROM orders o
                JOIN customers c
                    ON o.customer_id = c.customer_id
                JOIN machines m
                    ON o.machine_id = m.machine_id
                ORDER BY o.order_id;
                """;

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {

                Order order = new Order(
                    result.getInt("order_id"),
                    result.getString("customer_name"),
                    result.getString("country"),
                    result.getString("machine_name"),
                    result.getInt("quantity"),
                    result.getString("status"),
                    result.getDate("expected_delivery").toString()
                );

                System.out.println(
                    order.getOrderId() + " | " +
                    order.getCustomerName() + " | " +
                    order.getCountry() + " | " +
                    order.getMachineName() + " | " +
                    order.getQuantity() + " | " +
                    order.getStatus() + " | " +
                    order.getExpectedDelivery()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}