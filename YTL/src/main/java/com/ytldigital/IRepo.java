package com.ytldigital;

import com.ytldigital.error.EventCapacityReached;
import com.ytldigital.error.EventIdNotRegistered;
import com.ytldigital.error.PriorUnderBookingNotFound;
import com.ytldigital.error.PriorUnderBookingOrSuccessfulBookingFound;

interface IRepo {
    /**
     * Create and store an Auditorium object from the parameters passed to this function.
     *
     * @param auditoriumNumber The number of the auditorium.
     * @param eventId The id of the event.
     * @param capacity The capacity of the auditorium.
     */
    void registerEventInAuditorium(int auditoriumNumber, int eventId, int capacity);

    /**
     * Return the Auditorium object that represents the auditorium where an event with eventId will occur.
     *
     * @param eventId The id of the event.
     *
     * @return The Auditorium object that represents the auditorium where the event will occur.
     *
     * @throws EventIdNotRegistered Thrown if the event is not registered.
     */
    Auditorium getAuditoriumDetailsForEvent(int eventId) throws EventIdNotRegistered;

    /**
     * Return the sum of tickets booked and tickets that are under booking for the event with eventId.
     *
     * @param eventId The id of the event.
     *
     * @return The sum of tickets booked and tickets that are under booking for the event with eventId.
     *
     * @throws EventIdNotRegistered Thrown if the event is not registered.
     */
    int getNumberOfSeatsBooksOrUnderBookingForEvent(int eventId) throws EventIdNotRegistered;

    /**
     * Store the information that the user is booking an event with event ID.
     *
     * @param eventId The id of the event.
     * @param userId The id of the user.
     *
     * @throws EventIdNotRegistered Thrown if the event is not registered.
     * @throws PriorUnderBookingOrSuccessfulBookingFound Thrown if the user is already booking or has successfully booked the event.
     * @throws EventCapacityReached Thrown if the event has reached its capacity.
     */
    void addUnderBooking(int eventId, int userId) throws EventIdNotRegistered, PriorUnderBookingOrSuccessfulBookingFound, EventCapacityReached;

    /**
     * Remove information that the user is booking an event with event ID.
     *
     * @param eventId The id of the event.
     * @param userId The id of the user.
     *
     * @throws EventIdNotRegistered Thrown if the event is not registered.
     * @throws PriorUnderBookingNotFound Thrown if the user has not started the booking process previously.
     */
    void removeUnderBooking(int eventId, int userId) throws EventIdNotRegistered, PriorUnderBookingNotFound;

    /**
     * Store information that the user has successfully booked an event with event ID.
     *
     * @param eventId The id of the event.
     * @param userId The id of the user.
     *
     * @throws EventIdNotRegistered Thrown if the event is not registered.
     * @throws PriorUnderBookingNotFound Thrown if the user has not started the booking process previously.
     */
    void addSuccessfulBooking(int eventId, int userId) throws EventIdNotRegistered, PriorUnderBookingNotFound;
}