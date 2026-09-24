import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderAPI {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/api/orders", OrderAPI::handleOrders);
        server.createContext("/api/orders/status", OrderAPI::updateStatus);

        server.start();

        System.out.println("API server started at http://localhost:8080");
    }


    private static void handleOrders(HttpExchange exchange)
            throws IOException {

        StringBuilder json = new StringBuilder("[");

        String query = """
                SELECT o.order_id, c.customer_name, c.country,
                       m.machine_name, o.quantity, o.status,
                       o.expected_delivery
                FROM orders o
                JOIN customers c ON o.customer_id = c.customer_id
                JOIN machines m ON o.machine_id = m.machine_id
                ORDER BY o.order_id
                """;

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery()
        ) {

            boolean first = true;

            while (result.next()) {

                if (!first) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"orderId\":").append(result.getInt("order_id")).append(",")
                    .append("\"customer\":\"").append(result.getString("customer_name")).append("\",")
                    .append("\"country\":\"").append(result.getString("country")).append("\",")
                    .append("\"machine\":\"").append(result.getString("machine_name")).append("\",")
                    .append("\"quantity\":").append(result.getInt("quantity")).append(",")
                    .append("\"status\":\"").append(result.getString("status")).append("\",")
                    .append("\"delivery\":\"").append(result.getDate("expected_delivery")).append("\"")
                    .append("}");

                first = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        json.append("]");

        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().add(
                "Content-Type", "application/json"
        );

        byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, response.length);

        exchange.getResponseBody().write(response);

        exchange.close();
    }


    private static void updateStatus(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("PUT")) {

            exchange.sendResponseHeaders(405, -1);

            exchange.close();

            return;
        }


        String path = exchange.getRequestURI().getPath();

        String[] parts = path.split("/");

        int orderId = Integer.parseInt(parts[4]);


        String requestBody = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );


        String status = requestBody
                .replace("{\"status\":\"", "")
                .replace("\"}", "");


        String query = """
                UPDATE orders
                SET status = ?
                WHERE order_id = ?
                """;


        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)
        ) {

            statement.setString(1, status);
            statement.setInt(2, orderId);

            statement.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();

            exchange.sendResponseHeaders(500, -1);

            exchange.close();

            return;
        }


        exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().add(
                "Content-Type", "application/json"
        );


        String response = "{\"message\":\"Status updated successfully\"}";

        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, data.length);

        exchange.getResponseBody().write(data);

        exchange.close();
    }
}