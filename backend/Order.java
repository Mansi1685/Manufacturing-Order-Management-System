public class Order {

    private int orderId;
    private String customerName;
    private String country;
    private String machineName;
    private int quantity;
    private String status;
    private String expectedDelivery;

    public Order(
            int orderId,
            String customerName,
            String country,
            String machineName,
            int quantity,
            String status,
            String expectedDelivery) {

        this.orderId = orderId;
        this.customerName = customerName;
        this.country = country;
        this.machineName = machineName;
        this.quantity = quantity;
        this.status = status;
        this.expectedDelivery = expectedDelivery;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCountry() {
        return country;
    }

    public String getMachineName() {
        return machineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getExpectedDelivery() {
        return expectedDelivery;
    }
}