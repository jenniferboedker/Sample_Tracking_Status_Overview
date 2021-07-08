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
        countSamples.countSamples("QABCD")
        then:"a successful message is send"
        1 * output.countedSamples(_ as int, _ as int)
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
        countSamples.countSamples("QABCD")
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.countedSamples(_)
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
        countSamples.countSamples("QABCD")

        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.countedSamples(_)
    }
}
