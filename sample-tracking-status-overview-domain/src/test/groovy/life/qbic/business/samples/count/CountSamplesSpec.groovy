package life.qbic.business.samples.count

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

/**
 * <b>Tests the load samples use case</b>
 *
 * @since 1.0.0
 */
class CountSamplesSpec extends Specification {


    def "successful execution of the use case lead to success notifications"() {
        given:
        CountSamplesDataSource dataSource = Stub()
        String projectCode = "QABCD"
        dataSource.fetchSampleStatusesForProject(projectCode) >> { new ArrayList<Status>() }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countSamplesPerStatus(projectCode)
        then:"a successful message is send"
        1 * output.countedSamples({ verifyAll(it, StatusCount) {it.projectCode == projectCode} })
        0 * output.failedExecution(_ as String)
    }
    
    def "amount of samples having reached AT LEAST received status is correctly returned and deprecated statuses are not counted towards received samples"() {
        given:
        String projectCode = "QABCD"
        CountSamplesDataSource dataSource = Stub()
        List<Status> statuses = [Status.DATA_AT_QBIC, Status.SAMPLE_RECEIVED, Status.METADATA_REGISTERED, Status.SAMPLE_RECEIVED]
        dataSource.fetchSampleStatusesForProject(projectCode) >> { statuses }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countSamplesPerStatus(projectCode)
        then:"the correct amounts of samples are returned"
        1 * output.countedSamples({
            verifyAll(it, StatusCount) {
                it.getProjectCode() == projectCode
                it.getSamplesReceived() == 2
                it.getSamplesInProject() == 4
            }
        })
        0 * output.failedExecution(_ as String)
    }

    def "amount of samples having reached exactly #status status is correctly returned"() {
        given:
        String projectCode = "QABCD"
        CountSamplesDataSource dataSource = Stub()
        List<Status> statuses = [Status.DATA_AT_QBIC, status]
        dataSource.fetchSampleStatusesForProject(projectCode) >> { statuses }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when: "the use case is run"
        countSamples.countSamplesPerStatus(projectCode)
        then: "the correct amounts of samples are returned"
        1 * output.countedSamples({
            verifyAll(it, StatusCount) {
                it.getProjectCode() == projectCode
                countSupplier(it) == 1
                it.getSamplesInProject() == 2
            }
        })
        0 * output.failedExecution(_ as String)
        where:
        status << [Status.SAMPLE_RECEIVED,
                   Status.SAMPLE_QC_FAIL,
                   Status.SAMPLE_QC_PASS,
                   Status.LIBRARY_PREP_FINISHED,
                   Status.DATA_AVAILABLE]
        countSupplier << [StatusCount::getSamplesReceived,
                          StatusCount::getSamplesQcFail,
                          StatusCount::getSamplesQcPass,
                          StatusCount::getLibraryPrepFinished,
                          StatusCount::getDataAvailable]
    }

    def "amount of samples having reached status #status or later is correctly returned"() {
        given:
        String projectCode = "QABCD"
        CountSamplesDataSource dataSource = Stub()
        List<Status> statuses = [Status.DATA_AT_QBIC] + laterStatuses
        dataSource.fetchSampleStatusesForProject(projectCode) >> { statuses }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when: "the use case is run"
        countSamples.countSamplesPerStatus(projectCode)
        then: "the correct amounts of samples are returned"
        1 * output.countedSamples({
            verifyAll(it, StatusCount) {
                it.getProjectCode() == projectCode
                countSupplier(it) == expectedCount
                it.getSamplesInProject() == expectedCount + 1
            }
        })
        0 * output.failedExecution(_ as String)
        where:
        status << [Status.SAMPLE_RECEIVED, Status.SAMPLE_QC_FAIL, Status.SAMPLE_QC_PASS, Status.LIBRARY_PREP_FINISHED, Status.DATA_AVAILABLE]
        laterStatuses << [
                [Status.SAMPLE_QC_FAIL, Status.SAMPLE_QC_PASS, Status.LIBRARY_PREP_FINISHED, Status.DATA_AVAILABLE], //later then SAMPLE_RECEIVED
                [], //later than SAMPLE_QC_FAIL
                [Status.LIBRARY_PREP_FINISHED, Status.DATA_AVAILABLE], //later than SAMPLE_QC_PASS
                [Status.DATA_AVAILABLE], //later than LIBRARY_PREP_FINISHED
                [] //later than DATA_AVAILABLE
        ]
        countSupplier << [
                StatusCount::getSamplesReceived,
                StatusCount::getSamplesQcFail ,
                StatusCount::getSamplesQcPass,
                StatusCount::getLibraryPrepFinished,
                StatusCount::getDataAvailable
        ]
        expectedCount << [
                4,
                0,
                2,
                1,
                0
        ]


    }

    
    def "unsuccessful execution of the use case lead to failure notifications"() {
        given:
        String projectCode = "QABCD"
        CountSamplesDataSource dataSource = Stub()
        dataSource.fetchSampleStatusesForProject(projectCode) >> {
            throw new RuntimeException("Testing runtime exceptions")
        }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countSamplesPerStatus(projectCode)
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.countedSamples(_)
    }

    def "a DataSourceException leads to a failure notification and no samples being loaded"() {
        given:
        String projectCode = "QABCD"
        CountSamplesDataSource dataSource = Stub()
        dataSource.fetchSampleStatusesForProject(projectCode) >> {
            throw new DataSourceException("Testing data source exception")
        }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)

        when:"the use case is run"
        countSamples.countSamplesPerStatus(projectCode)

        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.countedSamples(_)
    }
}
