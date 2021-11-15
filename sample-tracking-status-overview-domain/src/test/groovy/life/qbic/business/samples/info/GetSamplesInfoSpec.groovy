package life.qbic.business.samples.info

import life.qbic.business.DataSourceException
import life.qbic.business.samples.Sample
import life.qbic.business.samples.download.DownloadSamplesDataSource
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

/**
 * <b>Tests the download samples use case</b>
 *
 * @since 1.0.0
 */
class GetSamplesInfoSpec extends Specification {
  
    def "successful execution of the use case forwards the map of found samples"() {
        
        given:
        DownloadSamplesDataSource sampleDataSource = Stub()
        GetSamplesInfoDataSource infoDataSource = Stub()
        String projectCode = "QABCD"
        List<String> codes = ["QABCD001A0", "QABCD002A8", "QABCD005AW", "QABCD019A3"]
        Status expectedStatus = Status.SAMPLE_QC_FAIL
        Sample sampleOne = new Sample("QABCD001A0", "one", expectedStatus)
        Sample sampleTwo = new Sample("QABCD002A8", "two", expectedStatus)
        Sample sampleGreen = new Sample("QABCD005AW", "green", expectedStatus)
        Sample sampleBlue = new Sample("QABCD019A3", "blue", expectedStatus)

        Map<String, String> mapWithNames = new HashMap<>()
        mapWithNames.put("QABCD001A0", "one")
        mapWithNames.put("QABCD002A8", "two")
        mapWithNames.put("QABCD005AW", "green")
        mapWithNames.put("QABCD019A3", "blue")
        sampleDataSource.fetchSampleCodesFor(projectCode, expectedStatus) >> { codes }
        infoDataSource.fetchSampleNamesFor(codes) >> { mapWithNames }
        GetSamplesInfoOutput output = Mock()
        GetSamplesInfo getInfos = new GetSamplesInfo(sampleDataSource, infoDataSource, output)
        
        when:"the use case is run"
        getInfos.requestSampleInfosFor(projectCode, expectedStatus)
        
        then:"a successful message is send"
        1 * output.samplesWithNames(*_) >> { arguments ->
            final Collection<Sample> givenSamples = arguments[0]
            assert givenSamples.containsAll([sampleOne, sampleTwo, sampleGreen, sampleBlue])
        }
        0 * output.failedExecution(_ as String)
    }

    
    def "unsuccessful execution of the use case does not lead to failure notification, but forwards the exception"() {
        
        given:
        DownloadSamplesDataSource sampleDataSource = Stub()
        GetSamplesInfoDataSource infoDataSource = Stub()
        String projectCode = "QABCD"
        sampleDataSource.fetchSampleCodesFor(projectCode, Status.SAMPLE_QC_FAIL) >> { new ArrayList<String>() }
        infoDataSource.fetchSampleNamesFor(_) >> { 
            throw new RuntimeException("Testing runtime exceptions")
        }
        GetSamplesInfoOutput output = Mock()
        GetSamplesInfo getInfos = new GetSamplesInfo(sampleDataSource, infoDataSource, output)
        
        when:"the use case is run"
        getInfos.requestSampleInfosFor(projectCode, Status.SAMPLE_QC_FAIL)
        
        then:"exception is not caught"
        0 * output.failedExecution(_)
        0 * output.samplesWithNames(_)
        RuntimeException ex = thrown()
        ex.message == "Testing runtime exceptions"
    }
    
    def "a DataSourceException leads to a failure notification and no sample codes being loaded"() {
        
        given:
        DownloadSamplesDataSource sampleDataSource = Stub()
        GetSamplesInfoDataSource infoDataSource = Stub()
        String projectCode = "QABCD"
        sampleDataSource.fetchSampleCodesFor(projectCode, Status.SAMPLE_QC_FAIL) >> { new ArrayList<String>() }
        infoDataSource.fetchSampleNamesFor(_) >> {
            throw new DataSourceException("Testing data source exception")
        }
        GetSamplesInfoOutput output = Mock()
        GetSamplesInfo getInfos = new GetSamplesInfo(sampleDataSource, infoDataSource, output)
        
        when:"the use case is run"
        getInfos.requestSampleInfosFor(projectCode, Status.SAMPLE_QC_FAIL)
        
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.samplesWithNames(_)
    }

}
