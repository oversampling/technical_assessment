package com.ytldigital

import com.ytldigital.error.EventCapacityReached
import com.ytldigital.error.EventIdNotRegistered
import com.ytldigital.error.PriorUnderBookingNotFound
import com.ytldigital.error.PriorUnderBookingOrSuccessfulBookingFound
import spock.lang.Specification

import static com.ytldigital.util.Utils.randomInt

class RepoTest extends Specification {
    def "can create, store and retrieve auditorium"() {
        when: "we create an auditorium"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)

        then: "we can retrieve the auditorium and its details"
        def auditorium = repo.getAuditoriumDetailsForEvent(eventId)
        assert auditorium != null
        assert auditorium.auditoriumNumber() == auditoriumNumber
        assert auditorium.capacity() == capacity
        assert auditorium.eventId() == eventId

        and: "initially there are no seats booked or under booking"
        assert repo.getNumberOfSeatsBooksOrUnderBookingForEvent(eventId) == 0

        where:
        repeats << (1..10)
    }

    def "cannot retrieve non-existing event"() {
        when: "we try to retrieve a non-existing event"
        def repo = new Repo()
        def eventId = randomInt(1000)
        repo.getAuditoriumDetailsForEvent(eventId)

        then: "we get error"
        def e = thrown(EventIdNotRegistered)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot retrieve number of seats for non-existing event"() {
        when: "we try to retrieve number of seats for a non-existing event"
        def repo = new Repo()
        def eventId = randomInt(1000)
        repo.getNumberOfSeatsBooksOrUnderBookingForEvent(eventId)

        then: "we get error"
        def e = thrown(EventIdNotRegistered)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot add under-booking for non-existing event"() {
        when: "we try to add under-booking for a non-existing event"
        def repo = new Repo()
        def eventId = randomInt(1000)
        def userId = randomInt(100)
        repo.addUnderBooking(eventId, userId)

        then: "we get error"
        def e = thrown(EventIdNotRegistered)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot add under-booking if prior under-booking found"() {
        given: "a user already start the booking process for an event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
        def userId = randomInt(100)
        repo.addUnderBooking(eventId, userId)

        when: "the same user try to start the booking process again"
        repo.addUnderBooking(eventId, userId)

        then: "we get error"
        def e = thrown(PriorUnderBookingOrSuccessfulBookingFound)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot add under-booking if prior successful booking found"() {
        given: "a user already booked a seat for an event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
        def userId = randomInt(100)
        repo.addUnderBooking(eventId, userId)
        repo.addSuccessfulBooking(eventId, userId)

        when: "the same user try to start the booking process again"
        repo.addUnderBooking(eventId, userId)

        then: "we get error"
        def e = thrown(PriorUnderBookingOrSuccessfulBookingFound)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot add under-booking if the event capacity is reached"() {
        given: "an event is fully booked"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
        def successfulBookingCount = randomInt(0, capacity)
        def userIds = (1..capacity).toList()
        def successfulBookingUserIds = userIds.take(successfulBookingCount)
        def underBookingUserIds = userIds.drop(successfulBookingCount)
        successfulBookingUserIds.each { repo.addUnderBooking(eventId, it) }
        successfulBookingUserIds.each { repo.addSuccessfulBooking(eventId, it) }
        underBookingUserIds.each { repo.addUnderBooking(eventId, it) }

        when: "a new user try to start the booking process"
        def userId = capacity + 1
        repo.addUnderBooking(eventId, userId)

        then: "we get error"
        def e = thrown(EventCapacityReached)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot remove under-booking for non-existing event"() {
        when: "we try to remove under-booking for a non-existing event"
        def repo = new Repo()
        def eventId = randomInt(1000)
        def userId = randomInt(100)
        repo.removeUnderBooking(eventId, userId)

        then: "we get error"
        def e = thrown(EventIdNotRegistered)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot remove under-booking if no prior under-booking found"() {
        given: "no prior under-booking found"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
        def userId = randomInt(100)

        when: "we try to remove under-booking"
        repo.removeUnderBooking(eventId, userId)

        then: "we get error"
        def e = thrown(PriorUnderBookingNotFound)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "can remove under-booking if there is a prior under-booking found"() {
        given: "a user already start the booking process for an event"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
        def userId = randomInt(100)
        repo.addUnderBooking(eventId, userId)

        when: "the same user try to cancel the booking process"
        repo.removeUnderBooking(eventId, userId)

        then: "the under-booking should be removed without error"
        noExceptionThrown()
        assert repo.getNumberOfSeatsBooksOrUnderBookingForEvent(eventId) == 0

        where:
        repeats << (1..10)
    }

    def "remove under-booking will free up the seat"() {
        given: "an event is fully booked"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
        def successfulBookingCount = randomInt(0, capacity)
        def userIds = (1..capacity).toList()
        def successfulBookingUserIds = userIds.take(successfulBookingCount)
        def underBookingUserIds = userIds.drop(successfulBookingCount)
        successfulBookingUserIds.each { repo.addUnderBooking(eventId, it) }
        successfulBookingUserIds.each { repo.addSuccessfulBooking(eventId, it) }
        underBookingUserIds.each { repo.addUnderBooking(eventId, it) }
        assert repo.getNumberOfSeatsBooksOrUnderBookingForEvent(eventId) == capacity

        when: "a user cancel the booking process"
        def userId = underBookingUserIds.first()
        repo.removeUnderBooking(eventId, userId)

        then: "the seat should be freed up"
        noExceptionThrown()
        assert repo.getNumberOfSeatsBooksOrUnderBookingForEvent(eventId) == capacity - 1

        when: "another user start the booking process"
        def newUserId = underBookingUserIds.last() + 1
        repo.addUnderBooking(eventId, newUserId)

        then: "the seat should be booked"
        noExceptionThrown()
        assert repo.getNumberOfSeatsBooksOrUnderBookingForEvent(eventId) == capacity

        where:
        repeats << (1..10)
    }

    def "cannot add successful booking for non-existing event"() {
        when: "we try to add successful booking for a non-existing event"
        def repo = new Repo()
        def eventId = randomInt(1000)
        def userId = randomInt(100)
        repo.addSuccessfulBooking(eventId, userId)

        then: "we get error"
        def e = thrown(EventIdNotRegistered)
        assert e != null

        where:
        repeats << (1..10)
    }

    def "cannot add successful booking if there is no prior under-booking found"() {
        given: "no prior under-booking found"
        def repo = new Repo()
        def auditoriumNumber = randomInt(20)
        def eventId = randomInt(1000)
        def capacity = randomInt(1, 250)
        repo.registerEventInAuditorium(auditoriumNumber, eventId, capacity)
        def userId = randomInt(100)

        when: "we try to add successful booking"
        repo.addSuccessfulBooking(eventId, userId)

        then: "we get error"
        def e = thrown(PriorUnderBookingNotFound)
        assert e != null

        where:
        repeats << (1..10)
    }
}