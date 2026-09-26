async function loadOrders() {

    try {

        const response =
            await fetch("http://localhost:8080/api/orders");

        const orders =
            await response.json();


        // Dashboard

        document.getElementById("totalOrders").textContent =
            orders.length;

        document.getElementById("receivedOrders").textContent =
            orders.filter(order => order.status === "Received").length;

        document.getElementById("productionOrders").textContent =
            orders.filter(order => order.status === "In Production").length;

        document.getElementById("testingOrders").textContent =
            orders.filter(order => order.status === "Testing").length;

        document.getElementById("readyOrders").textContent =
            orders.filter(order => order.status === "Ready").length;


        // Display Orders

        function displayOrders(orderList) {

            const tbody =
                document.querySelector("tbody");

            tbody.innerHTML = "";


            orderList.forEach(order => {

                const row =
                    document.createElement("tr");


                row.innerHTML = `

                    <td>${order.orderId}</td>

                    <td>${order.customer}</td>

                    <td>${order.country}</td>

                    <td>${order.machine}</td>

                    <td>${order.quantity}</td>

                    <td>

                        <select class="status-select">

                            <option value="Received"
                                ${order.status === "Received" ? "selected" : ""}>
                                Received
                            </option>

                            <option value="In Production"
                                ${order.status === "In Production" ? "selected" : ""}>
                                In Production
                            </option>

                            <option value="Testing"
                                ${order.status === "Testing" ? "selected" : ""}>
                                Testing
                            </option>

                            <option value="Ready"
                                ${order.status === "Ready" ? "selected" : ""}>
                                Ready
                            </option>

                            <option value="Dispatched"
                                ${order.status === "Dispatched" ? "selected" : ""}>
                                Dispatched
                            </option>

                        </select>

                    </td>

                    <td>${order.delivery}</td>

                `;


                // Status Update

                const statusSelect =
                    row.querySelector(".status-select");


                statusSelect.addEventListener(
                    "change",
                    async function () {

                        const newStatus =
                            this.value;


                        try {

                            const response =
                                await fetch(
                                    `http://localhost:8080/api/orders/status/${order.orderId}`,
                                    {
                                        method: "PUT",

                                        headers: {
                                            "Content-Type":
                                                "application/json"
                                        },

                                        body:
                                            JSON.stringify({
                                                status: newStatus
                                            })
                                    }
                                );


                            if (!response.ok) {
                                throw new Error(
                                    "Status update failed"
                                );
                            }


                            await loadOrders();


                        } catch (error) {

                            console.error(
                                "Failed to update status:",
                                error
                            );

                        }

                    }
                );


                tbody.appendChild(row);

            });

        }


        // Display all orders

        displayOrders(orders);


        // SEARCH BUTTON

        const searchButton =
            document.getElementById("searchButton");

        const searchBox =
            document.getElementById("orderSearch");


        if (searchButton && searchBox) {

            searchButton.addEventListener(
                "click",
                function () {

                    const searchText =
                        searchBox.value
                            .toLowerCase()
                            .trim();


                    const filteredOrders =
                        orders.filter(order =>

                            String(order.orderId)
                                .includes(searchText)

                            ||

                            order.customer
                                .toLowerCase()
                                .includes(searchText)

                            ||

                            order.country
                                .toLowerCase()
                                .includes(searchText)

                            ||

                            order.machine
                                .toLowerCase()
                                .includes(searchText)

                            ||

                            order.status
                                .toLowerCase()
                                .includes(searchText)

                        );


                    displayOrders(filteredOrders);

                }
            );

        }


    } catch (error) {

        console.error(
            "Failed to load orders:",
            error
        );

    }

}


// ADD NEW ORDER

const orderForm =
    document.getElementById("orderForm");


if (orderForm) {

    orderForm.addEventListener(
        "submit",
        async function (event) {

            event.preventDefault();


            const customerId =
                document.getElementById("customerId").value;

            const machineId =
                document.getElementById("machineId").value;

            const quantity =
                document.getElementById("quantity").value;

            const delivery =
                document.getElementById("delivery").value;


            try {

                const response =
                    await fetch(
                        "http://localhost:8080/api/orders/add",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body:
                                JSON.stringify({
                                    customerId: customerId,
                                    machineId: machineId,
                                    quantity: quantity,
                                    delivery: delivery
                                })
                        }
                    );


                if (!response.ok) {
                    throw new Error(
                        "Failed to add order"
                    );
                }


                alert(
                    "Order added successfully!"
                );


                orderForm.reset();

                await loadOrders();


            } catch (error) {

                console.error(
                    "Failed to add order:",
                    error
                );

                alert(
                    "Failed to add order."
                );

            }

        }
    );

}


// Load Dashboard

loadOrders();