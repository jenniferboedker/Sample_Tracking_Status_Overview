package life.qbic.portal.sampletracking.resource.status

import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.communication.Subscriber
import life.qbic.portal.sampletracking.communication.Topic
import spock.lang.Specification

/**
 * <b>Tests that the status count resource service functions as expected</b>
 *
 * @since 1.0.0
 */
class StatusCountResourceServiceSpec extends Specification {

    StatusCountResourceService statusCountService = new StatusCountResourceService()
    Subscriber<StatusCount> subscriber1 = Mock()
    Subscriber<StatusCount> subscriber2 = Mock()

    def "Removing and adding a count for #status informs all subscribers"() {
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
        StatusCount statusCount = generateRandomStatusCount()
        when: "the status count is added to the resource service"
        statusCountService.addToResource(statusCount)
        then: "the status count is added to the resource"
        statusCountService.iterator().toList().contains(statusCount)
    }

    def "Removing of a status count removes the count from the resource"() {
        given: "a status count from the service"
        StatusCount statusCount = generateRandomStatusCount()
        statusCountService.addToResource(statusCount)
        and: "the adding functionality works"
        assert statusCountService.iterator().toList().contains(statusCount)
        when: "a count is removed from the resource service"
        statusCountService.removeFromResource(statusCount)
        then: "the count is removed to the resource"
        ! statusCountService.iterator().toList().contains(statusCount)
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
