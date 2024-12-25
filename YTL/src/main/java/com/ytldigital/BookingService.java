package com.ytldigital;

import java.util.ArrayList;

import com.ytldigital.error.EventCapacityReached;
import com.ytldigital.error.EventIdNotRegistered;
import com.ytldigital.error.FailedToConfirmBookingStatus;
import com.ytldigital.error.FailedToStartBookingProcess;
import com.ytldigital.error.PriorUnderBookingNotFound;
import com.ytldigital.error.PriorUnderBookingOrSuccessfulBookingFound;

class BookingService implements IBookingService {
    public Repo repo;

    public BookingService(Repo repo) {
        this.repo = repo;
    }

    /**
     * If the sum of tickets booked and tickets that are under booking for the event
     * with eventId is strictly less
     * than the capacity of the auditorium, start the booking process for the user
     * for this event.
     *
     * @param eventId The id of the event.
     * @param userId  The id of the user.
     *
     * @throws FailedToStartBookingProcess Thrown if the booking process cannot be
     *                                     started. The underlying cause could be
     *                                     {@link EventIdNotRegistered},
     *                                     {@link PriorUnderBookingOrSuccessfulBookingFound},
     *                                     or {@link EventCapacityReached}.
     */
    @Override
    public void startBookingProcess(int eventId, int userId) throws FailedToStartBookingProcess {
        AuditoriumWithParticipants auditoriumWithParticipants = this.repo.auditoriumsWithParticipants
                .get(String.valueOf(eventId));
        if (auditoriumWithParticipants == null) {
            throw new FailedToStartBookingProcess(new EventIdNotRegistered());
        }
        ArrayList<Participant> participants = auditoriumWithParticipants.getParticipants();
        if (participants == null) {
            participants = new ArrayList<Participant>();
            auditoriumWithParticipants.setParticipants(participants);
        }
        if (participants.size() + 1 <= auditoriumWithParticipants.getAuditorium().capacity()) {
            for (Participant participant : participants) {
                if (participant.getUserId() == userId) {
                    throw new FailedToStartBookingProcess(new PriorUnderBookingOrSuccessfulBookingFound());
                }
            }
            participants.add(new Participant(userId, false));
        } else {
            throw new FailedToStartBookingProcess(new EventCapacityReached());
        }

    }

    /**
     * Confirm the booking status of the given user for the given event. If
     * `bookingSuccessful` is `true`, the booking will be confirmed as successful,
     * otherwise, the booking will be removed to allow other users to book the seat.
     *
     * @param eventId           The id of the event.
     * @param userId            The id of the user.
     * @param bookingSuccessful If `true`, the booking will be confirmed as
     *                          successful, otherwise, the booking will be removed
     *                          to allow other users to book the seat.
     *
     * @throws FailedToConfirmBookingStatus Thrown if the booking status cannot be
     *                                      confirmed. The underlying cause could be
     *                                      {@link EventIdNotRegistered}, or
     *                                      {@link PriorUnderBookingNotFound}.
     */
    @Override
    public void confirmBookingStatus(int eventId, int userId, boolean bookingSuccessful)
            throws FailedToConfirmBookingStatus {
        AuditoriumWithParticipants auditoriumWithParticipants = this.repo.auditoriumsWithParticipants
                .get(String.valueOf(eventId));
        if (auditoriumWithParticipants == null) {
            throw new FailedToConfirmBookingStatus(new EventIdNotRegistered());
        }
        ArrayList<Participant> participants = auditoriumWithParticipants.getParticipants();
        if (participants == null) {
            throw new FailedToConfirmBookingStatus(new PriorUnderBookingNotFound());
        }
        for (Participant participant : participants) {
            if (participant.getUserId() == userId && participant.isBooked() == false) {
                if (bookingSuccessful) {
                    participant.setBooked(true);
                } else {
                    participants.remove(participant);
                }
                return;
            }
        }
        throw new FailedToConfirmBookingStatus(new PriorUnderBookingNotFound());
    }
}