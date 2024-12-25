# Show Booking

There is a theatre with multiple auditoriums, each with a fixed seating capacity. On a single day, one auditorium can host a single event.

Complete class `Repo` that implements interface `IRepo`, which stores the information about booking a seat.
- `void registerEventInAuditorium(int auditoriumNumber, int eventId, int capacity)`: Create and store an `Auditorium` object from the parameters passed to this function.
- `Auditorium getAuditoriumDetailsForEvent(int eventId)`: Return the Auditorium object that represents the auditorium where an event with eventId will occur. Throws `EventIdNotRegistered` if the event is not registered.
- `int getNumberOfSeatsBookedOrUnderBookingForEvent(int eventId)`: Return the sum of tickets booked and tickets that are under booking for the event with eventId. Throws `EventIdNotRegistered` if the event is not registered.
- `void addUnderBooking(int eventId, int userId)`: Store the information that the user with `userId` is booking an event with event ID. Throws `EventIdNotRegistered` if the event is not registered. Throws `PriorUnderBookingOrSuccessfulBookingFound` if the user is already booking or has successfully booked the event. Throws `EventCapacityReached` if the event has reached its capacity.
- `void removeUnderBooking(int eventId, int userId)`: Remove information that the user with `userId` is booking an event with event ID. Throws `EventIdNotRegistered` if the event is not registered. Throws `PriorUnderBookingNotFound` if the user has not started the booking process previously.
- `void addSuccessfulBooking(int eventId, int userId)`: Store information that the user with `userId` has successfully booked an event with event ID. Throws `EventIdNotRegistered` if the event is not registered. Throws `PriorUnderBookingNotFound` if the user has not started the booking process previously.

Complete class `BookingService` that implements interface `IBookingService`. An object of class `Repo` is passed to the constructor of this class. Use this object in `BookingService` to implement logic to manage booking tickets.
- `void startBookingProcess(int eventId, int userId)`: If the sum of tickets booked and tickets that are under booking for the event with eventId is strictly less than the capacity of the auditorium, start the booking process for the user for this event. Throws `FailedToStartBooking` if the booking process cannot be started - it could be due to (1) the event is not registered, (2) the event has reached its capacity, and (3) the user is already booking or has successfully booked the event.
- `void confirmBookingStatus(int eventId, int userId, boolean bookingSuccessful)`: Confirm the booking status of the given user for the given event. If `bookingSuccessful` is `true`, the booking will be confirmed as successful, otherwise, the booking will be removed to allow other users to book the seat. Throws `FailedToConfirmBookingStatus` if the booking status cannot be confirmed - it could be due to (1) the event is not registered, and (2) the user has not started the booking process previously.

Notes:
- (Tip) Look for the places which throws `new UnsupportedOperationException("Not implemented")` and replace them with your code.
- You are not allowed to modify interface `IRepo` and `IBookingService` and introduce new `public` methods in the pre-defined classes to preserve the API specification.
- It is okay to introduce new `private` methods and fields if needed.
- It is okay to introduce new classes, records, enums etc. if needed.
- You are required to fulfill all the test cases in `BookingServiceTest`. Do not modify the test cases.