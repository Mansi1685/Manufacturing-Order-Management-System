import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

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

        server.createContext(
                "/api/orders",
                OrderAPI::handleOrders
        );

        server.createContext(
                "/api/orders/status",
                OrderAPI::handleStatusUpdate
        );

        server.createContext(
                "/api/orders/add",
                OrderAPI::handleAddOrder
        );

        server.start();

        System.out.println(
                "API server started at http://localhost:8080"
        );
    }


    // =========================
    // GET ORDERS
    // =========================

    private static void handleOrders(
            HttpExchange exchange) throws IOException {

        addCorsHeaders(exchange);

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        StringBuilder json =
                new StringBuilder("[");

        String query = """
                SELECT o.order_id,
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
                ORDER BY o.order_id
                """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(query);

            ResultSet result =
                    statement.executeQuery()
        ) {

            boolean first = true;

            while (result.next()) {

                if (!first) {
                    json.append(",");
                }

                json.append("{")

                    .append("\"orderId\":")
                    .append(result.getInt("order_id"))
                    .append(",")

                    .append("\"customer\":\"")
                    .append(result.getString("customer_name"))
                    .append("\",")

                    .append("\"country\":\"")
                    .append(result.getString("country"))
                    .append("\",")

                    .append("\"machine\":\"")
                    .append(result.getString("machine_name"))
                    .append("\",")

                    .append("\"quantity\":")
                    .append(result.getInt("quantity"))
                    .append(",")

                    .append("\"status\":\"")
                    .append(result.getString("status"))
                    .append("\",")

                    .append("\"delivery\":\"")
                    .append(result.getDate("expected_delivery"))
                    .append("\"")

                    .append("}");

                first = false;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        json.append("]");

        byte[] response =
                json.toString()
                        .getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(
                200,
                response.length
        );

        exchange.getResponseBody().write(response);

        exchange.close();
    }


    // =========================
    // UPDATE STATUS
    // =========================

    private static void handleStatusUpdate(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("PUT")) {

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String path =
                exchange.getRequestURI().getPath();

        String[] parts =
                path.split("/");

        int orderId =
                Integer.parseInt(parts[4]);

        String body =
                new String(
                        exchange.getRequestBody()
                                .readAllBytes(),
                        StandardCharsets.UTF_8
                );

        String status =
                body
                        .replace("{", "")
                        .replace("}", "")
                        .replace("\"", "")
                        .replace("status:", "")
                        .trim();

        String query = """
                UPDATE orders
                SET status = ?
                WHERE order_id = ?
                """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(query)
        ) {

            statement.setString(1, status);
            statement.setInt(2, orderId);

            int rowsUpdated =
                    statement.executeUpdate();

            System.out.println(
                    "Order " + orderId +
                    " updated to: " + status +
                    " | Rows updated: " +
                    rowsUpdated
            );

        } catch (Exception e) {

            e.printStackTrace();
        }

        String response =
                "{\"message\":\"Order status updated successfully\"}";

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        exchange.sendResponseHeaders(
                200,
                responseBytes.length
        );

        exchange.getResponseBody()
                .write(responseBytes);

        exchange.close();
    }


    // =========================
    // ADD NEW ORDER
    // =========================

    private static void handleAddOrder(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String body =
                new String(
                        exchange.getRequestBody()
                                .readAllBytes(),
                        StandardCharsets.UTF_8
                );

        String[] values =
                body
                        .replace("{", "")
                        .replace("}", "")
                        .replace("\"", "")
                        .split(",");

        int customerId = 0;
        int machineId = 0;
        int quantity = 0;
        String delivery = "";

        for (String value : values) {

            String[] pair =
                    value.split(":", 2);

            String key =
                    pair[0].trim();

            String val =
                    pair[1].trim();

            if (key.equals("customerId")) {
                customerId =
                        Integer.parseInt(val);
            }

            if (key.equals("machineId")) {
                machineId =
                        Integer.parseInt(val);
            }

            if (key.equals("quantity")) {
                quantity =
                        Integer.parseInt(val);
            }

            if (key.equals("delivery")) {
                delivery = val;
            }
        }


        String query = """
                INSERT INTO orders
                (
                    customer_id,
                    machine_id,
                    quantity,
                    expected_delivery,
                    status
                )
                VALUES (?, ?, ?, ?, 'Received')
                """;


        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(query)
        ) {

            statement.setInt(
                    1,
                    customerId
            );

            statement.setInt(
                    2,
                    machineId
            );

            statement.setInt(
                    3,
                    quantity
            );

            statement.setDate(
                    4,
                    java.sql.Date.valueOf(
                            delivery
                    )
            );

            statement.executeUpdate();

            System.out.println(
                    "New order added successfully."
            );


            String response =
                    "{\"message\":\"Order added successfully\"}";

            byte[] responseBytes =
                    response.getBytes(
                            StandardCharsets.UTF_8
                    );

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json"
            );

            exchange.sendResponseHeaders(
                    200,
                    responseBytes.length
            );

            exchange.getResponseBody()
                    .write(responseBytes);

        } catch (Exception e) {

            e.printStackTrace();

            String response =
                    "{\"message\":\"Failed to add order\"}";

            byte[] responseBytes =
                    response.getBytes(
                            StandardCharsets.UTF_8
                    );

            exchange.sendResponseHeaders(
                    500,
                    responseBytes.length
            );

            exchange.getResponseBody()
                    .write(responseBytes);
        }

        exchange.close();
    }


    // =========================
    // CORS
    // =========================

    private static void addCorsHeaders(
            HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );
    }
}