async function loadOrders() {

    try {

        const response = await fetch("http://localhost:8080/api/orders");
        const orders = await response.json();

        document.getElementById("totalOrders").textContent = orders.length;

        document.getElementById("receivedOrders").textContent =
            orders.filter(order => order.status === "Received").length;

        document.getElementById("productionOrders").textContent =
            orders.filter(order => order.status === "In Production").length;

        document.getElementById("testingOrders").textContent =
            orders.filter(order => order.status === "Testing").length;

        document.getElementById("readyOrders").textContent =
            orders.filter(order => order.status === "Ready").length;


        const tableBody = document.querySelector("tbody");

        tableBody.innerHTML = "";


        orders.forEach(order => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${order.orderId}</td>
                <td>${order.customer}</td>
                <td>${order.country}</td>
                <td>${order.machine}</td>
                <td>${order.quantity}</td>

                <td>
                    <select class="status-select">
                        <option ${order.status === "Received" ? "selected" : ""}>
                            Received
                        </option>

                        <option ${order.status === "In Production" ? "selected" : ""}>
                            In Production
                        </option>

                        <option ${order.status === "Testing" ? "selected" : ""}>
                            Testing
                        </option>

                        <option ${order.status === "Ready" ? "selected" : ""}>
                            Ready
                        </option>

                        <option ${order.status === "Dispatched" ? "selected" : ""}>
                            Dispatched
                        </option>
                    </select>
                </td>

                <td>${order.delivery}</td>
            `;


            const statusSelect = row.querySelector(".status-select");


            statusSelect.addEventListener("change", async function () {

                const newStatus = this.value;

                await fetch(
                    `http://localhost:8080/api/orders/status/${order.orderId}`,
                    {
                        method: "PUT",

                        headers: {
                            "Content-Type": "application/json"
                        },

                        body: JSON.stringify({
                            status: newStatus
                        })
                    }
                );

                loadOrders();

            });


            tableBody.appendChild(row);

        });


    } catch (error) {

        console.error("Failed to load orders:", error);

    }

}


loadOrders();