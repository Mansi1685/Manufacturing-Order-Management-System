const orders = [
    {
        id: 1,
        customer: "TechWerk GmbH",
        country: "Germany",
        machine: "CNC Precision Machine",
        quantity: 2,
        status: "In Production",
        delivery: "20 Oct 2026"
    },
    {
        id: 2,
        customer: "AutoMech Solutions",
        country: "United Kingdom",
        machine: "Automated Welding Unit",
        quantity: 1,
        status: "Received",
        delivery: "10 Oct 2026"
    },
    {
        id: 3,
        customer: "Indus Automation",
        country: "India",
        machine: "Industrial Assembly Robot",
        quantity: 3,
        status: "Testing",
        delivery: "5 Nov 2026"
    },
    {
        id: 4,
        customer: "TechWerk GmbH",
        country: "Germany",
        machine: "Automated Welding Unit",
        quantity: 1,
        status: "Ready",
        delivery: "30 Oct 2026"
    }
];


function updateDashboard() {

    document.getElementById("totalOrders").textContent = orders.length;

    document.getElementById("receivedOrders").textContent =
        orders.filter(order => order.status === "Received").length;

    document.getElementById("productionOrders").textContent =
        orders.filter(order => order.status === "In Production").length;

    document.getElementById("testingOrders").textContent =
        orders.filter(order => order.status === "Testing").length;

    document.getElementById("readyOrders").textContent =
        orders.filter(order => order.status === "Ready").length;
}


updateDashboard();
