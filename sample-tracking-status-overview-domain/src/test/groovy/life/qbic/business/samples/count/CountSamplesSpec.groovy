package life.qbic.business.samples.count

import life.qbic.business.DataSourceException
import spock.lang.Specification
import life.qbic.datamodel.samples.Status

/**
 * <b>Tests the load samples use case</b>
 *
 * @since 1.0.0
 */
class CountSamplesSpec extends Specification {


    def "successful execution of the use case lead to success notifications"() {
        given:
        CountSamplesDataSource dataSource = Stub()
        dataSource.fetchSampleStatusesForProject("QABCD") >> { new ArrayList<Status>() }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countReceivedSamples("QABCD")
        then:"a successful message is send"
        1 * output.countedReceivedSamples(_ as Integer, _ as Integer)
        0 * output.failedExecution(_ as String)
    }
    
    def "amount of samples having reached received status is correctly returned"() {
        given:
        CountSamplesDataSource dataSource = Stub()
        List<Status> statuses = [Status.SAMPLE_RECEIVED, Status.METADATA_REGISTERED, Status.SAMPLE_RECEIVED]
        dataSource.fetchSampleStatusesForProject("QABCD") >> { statuses }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countReceivedSamples("QABCD")
        then:"the correct amounts of samples are returned"
        1 * output.countedReceivedSamples(3, 2)
        0 * output.failedExecution(_ as String)
    }
    
    def "amount of samples having reached AT LEAST received status is correctly returned"() {
        given:
        CountSamplesDataSource dataSource = Stub()
        List<Status> statuses = [Status.DATA_AVAILABLE, Status.SAMPLE_RECEIVED, Status.METADATA_REGISTERED, Status.SAMPLE_RECEIVED]
        dataSource.fetchSampleStatusesForProject("QABCD") >> { statuses }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countReceivedSamples("QABCD")
        then:"the correct amounts of samples are returned"
        1 * output.countedReceivedSamples(4, 3)
        0 * output.failedExecution(_ as String)
    }
    
    def "amount of samples having reached AT LEAST received status is correctly returned and deprecated statuses are not counted towards received samples"() {
        given:
        CountSamplesDataSource dataSource = Stub()
        List<Status> statuses = [Status.DATA_AT_QBIC, Status.SAMPLE_RECEIVED, Status.METADATA_REGISTERED, Status.SAMPLE_RECEIVED]
        dataSource.fetchSampleStatusesForProject("QABCD") >> { statuses }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countReceivedSamples("QABCD")
        then:"the correct amounts of samples are returned"
        1 * output.countedReceivedSamples(4, 2)
        0 * output.failedExecution(_ as String)
    }
    
    def "unsuccessful execution of the use case lead to failure notifications"() {
        given:
        CountSamplesDataSource dataSource = Stub()
        dataSource.fetchSampleStatusesForProject("QABCD") >> {
            throw new RuntimeException("Testing runtime exceptions")
        }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)
        when:"the use case is run"
        countSamples.countReceivedSamples("QABCD")
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.countedReceivedSamples(_)
    }

    def "a DataSourceException leads to a failure notification and no samples being loaded"() {
        given:
        CountSamplesDataSource dataSource = Stub()
        dataSource.fetchSampleStatusesForProject("QABCD") >> {
            throw new DataSourceException("Testing data source exception")
        }
        CountSamplesOutput output = Mock()
        CountSamples countSamples = new CountSamples(dataSource, output)

        when:"the use case is run"
        countSamples.countReceivedSamples("QABCD")

        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.countedReceivedSamples(_)
    }
}
