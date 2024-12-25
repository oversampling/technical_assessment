package com.ytldigital;

import com.ytldigital.error.EventCapacityReached;
import com.ytldigital.error.EventIdNotRegistered;
import com.ytldigital.error.FailedToConfirmBookingStatus;
import com.ytldigital.error.FailedToStartBookingProcess;
import com.ytldigital.error.PriorUnderBookingNotFound;
import com.ytldigital.error.PriorUnderBookingOrSuccessfulBookingFound;

interface IBookingService {
    /**
     * If the sum of tickets booked and tickets that are under booking for the event with eventId is strictly less
     * than the capacity of the auditorium, start the booking process for the user for this event.
     *
     * @param eventId The id of the event.
     * @param userId The id of the user.
     *
     * @throws FailedToStartBookingProcess Thrown if the booking process cannot be started. The underlying cause could be {@link EventIdNotRegistered}, {@link PriorUnderBookingOrSuccessfulBookingFound}, or {@link EventCapacityReached}.
     */
    void startBookingProcess(int eventId, int userId) throws FailedToStartBookingProcess;

    /**
     * Confirm the booking status of the given user for the given event. If `bookingSuccessful` is `true`, the booking will be confirmed as successful, otherwise, the booking will be removed to allow other users to book the seat.
     *
     * @param eventId The id of the event.
     * @param userId The id of the user.
     * @param bookingSuccessful If `true`, the booking will be confirmed as successful, otherwise, the booking will be removed to allow other users to book the seat.
     *
     * @throws FailedToConfirmBookingStatus Thrown if the booking status cannot be confirmed. The underlying cause could be {@link EventIdNotRegistered}, or {@link PriorUnderBookingNotFound}.
     */
    void confirmBookingStatus(int eventId, int userId, boolean bookingSuccessful) throws FailedToConfirmBookingStatus;
}