INSERT INTO customers (customer_name, country, email)
VALUES
('TechWerk GmbH', 'Germany', 'contact@techwerk.com'),
('AutoMech Solutions', 'United Kingdom', 'info@automech.com'),
('Indus Automation', 'India', 'orders@indusautomation.com');

INSERT INTO machines (machine_name, machine_type, price)
VALUES
('CNC Precision Machine', 'CNC Automation', 850000.00),
('Automated Welding Unit', 'Welding Automation', 620000.00),
('Industrial Assembly Robot', 'Robotic Automation', 950000.00);

INSERT INTO orders
(customer_id, machine_id, quantity, expected_delivery, status)
VALUES
(1, 1, 2, '2026-10-20', 'In Production'),
(2, 2, 1, '2026-10-10', 'Received'),
(3, 3, 3, '2026-11-05', 'Testing'),
(1, 2, 1, '2026-10-30', 'Ready');
