CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    country VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE machines (
    machine_id SERIAL PRIMARY KEY,
    machine_name VARCHAR(100) NOT NULL,
    machine_type VARCHAR(100),
    price DECIMAL(12,2)
);

CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INT REFERENCES customers(customer_id),
    machine_id INT REFERENCES machines(machine_id),
    quantity INT NOT NULL,
    order_date DATE DEFAULT CURRENT_DATE,
    expected_delivery DATE,
    status VARCHAR(30) DEFAULT 'Received'
);
