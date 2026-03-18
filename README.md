# Bus Ticket Booking System

## Overview

The **Bus Ticket Booking System** is a REST-based application that allows users to:

* Check seat availability between bus stops
* Calculate total trip price
* Reserve seats for passengers
* Manage seat reservations per travel segment

The application runs as a **standalone Java JAR application** and stores seat data **in memory** without using a database.

---

# System Requirements

Before running the application, ensure the following are installed:

* **Java 17 or higher**
* **curl / Postman** for API testing

Check Java installation:

```bash
java -version
```

---

# Running the Application

Navigate to the project directory where the JAR file exists and run:

```bash
java -jar target/bus-ticketing-system-1.0-SNAPSHOT-jar-with-dependencies.jar
```

After starting successfully, the application will run on:

```
http://localhost:8080
```

---

# API Base URL

```
http://localhost:8080/api/v1
```

---

# Testing the APIs

You can test APIs using:

* **Browser (for GET requests)**
* **curl**
* **Postman**
* **Any REST client**

---

# API 1 — Check Availability & Price

## Endpoint

```
GET /api/v1/trips/availability
```

## Purpose

Check seat availability and calculate the total price for a trip between a specified origin and destination based on the number of passengers.

---

## Example Request

```
http://localhost:8080/api/v1/tickets/availability?origin=A&destination=D&passengerCount=3
```

Example using curl:

```bash
curl "http://localhost:8080/api/v1/tickets/availability?origin=A&destination=D&passengerCount=3"
```

---

## Request Parameters

| Field          | Type    | Required | Example | Description               |
| -------------- | ------- | -------- | ------- | ------------------------- |
| origin         | String  | Yes      | A       | Starting location code    |
| destination    | String  | Yes      | D       | Destination location code |
| passengerCount | Integer | Yes      | 3       | Number of passengers      |

---

## Example Response

```json
{
  "availableSeats": [
    {
      "seatId": "2E",
      "row": "E",
      "column": 2,
      "seatSegments": {
        "A-B": {
          "status": "AVAILABLE",
          "reservationId": ""
        }
      }
    }
  ],
  "totalPrice": 450.0
}
```

---

## Response Fields

| Field          | Type    | Description                         |
| -------------- | ------- | ----------------------------------- |
| availableSeats | Array   | List of seats available for booking |
| seatId         | String  | Unique seat identifier              |
| row            | String  | Seat row identifier                 |
| column         | Integer | Seat column number                  |
| seatSegments   | Map     | Seat availability per route segment |
| status         | Enum    | AVAILABLE or RESERVED               |
| reservationId  | String  | Reservation identifier if booked    |
| totalPrice     | Number  | Total calculated price              |

---

## Status Codes

| Status Code | Description                 |
| ----------- | --------------------------- |
| 200         | Request successful          |
| 400         | Invalid request parameters  |
| 401         | Unauthorized                |
| 404         | Route or endpoint not found |
| 409         | Duplicate request           |
| 500         | Internal server error       |

---

# API 2 — Reserve Tickets

## Endpoint

```
POST /api/v1/tickets/reserve
```

---

## Purpose

Reserve tickets for passengers by booking available seats and returning a confirmation with ticket details.

---

## Request Body

```json
{
  "origin": "A",
  "destination": "B",
  "passengerCount": 2,
  "priceConfirmation": 100.00
}
```

---

## Request Fields

| Field             | Type    | Required | Example | Description                        |
| ----------------- | ------- | -------- | ------- | ---------------------------------- |
| origin            | String  | Yes      | A       | Starting location code             |
| destination       | String  | Yes      | B       | Destination location               |
| passengerCount    | Integer | Yes      | 2       | Number of passengers               |
| priceConfirmation | Double  | Yes      | 100.00  | Confirms the calculated trip price |

If the provided price **matches the system-calculated price**, the reservation proceeds.

If the price **does not match**, the reservation is rejected.

---

## Example Request

```bash
curl -X POST http://localhost:8080/api/v1/tickets/reserve \
-H "Content-Type: application/json" \
-d '{
  "origin": "A",
  "destination": "B",
  "passengerCount": 2,
  "priceConfirmation": 100.00
}'
```

---

## Example Response

```json
{
  "ticketNumber": "TKT-107C70C6",
  "tripDetails": {
    "origin": "A",
    "destination": "B"
  },
  "assignedSeats": [
    {
      "seatId": "4A",
      "row": "A",
      "column": 4
    }
  ],
  "totalPrice": 100.0
}
```

---

## Response Fields

| Field         | Type    | Description                  |
| ------------- | ------- | ---------------------------- |
| ticketNumber  | String  | Unique ticket reservation ID |
| tripDetails   | Object  | Trip information             |
| assignedSeats | Array   | Seats assigned to passengers |
| seatId        | String  | Seat identifier              |
| row           | String  | Seat row                     |
| column        | Integer | Seat column                  |
| totalPrice    | Number  | Total reservation price      |

---

## Status Codes

| Status Code | Description                      |
| ----------- | -------------------------------- |
| 201         | Reservation created successfully |
| 400         | Invalid input                    |
| 401         | Unauthorized                     |
| 404         | Route or endpoint not found      |
| 409         | Duplicate request                |
| 500         | Internal server error            |

---

# Important Notes

* The system uses **in-memory storage**
* Restarting the application will **reset all reservations**
* Designed for **API demonstration and testing**

---

# Author

Manjitha Teshara
