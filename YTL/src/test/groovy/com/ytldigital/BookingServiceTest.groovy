package com.ytldigital

import com.ytldigital.error.EventCapacityReached
import com.ytldigital.error.EventIdNotRegistered
import com.ytldigital.error.FailedToConfirmBookingStatus
import com.ytldigital.error.FailedToStartBookingProcess
import com.ytldigital.error.PriorUnderBookingNotFound
import com.ytldigital.error.PriorUnderBookingOrSuccessfulBookingFound
import spock.lang.Specification

import java.util.stream.Stream

import static com.ytldigital.util.Utils.randomInt

class BookingServiceTest extends Specification {
    def "cannot start booking process if event id not registered"() {
        given: "we have 10 auditorium"
        def repo = new Repo()
        def auditoriumNumbers = (1..10)

        and: "some of them has events registered"
        def numRegisteredEvent = randomInt(auditoriumNumbers.size())
        def numUnregisteredEvent = 1

        def eventIds = Stream.generate { randomInt(1000) }
                .distinct()
                .limit(numRegisteredEvent + numUnregisteredEvent)
                .toList()

        def registeredEventIds = eventIds.take(numRegisteredEvent)
        def unregisteredEventId = eventIds.drop(numRegisteredEvent).first()

        def auditoriumNumbersShuffled = auditoriumNumbers.shuffled()

        if (numRegisteredEvent > 0) {
            (0..numRegisteredEvent - 1).each {
                def auditoriumNumber = auditoriumNumbersShuffled[it]
                def eventId = registeredEventIds[it]
                def capacity = randomInt(1, 250)
                repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
            }
        }

        when: "we try to start a booking process for an event id which is not registered"
        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)
        bookingService.startBookingProcess(unregisteredEventId, userId)

        then: "the booking process should fail"
        def e = thrown(FailedToStartBookingProcess)
        assert e.getCause() instanceof EventIdNotRegistered

        where:
        repeats << (1..10)
    }

    def "cannot start booking process if prior under-booking found"() {
        given: "a started booking process"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)
        bookingService.startBookingProcess(eventId, userId)

        when: "we try to start the same booking process"
        bookingService.startBookingProcess(eventId, userId)

        then: "the process should fail"
        def e = thrown(FailedToStartBookingProcess)
        assert e.getCause() instanceof PriorUnderBookingOrSuccessfulBookingFound

        where:
        repeats << (1..10)
    }

    def "cannot start booking process if prior successful booking found"() {
        given: "a confirmed successful booking"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)
        bookingService.startBookingProcess(eventId, userId)
        bookingService.confirmBookingStatus(eventId, userId, true)

        when: "we try to start the same booking process"
        bookingService.startBookingProcess(eventId, userId)

        then: "the process should fail"
        def e = thrown(FailedToStartBookingProcess)
        assert e.getCause() instanceof PriorUnderBookingOrSuccessfulBookingFound

        where:
        repeats << (1..10)
    }

    def "cannot start booking process an event has already reached its capacity"() {
        given: "a fully booked event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)

        def numUnderBooking = randomInt(capacity)

        def userIds = (1..capacity).shuffled()

        def userIdsUnderBooking = userIds.take(numUnderBooking)
        def userIdsConfirmedSuccessfulBooking = userIds.drop(numUnderBooking)

        if (!userIdsUnderBooking.isEmpty()) {
            userIdsUnderBooking.each { userId ->
                bookingService.startBookingProcess(eventId, userId)
            }
        }

        if (!userIdsConfirmedSuccessfulBooking.isEmpty()) {
            userIdsConfirmedSuccessfulBooking.each { userId ->
                bookingService.startBookingProcess(eventId, userId)
                bookingService.confirmBookingStatus(eventId, userId, true)
            }
        }

        when: "we try to start a booking process for the same event"
        def anotherUserId = capacity + 1
        bookingService.startBookingProcess(eventId, anotherUserId)

        then: "the process should fail"
        def e = thrown(FailedToStartBookingProcess)
        assert e.getCause() instanceof EventCapacityReached

        where:
        repeats << (1..10)
    }

    def "can start booking process when an event is registered, not fully booked, and the user has no prior booking for the event"() {
        given: "an registered event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)

        when: "a user start a booking process for the event"
        bookingService.startBookingProcess(eventId, userId)

        then: "the process should complete without error"
        noExceptionThrown()
    }

    def "can start booking process when a previous fully booked event has seats freed up after being confirmed as unsuccessful"() {
        given: "a fully booked event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)

        def numUnderBooking = randomInt(1, capacity) // minimum 1 under booking

        def userIds = (1..capacity).shuffled()

        def userIdsUnderBooking = userIds.take(numUnderBooking)
        def userIdsConfirmedSuccessfulBooking = userIds.drop(numUnderBooking)

        if (!userIdsUnderBooking.isEmpty()) {
            userIdsUnderBooking.each { userId ->
                bookingService.startBookingProcess(eventId, userId)
            }
        }

        if (!userIdsConfirmedSuccessfulBooking.isEmpty()) {
            userIdsConfirmedSuccessfulBooking.each { userId ->
                bookingService.startBookingProcess(eventId, userId)
                bookingService.confirmBookingStatus(eventId, userId, true)
            }
        }

        and: "one of the previously under-booking confirmed as unsuccessful"
        def userIdUnsuccessfulBooking = userIdsUnderBooking.first()
        bookingService.confirmBookingStatus(eventId, userIdUnsuccessfulBooking, false)

        when: "we try to start a booking process for the same event"
        def anotherUserId = capacity + 1
        bookingService.startBookingProcess(eventId, anotherUserId)

        then: "the process should complete without error"
        noExceptionThrown()

        where:
        repeats << (1..10)
    }

    def "cannot confirm booking if event id is not registered"() {
        given: "we have 10 auditorium"
        def repo = new Repo()
        def auditoriumNumbers = (1..1000)

        and: "some of them has events registered"
        def numRegisteredEvent = randomInt(auditoriumNumbers.size())
        def numUnregisteredEvent = 1

        def eventIds = Stream.generate { randomInt(1000) }
                .distinct()
                .limit(numRegisteredEvent + numUnregisteredEvent)
                .toList()

        def registeredEventIds = eventIds.take(numRegisteredEvent)
        def unregisteredEventId = eventIds.drop(numRegisteredEvent).first()

        def auditoriumNumbersShuffled = auditoriumNumbers.shuffled()

        if (numRegisteredEvent > 0) {
            (0..numRegisteredEvent - 1).each {
                def auditoriumNumber = auditoriumNumbersShuffled[it]
                def eventId = registeredEventIds[it]
                def capacity = randomInt(1, 250)
                repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
            }
        }

        when: "we try to confirm a booking process for an event id which is not registered"
        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)
        bookingService.confirmBookingStatus(unregisteredEventId, userId, true)

        then: "the booking confirmation process should fail"
        def e = thrown(FailedToConfirmBookingStatus)
        assert e.getCause() instanceof EventIdNotRegistered

        where:
        repeats << (1..10)
    }

    def "cannot confirm booking if no prior under booking found - the user hasn't start the process"() {
        given: "a registered event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)

        when: "try to confirm the booking status"
        bookingService.confirmBookingStatus(eventId, userId, bookingSuccessful)

        then: "the process should fail"
        def e = thrown(FailedToConfirmBookingStatus)
        assert e.getCause() instanceof PriorUnderBookingNotFound

        where:
        bookingSuccessful = randomInt(2) == 1
        repeats << (1..10)
    }

    def "cannot confirm booking if no prior under booking found - the user has already completed the process"() {
        given: "a registered event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)

        and: "the user already has a successful booking"
        bookingService.startBookingProcess(eventId, userId)
        bookingService.confirmBookingStatus(eventId, userId, true)

        when: "try to confirm the booking status"
        bookingService.confirmBookingStatus(eventId, userId, bookingSuccessful)

        then: "the process should fail"
        def e = thrown(FailedToConfirmBookingStatus)
        assert e.getCause() instanceof PriorUnderBookingNotFound

        where:
        bookingSuccessful = randomInt(2) == 1
        repeats << (1..10)
    }

    def "can confirm booking if an event id is registered, and the user has prior under booking"() {
        given: "a registered event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(100)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        def bookingService = new BookingService(repo)
        def userId = randomInt(1000)

        and: "the user has started the booking process"
        bookingService.startBookingProcess(eventId, userId)

        when: "try to confirm the booking status"
        bookingService.confirmBookingStatus(eventId, userId, bookingSuccessful)

        then: "the process should complete without error"
        noExceptionThrown()

        where:
        bookingSuccessful = randomInt(2) == 1
        repeats << (1..10)
    }
}