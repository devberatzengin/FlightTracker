# Flight Tracker API Documentation 📖

This document provides a detailed overview of the REST API endpoints for the Flight Tracker System. All responses are wrapped in a `RootEntity` object unless otherwise specified.

**Base URL**: `http://localhost:8080/rest/api`  
**Authentication**: Bearer Token (`Authorization: Bearer <token>`)

---

## 🔐 Authentication ([Auth]())

### Register
`POST /auth/register`
- **Description**: Creates a new passenger account.
- **Payload**: `UserDto.CreateRequest` (firstName, lastName, email, password, phoneNumber)

### Login
`POST /auth/login`
- **Description**: Authenticates user and returns a JWT.
- **Payload**: `AuthDto.Request` (email, password)
- **Response**: `AuthDto.Response` (token, email)

---

## 👤 User Management ([User]())

### Get My Profile
`GET /user/me` (Auth Required)
- **Description**: Returns the authenticated user's profile information.
- **Response**: `UserDto.Info`

### Admin: Create User
`POST /user/save` (Admin Required)
- **Payload**: `UserDto.CreateRequest`

### Admin: Activate/Deactivate
- `POST /user/activate`
- `POST /user/deactivate`
- **Auth**: Admin Required
- **Payload**: `UserDto.StatusChangeRequest` (email)

---

## ✈️ Flight Operations ([Flight]())

### List Flights
`GET /flight/list` (Auth Required)
- **Description**: Returns all flights.

### Get Flight By ID
`GET /flight/{id}` (Auth Required)

### Create Flight
`POST /flight/create` (Tower/Admin Only)
- **Payload**: `FlightDto.CreateRequest`

### Update Flight Status
`PUT /flight/update-status/{id}?status={STATUS}` (Tower/Admin Only)
- **Query Param**: `status` (ON_TIME, DELAYED, CANCELLED, etc.)

### Assign Captain
`PUT /flight/{flightId}/assign-captain/{captainId}` (Tower/Admin Only)

### Delete Flight
`DELETE /flight/delete/{id}` (Admin Only)

---

## 🏢 Airport & Aircraft ([Infrastructure]())

### Airport Management
- `GET /airport/list` (Auth Required)
- `GET /airport/{id}` (Auth Required)
- `POST /airport/create` (Admin Only, Payload: `AirportDto.Request`)
- `DELETE /airport/delete/{id}` (Admin Only)

### Aircraft Management
- `GET /aircraft/list` (Admin/Tower/Captain Required)
- `GET /aircraft/{id}` (Admin/Tower/Captain Required)
- `POST /aircraft/create` (Admin Only, Payload: `AircraftDto.Request`)
- `DELETE /aircraft/delete/{id}` (Admin Only)

---

## 🎫 Ticket & Booking ([Ticket]())

### Book Ticket
`POST /ticket/book` (Auth Required)
- **Description**: Books a seat on a flight and deducts balance.
- **Payload**: `TicketDto.BookingRequest` (flightId, seatNumber, paymentMethod)

### My Tickets
`GET /ticket/my-tickets` (Auth Required)

### Check-in
`POST /ticket/check-in/{pnrCode}` (Auth Required)
- **Description**: Confirms flight check-in via PNR.

### Seat Map
`GET /ticket/flight/{flightId}/seats` (Auth Required)
- **Description**: Returns seat occupancy for a specific flight.

### Cancel Ticket
`PUT /ticket/cancel/{id}` (Auth Required)

---

## 💳 SkyWallet ([Wallet]())

### Check Balance
`GET /wallet/balance` (Auth Required)
- **Response**: `BigDecimal` (Remaining balance)

### Add Funds
`POST /wallet/add-funds?amount={BigDecimal}` (Auth Required)
- **Description**: Increases the user's balance.

---

## 🔔 Notifications ([Notification]())

### List My Notifications
`GET /notifications` (Auth Required)
- **Description**: Returns all notifications for the authenticated user.

### Unread Count
`GET /notifications/unread-count` (Auth Required)
- **Response**: `Long` (Count of unread notifications)

### Mark as Read
`PUT /notifications/{id}/read` (Auth Required)
- **Response**: `Boolean` (Success status)

---

## 🌤️ Weather ([Weather]())

### Get City Weather
`GET /weather/{city}`
- **Description**: Returns a simplified weather object.
- **Response**: `{"city": "...", "condition": "...", "temp": "..."}`

---

## ⚠️ Error Responses

All errors follow this standard structure:
```json
{
  "exception": {
    "createTime": "...",
    "message": "Detailed error message",
    "path": "/api/..."
  },
  "status": 400
}
```
