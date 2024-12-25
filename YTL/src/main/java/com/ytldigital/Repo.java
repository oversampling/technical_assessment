package com.ytldigital;

import java.util.ArrayList;
import java.util.HashMap;

import com.ytldigital.error.EventCapacityReached;
import com.ytldigital.error.EventIdNotRegistered;
import com.ytldigital.error.PriorUnderBookingNotFound;
import com.ytldigital.error.PriorUnderBookingOrSuccessfulBookingFound;

class Participant {
    private int userId;
    private boolean isBooked;

    public Participant(int userId, boolean isBooked) {
        this.userId = userId;
        this.isBooked = isBooked;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}

class AuditoriumWithParticipants {
    private Auditorium auditorium;
    private ArrayList<Participant> participants;

    public AuditoriumWithParticipants(Auditorium auditorium, ArrayList<Participant> participants) {
        this.auditorium = auditorium;
        this.participants = participants;
    }

    public Auditorium getAuditorium() {
        return this.auditorium;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    public ArrayList<Participant> getParticipants() {
        return this.participants;
    }

    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
}

class Repo implements IRepo {

    public HashMap<String, AuditoriumWithParticipants> auditoriumsWithParticipants = new HashMap<String, AuditoriumWithParticipants>();

    /**
     * Create and store an Auditorium object from the parameters passed to this
     * function.
     *
     * @param auditoriumNumber The number of the auditorium.
     * @param eventId          The id of the event.
     * @param capacity         The capacity of the auditorium.
     */
    @Override
    public void registerEventInAuditorium(int auditoriumNumber, int eventId, int capacity) {
        // this function.
        Auditorium auditorium = new Auditorium(auditoriumNumber, eventId, capacity);
        AuditoriumWithParticipants auditoriumWithParticipants = new AuditoriumWithParticipants(auditorium,
                new ArrayList<Participant>());
        if (this.auditoriumsWithParticipants == null) {
            this.auditoriumsWithParticipants = new HashMap<String, AuditoriumWithParticipants>();
        }
        this.auditoriumsWithParticipants.put(String.valueOf(eventId), auditoriumWithParticipants);
        // throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return the Auditorium object that represents the auditorium where an event
     * with eventId will occur.
     *
     * @param eventId The id of the event.
     *
     * @return The Auditorium object that represents the auditorium where the event
     *         will occur.
     *
     * @throws EventIdNotRegistered Thrown if the event is not registered.
     */
    @Override
    public Auditorium getAuditoriumDetailsForEvent(int eventId) throws EventIdNotRegistered {
        AuditoriumWithParticipants auditoriumWithParticipants = this.auditoriumsWithParticipants
                .get(String.valueOf(eventId));

        if (auditoriumWithParticipants == null) {
            throw new EventIdNotRegistered();
        } else {
            if (auditoriumWithParticipants.getAuditorium() == null) {
                throw new EventIdNotRegistered();
            }
            // If the event is not registered, throw an EventIdNotRegistered exception.
            if (auditoriumWithParticipants.getAuditorium().eventId() != eventId) {
                throw new EventIdNotRegistered();
            }
        }

        return auditoriumWithParticipants.getAuditorium();

        // throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return the sum of tickets booked and tickets that are under booking for the
     * event with eventId.
     *
     * @param eventId The id of the event.
     *
     * @return The sum of tickets booked and tickets that are under booking for the
     *         event with eventId.
     *
     * @throws EventIdNotRegistered Thrown if the event is not registered.
     */
    @Override
    public int getNumberOfSeatsBooksOrUnderBookingForEvent(int eventId) throws EventIdNotRegistered {
        AuditoriumWithParticipants auditoriumWithParticipants = this.auditoriumsWithParticipants
                .get(String.valueOf(eventId));
        if (auditoriumWithParticipants == null) {
            throw new EventIdNotRegistered();
        }

        ArrayList<Participant> participants = auditoriumWithParticipants.getParticipants();
        if (participants == null) {
            return 0;
        }
        return participants.size();
    }

    /**
     * Store the information that the user is booking an event with event ID.
     *
     * @param eventId The id of the event.
     * @param userId  The id of the user.
     *
     * @throws EventIdNotRegistered                      Thrown if the event is not
     *                                                   registered.
     * @throws PriorUnderBookingOrSuccessfulBookingFound Thrown if the user is
     *                                                   already booking or has
     *                                                   successfully booked the
     *                                                   event.
     * @throws EventCapacityReached                      Thrown if the event has
     *                                                   reached its capacity.
     */
    @Override
    public void addUnderBooking(int eventId, int userId)
            throws EventIdNotRegistered, PriorUnderBookingOrSuccessfulBookingFound, EventCapacityReached {
        AuditoriumWithParticipants auditoriumWithParticipants = this.auditoriumsWithParticipants
                .get(String.valueOf(eventId));
        if (auditoriumWithParticipants == null) {
            throw new EventIdNotRegistered();
        }
        ArrayList<Participant> participants = auditoriumWithParticipants.getParticipants();
        if (participants == null) {
            participants = new ArrayList<Participant>();
        }
        if (participants.size() + 1 <= auditoriumWithParticipants.getAuditorium().capacity()) {
            for (Participant participant : participants) {
                if (participant.getUserId() == userId) {
                    throw new PriorUnderBookingOrSuccessfulBookingFound();
                }
            }
            participants.add(new Participant(userId, false));
        } else {
            throw new EventCapacityReached();
        }
    }

    /**
     * Remove information that the user is booking an event with event ID.
     *
     * @param eventId The id of the event.
     * @param userId  The id of the user.
     *
     * @throws EventIdNotRegistered      Thrown if the event is not registered.
     * @throws PriorUnderBookingNotFound Thrown if the user has not started the
     *                                   booking process previously.
     */
    @Override
    public void removeUnderBooking(int eventId, int userId) throws EventIdNotRegistered, PriorUnderBookingNotFound {
        AuditoriumWithParticipants auditoriumWithParticipants = this.auditoriumsWithParticipants
                .get(String.valueOf(eventId));
        if (auditoriumWithParticipants == null) {
            throw new EventIdNotRegistered();
        } else {
            ArrayList<Participant> participants = auditoriumWithParticipants.getParticipants();
            if (participants == null) {
                throw new PriorUnderBookingNotFound();
            }
            for (Participant participant : participants) {
                if (participant.getUserId() == userId && participant.isBooked() == false) {
                    participants.remove(participant);
                    return;
                }
            }
            throw new PriorUnderBookingNotFound();
        }
    }

    /**
     * Store information that the user has successfully booked an event with event
     * ID.
     *
     * @param eventId The id of the event.
     * @param userId  The id of the user.
     *
     * @throws EventIdNotRegistered      Thrown if the event is not registered.
     * @throws PriorUnderBookingNotFound Thrown if the user has not started the
     *                                   booking process previously.
     */
    @Override
    public void addSuccessfulBooking(int eventId, int userId) throws EventIdNotRegistered, PriorUnderBookingNotFound {
        AuditoriumWithParticipants auditoriumWithParticipants = this.auditoriumsWithParticipants
                .get(String.valueOf(eventId));
        if (auditoriumWithParticipants == null) {
            throw new EventIdNotRegistered();
        }
        ArrayList<Participant> participants = auditoriumWithParticipants.getParticipants();
        if (participants == null) {
            throw new PriorUnderBookingNotFound();
        }
        for (Participant participant : participants) {
            if (participant.getUserId() == userId && participant.isBooked() == false) {
                participant.setBooked(true);
                return;
            }
        }
        throw new PriorUnderBookingNotFound();
    }
}