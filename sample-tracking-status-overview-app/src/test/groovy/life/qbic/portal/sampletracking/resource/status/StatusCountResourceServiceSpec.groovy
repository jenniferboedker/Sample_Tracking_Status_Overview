package life.qbic.portal.sampletracking.resource.status

import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.Subscriber
import life.qbic.portal.sampletracking.communication.Topic
import spock.lang.Shared
import spock.lang.Specification

/**
 * <b>Tests that the status count resource service functions as expected</b>
 *
 * @since 1.0.0
 */
class StatusCountResourceServiceSpec extends Specification {

    StatusCountResourceService statusCountService = new StatusCountResourceService()
    Subscriber<StatusCount> subscriber1 = Mock()

    @Shared def knownStatuses = [Status.SAMPLE_RECEIVED].sort {a,b -> a.name() <=> b.name()}
    @Shared def unknownStatuses = Status.values().findAll {! (it in knownStatuses)}.sort {a,b -> a.name() <=> b.name()}

    def "Removing and adding a count for #status informs all subscribers to #topic"() {
        given: "a status count"
        StatusCount statusCount = generateRandomStatusCount(status)
        when: "subscribers subscribed to the service"
        statusCountService.subscribe(subscriber1, topic)
        and: "the count is added and removed from the resource"
        statusCountService.addToResource(statusCount)
        statusCountService.removeFromResource(statusCount)

        then: "all subscribers subscribed to the topic are informed"
        2 * subscriber1.receive(statusCount)

        where:
        topic | status
        Topic.SAMPLE_RECEIVED_COUNT_UPDATE | Status.SAMPLE_RECEIVED
    }

    def "Adding of a status count adds the count to the resource"() {
        given: "a status count"
        StatusCount statusCount = generateRandomStatusCount(status)
        when: "the status count is added to the resource service"
        statusCountService.addToResource(statusCount)
        then: "the status count is added to the resource"
        statusCountService.iterator().toList().contains(statusCount)
        where: "the status is allowed"
        status << knownStatuses
    }

    def "Removing of a status count removes the count from the resource"() {
        given: "a status count from the service"
        StatusCount statusCount = generateRandomStatusCount(status)
        statusCountService.addToResource(statusCount)
        and: "the adding functionality works"
        assert statusCountService.iterator().toList().contains(statusCount)
        when: "a count is removed from the resource service"
        statusCountService.removeFromResource(statusCount)
        then: "the count is removed to the resource"
        ! statusCountService.iterator().toList().contains(statusCount)
        where: "the status is allowed"
        status << knownStatuses
    }

    def "Adding of a status that is not allowed does not add it to the resource"() {
        given: "a status count"
        StatusCount statusCount = generateRandomStatusCount(status)
        List originalContent = statusCountService.iterator().toList()
        when: "the status count is added to the resource service"
        statusCountService.addToResource(statusCount)
        then: "the status count is not added to the resource"
        statusCountService.iterator().toList() == originalContent
        and: "an illegal argument exception is thrown"
        thrown(IllegalArgumentException)
        where: "the status is not allowed"
        status << unknownStatuses
    }

    def "Removing of a status that is not allowed throws an IllegalArgumentException"() {
        given: "a status count"
        StatusCount statusCount = generateRandomStatusCount(status)
        List originalContent = statusCountService.iterator().toList()
        when: "the status count is removed from the resource service"
        statusCountService.removeFromResource(statusCount)
        then: "the status count is not added to the resource"
        statusCountService.iterator().toList() == originalContent
        and: "an illegal argument exception is thrown"
        thrown(IllegalArgumentException)
        where: "the status is not allowed"
        status << unknownStatuses
    }


    static StatusCount generateRandomStatusCount() {
        Random random = new Random()
        int randomStatusOrdinal = random.nextInt(Status.values().size())
        Status status = Status.values()[randomStatusOrdinal]
        return generateRandomStatusCount(status)
    }

    static StatusCount generateRandomStatusCount(Status status) {
        Random random = new Random()
        int count = random.nextInt()
        String projectName = "TEST_NAME"
        return new StatusCount(projectName, status, count)
    }
}
