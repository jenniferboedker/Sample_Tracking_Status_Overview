package life.qbic.portal.sampletracking.resource.status

import life.qbic.business.samples.count.StatusCount
import life.qbic.datamodel.samples.Status
import spock.lang.Shared
import spock.lang.Specification

/**
 * <b>Tests that the status count resource service functions as expected</b>
 *
 * @since 1.0.0
 */
class StatusCountResourceServiceSpec extends Specification {

    StatusCountResourceService statusCountService = new StatusCountResourceService()

    @Shared def knownStatuses = [Status.SAMPLE_RECEIVED, Status.SAMPLE_QC_PASS, Status.SAMPLE_QC_FAIL, Status.DATA_AVAILABLE, Status.LIBRARY_PREP_FINISHED].sort { a, b -> a.name() <=> b.name()}

    def "Adding of a status count adds the count to the resource"() {
        given: "a status count"
        StatusCount statusCount = getFakeStatusCount()
        when: "the status count is added to the resource service"
        statusCountService.addToResource(statusCount)
        then: "the status count is added to the resource"
        statusCountService.iterator().toList().contains(statusCount)
    }

    def "Removing of a status count removes the count from the resource"() {
        given: "a status count from the service"
        StatusCount statusCount = getFakeStatusCount()
        statusCountService.addToResource(statusCount)
        and: "the adding functionality works"
        assert statusCountService.iterator().toList().contains(statusCount)
        when: "a count is removed from the resource service"
        statusCountService.removeFromResource(statusCount)
        then: "the count is removed to the resource"
        ! statusCountService.iterator().toList().contains(statusCount)
    }

    def "Replacing a status count is unsupported"() {
        given: "a status count from the service"
        StatusCount statusCount = getFakeStatusCount()
        StatusCount replacement = getFakeStatusCount()
        statusCountService.addToResource(statusCount)
        and: "the adding functionality works"
        assert statusCountService.iterator().toList().contains(statusCount)
        when: "a count is replaced in the resource service"
        statusCountService.replace({ (it == statusCount) }, {return replacement})
        then: "the operation is not supported"
        thrown(UnsupportedOperationException)
    }


    static StatusCount getFakeStatusCount() {
        String projectName = "TEST_NAME"
        return new StatusCount(projectName, 1, 1, 1, 1,1 , 5)
    }
}
