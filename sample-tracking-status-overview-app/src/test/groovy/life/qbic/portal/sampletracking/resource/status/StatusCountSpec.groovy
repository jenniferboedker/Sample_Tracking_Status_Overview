package life.qbic.portal.sampletracking.resource.status

import life.qbic.business.samples.count.StatusCount
import spock.lang.Specification

/**
 * <p>Tests the equals method for the StatusCount DTO</p>
 * @since 1.0.0
 */
class StatusCountSpec extends Specification {

    def "equal StatusCount objects are equal"() {
        given: "two status counts with the same information"
        StatusCount statusCount1 = new StatusCount("TEST" , 1 , 2 , 3 , 4 , 5 , 19)
        StatusCount statusCount2 = new StatusCount(projectCode, samplesReceived , samplesQcPass , samplesQcFail , libraryPrepFinished , dataAvailable , samplesInProject)

        expect: "the two are equal"
        statusCount1 == statusCount2

        where:
        projectCode | samplesReceived | samplesQcPass | samplesQcFail | libraryPrepFinished | dataAvailable | samplesInProject
        "TEST" | 1 | 2 | 3 | 4 | 5 | 19
    }

    def "different StatusCount objects are not equal"() {
        given: "two status counts with the same information"
        StatusCount statusCount1 = new StatusCount("TEST" , 1 , 2 , 3 , 4 , 5 , 19)
        StatusCount statusCount2 = new StatusCount(projectCode, samplesReceived , samplesQcPass , samplesQcFail , libraryPrepFinished , dataAvailable , samplesInProject)

        expect: "the two are not equal"
        statusCount1 != statusCount2

        where:
        projectCode | samplesReceived | samplesQcPass | samplesQcFail | libraryPrepFinished | dataAvailable | samplesInProject
        "    " | 1 | 2 | 3 | 4 | 5 | 19
        "TEST" | 0 | 2 | 3 | 4 | 5 | 19
        "TEST" | 1 | 0 | 3 | 4 | 5 | 19
        "TEST" | 1 | 2 | 0 | 4 | 5 | 19
        "TEST" | 1 | 2 | 3 | 0 | 5 | 19
        "TEST" | 1 | 2 | 3 | 4 | 0 | 19
        "TEST" | 1 | 2 | 3 | 4 | 5 | 0

    }
}
