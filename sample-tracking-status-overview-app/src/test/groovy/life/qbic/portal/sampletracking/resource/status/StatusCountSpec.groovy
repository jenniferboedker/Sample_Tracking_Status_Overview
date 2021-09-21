package life.qbic.portal.sampletracking.resource.status

import life.qbic.datamodel.samples.Status
import spock.lang.Specification

/**
 * <p>Tests the equals method for the StatusCount DTO</p>
 * @since 1.0.0
 */
class StatusCountSpec extends Specification {

    def "equal StatusCount objects are equal"() {
        given: "two status counts with the same information"
        StatusCount statusCount1 = new StatusCount(projectCode, status, count, total)
        StatusCount statusCount2 = new StatusCount(projectCode, status, count, total)

        expect: "the two are equal"
        statusCount1 == statusCount2

        where:
        projectCode | status | count | total
        "TEST" | Status.SAMPLE_RECEIVED | 1 | 6
    }

    def "different StatusCount objects are not equal"() {
        given: "two status counts with the same information"
        StatusCount statusCount1 = new StatusCount("TEST", Status.SAMPLE_RECEIVED, 0, 8)
        StatusCount statusCount2 = new StatusCount(projectCode, status, count, total)

        expect: "the two are not equal"
        statusCount1 != statusCount2

        where:
        projectCode | status | count | total
        "TEST" | Status.SAMPLE_RECEIVED | 1 | 8
        "TEST" | Status.DATA_AVAILABLE | 0 | 8
        "TEST_2" | Status.SAMPLE_RECEIVED | 0 | 8
    }
}
