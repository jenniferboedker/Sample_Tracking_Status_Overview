package life.qbic.business.samples.load

import life.qbic.business.DataSourceException
import spock.lang.Specification

/**
 * <b>Tests the load samples use case</b>
 *
 * @since 1.0.0
 */
class LoadSamplesSpec extends Specification {


    def "successful execution of the use case lead to success notifications"() {
        given:
        LoadSamplesDataSource dataSource = Stub()
        dataSource.fetchSamplesWithCurrentStatus("QABCD") >> { new ArrayList<String>() }
        LoadSamplesOutput output = Mock()
        LoadSamples loadSamples = new LoadSamples(dataSource, output)
        when:"the use case is run"
        loadSamples.loadSamples("QABCD")
        then:"a successful message is send"
        1 * output.loadedSamples(_ as List<String>)
        0 * output.failedExecution(_ as String)
    }

    def "unsuccessful execution of the use case lead to failure notifications"() {
        given:
        LoadSamplesDataSource dataSource = Stub()
        dataSource.fetchSamplesWithCurrentStatus("QABCD") >> {
            throw new RuntimeException("Testing runtime exceptions")
        }
        LoadSamplesOutput output = Mock()
        LoadSamples loadSamples = new LoadSamples(dataSource, output)
        when:"the use case is run"
        loadSamples.loadSamples("QABCD")
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.loadedSamples(_)
    }

    def "a DataSourceException leads to a failure notification and no samples being loaded"() {
        given:
        LoadSamplesDataSource dataSource = Stub()
        dataSource.fetchSamplesWithCurrentStatus("QABCD") >> {
            throw new DataSourceException("Testing data source exception")
        }
        LoadSamplesOutput output = Mock()
        LoadSamples loadSamples = new LoadSamples(dataSource, output)

        when:"the use case is run"
        loadSamples.loadSamples("QABCD")

        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.loadedSamples(_)
    }
}
