package life.qbic.business.samples.info

import life.qbic.business.samples.download.DownloadSamplesDataSource
import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

/**
 * <b>Tests the download samples use case</b>
 *
 * @since 1.0.0
 */
class GetSamplesInfoSpec extends Specification {
  
    def "successful execution of the use case lead to success notifications"() {
        
        given:
        DownloadSamplesDataSource sampleDataSource = Stub()
        GetSamplesInfoDataSource infoDataSource = Stub()
        String projectCode = "QABCD"
        List<String> codes = ["QABCD001AB", "QABCD002AC", "QABCD005AX", "QABCD019A2"]
        sampleDataSource.fetchSampleCodesFor(projectCode, Status.SAMPLE_QC_FAIL) >> { new ArrayList<String>() }
        infoDataSource.fetchSampleNamesFor(codes) >> { new HashMap<String,String>() }
        GetSamplesInfoOutput output = Mock()
        GetSamplesInfo getInfos = new GetSamplesInfo(sampleDataSource, infoDataSource, output)
        
        when:"the use case is run"
        getInfos.requestSampleInfosFor(projectCode, Status.SAMPLE_QC_FAIL)
        
        then:"a successful message is send"
        1 * output.samplesWithNames(projectCode, Status.SAMPLE_QC_FAIL, _ as Map<String, String>)
        0 * output.failedExecution(_ as String)
    }

    
    def "unsuccessful execution of the use case lead to failure notifications"() {
        
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
        
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.samplesWithNames(_)
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
