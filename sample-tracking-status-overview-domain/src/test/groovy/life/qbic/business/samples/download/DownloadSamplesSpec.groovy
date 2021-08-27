package life.qbic.business.samples.download

import life.qbic.business.DataSourceException
import life.qbic.business.OutputException
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

/**
 * <b>Tests the download samples use case</b>
 *
 * @since 1.0.0
 */
class DownloadSamplesSpec extends Specification {
  
    def "successful execution of the use case lead to success notifications"() {
        given:
        DownloadSamplesDataSource dataSource = Stub()
        String projectCode = "QABCD"
        dataSource.fetchSampleCodesFor(projectCode, Status.DATA_AVAILABLE) >> { new ArrayList<String>() }
        DownloadSamplesOutput output = Mock()
        DownloadSamples downloadSamples = new DownloadSamples(dataSource, output)
        
        when:"the use case is run"
        downloadSamples.requestSampleCodesFor(projectCode)
        then:"a successful message is send"
        1 * output.foundDownloadableSamples(projectCode, _ as List<String>)
        0 * output.failedExecution(_ as String)
    }
    
    def "sample codes returned by data source are correctly forwarded"() {
        given:
        DownloadSamplesDataSource dataSource = Stub()
        List<String> codes = ["QABCD001AB", "QABCD002AC", "QABCD005AX", "QABCD019A2"]
        String projectCode = "QABCD"
        dataSource.fetchSampleCodesFor(projectCode, Status.DATA_AVAILABLE) >> { codes }
        DownloadSamplesOutput output = Mock()
        DownloadSamples downloadSamples = new DownloadSamples(dataSource, output)
        when:"the use case is run"
        downloadSamples.requestSampleCodesFor(projectCode)
        then:"the correct amounts of samples are returned"
        1 * output.foundDownloadableSamples(projectCode, codes)
        0 * output.failedExecution(_ as String)
    }

    def "unsuccessful execution of the use case lead to runtime exception"() {
        given:
        String projectCode = "QABCD"
        DownloadSamplesDataSource dataSource = Stub()
        dataSource.fetchSampleCodesFor(projectCode, Status.DATA_AVAILABLE) >> {
            throw new RuntimeException("Testing runtime exceptions")
        }
        DownloadSamplesOutput output = Mock()
        DownloadSamples downloadSamples = new DownloadSamples(dataSource, output)
        when:"the use case is run"
        downloadSamples.requestSampleCodesFor(projectCode)
        then:"a failure message is send"
        thrown(RuntimeException)
    }

    def "output failure leads to runtime exception being thrown"() {
        given:
        String projectCode = "QABCD"
        DownloadSamplesDataSource dataSource = Mock()
        DownloadSamplesOutput output = Stub()
        output.failedExecution(_ as String) >> { throw  new OutputException("Output failure does not work")}
        output.foundDownloadableSamples(_ as String, _ as List<String>) >> { throw  new OutputException("Output success does not work")}
        output.failedExecution(_ as String) >> {}
        DownloadSamples downloadSamples = new DownloadSamples(dataSource, output)
        when:"the use case is run"
        downloadSamples.requestSampleCodesFor(projectCode)
        then:"a runtime exception is thrown"
        thrown(RuntimeException)
    }

    def "a DataSourceException leads to a failure notification and no sample codes being loaded"() {
        given:
        String projectCode = "QABCD"
        DownloadSamplesDataSource dataSource = Stub()
        dataSource.fetchSampleCodesFor(projectCode, Status.DATA_AVAILABLE) >> {
            throw new DataSourceException("Testing data source exception")
        }
        DownloadSamplesOutput output = Mock()
        DownloadSamples downloadSamples = new DownloadSamples(dataSource, output)

        when:"the use case is run"
        downloadSamples.requestSampleCodesFor(projectCode)

        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.foundDownloadableSamples(_)
    }

}
