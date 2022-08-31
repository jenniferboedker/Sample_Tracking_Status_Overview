package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.datamodel.samples.Status
import life.qbic.portal.sampletracking.old.datasources.database.ConnectionProvider
import life.qbic.portal.sampletracking.old.datasources.samples.SamplesDbConnector
import life.qbic.portal.sampletracking.old.services.sample.SampleTracking
import life.qbic.portal.sampletracking.old.services.sample.SampleTrackingService
import spock.lang.Specification

import java.time.Instant


class SampleTrackingSpec extends Specification{

    def "when fetchSampleCodesFor project, then return a list of samples" () {
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        sampleTracking.requestProjectSamplesStatus(_) >> [
            new SampleTracking.TrackedSample("QABCD0215","DATA_AVAILABLE","2022-08-10 14:22:00"),
            new SampleTracking.TrackedSample("QABCD0216","DATA_AVAILABLE","2022-08-10 14:22:00"),
            new SampleTracking.TrackedSample("QABCD0217","DATA_AVAILABLE","2022-08-10 14:22:00"),
            new SampleTracking.TrackedSample("QABCD0218","SAMPLE_QC_FAIL","2022-08-10 14:22:00"),
            new SampleTracking.TrackedSample("QABCD0219","SAMPLE_QC_FAIL","2022-08-10 14:22:00"),
        ]

        when:
        List codes = connector.fetchSampleCodesFor("QABCD")

        then:
        codes.size() == 5
    }

    def "when fetchSampleCodesFor project, then return an empty list of no samples were found" () {
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        sampleTracking.requestProjectSamplesStatus(_) >> [ ]

        when:
        List codes = connector.fetchSampleCodesFor("QABCD")

        then:
        codes.size() == 0
    }


    def "when fetchSampleCodesFor project and status, then return a list of samples" () {
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        sampleTracking.requestProjectSamplesStatus(_) >> [
                new SampleTracking.TrackedSample("QABCD0215","DATA_AVAILABLE","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0216","DATA_AVAILABLE","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0217","DATA_AVAILABLE","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0218","SAMPLE_QC_FAIL","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0219","SAMPLE_QC_FAIL","2022-08-10 14:22:00"),
        ]

        when:
        List codes = connector.fetchSampleCodesFor("QABCD", Status.SAMPLE_QC_FAIL)

        then:
        codes.size() == 2
    }

    def "when fetchSampleCodesFor project and status, then return an empty list of samples if none were found" () {
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        sampleTracking.requestProjectSamplesStatus(_) >> []

        when:
        List codes = connector.fetchSampleCodesFor("QABCD", Status.SAMPLE_QC_FAIL)

        then:
        codes.size() == 0
    }


    def "when fetchSampleStatusForProject with a project code a list of statuses is returned" () {
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        sampleTracking.requestProjectSamplesStatus(_) >> [
                new SampleTracking.TrackedSample("QABCD0215","DATA_AVAILABLE","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0216","DATA_AVAILABLE","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0217","DATA_AVAILABLE","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0218","SAMPLE_QC_FAIL","2022-08-10 14:22:00"),
                new SampleTracking.TrackedSample("QABCD0219","SAMPLE_QC_FAIL","2022-08-10 14:22:00"),
        ]

        when:
        List codes = connector.fetchSampleStatusesForProject("QABCD")

        then:
        codes.size() == 5
        codes.count({status -> status.toString() == "DATA_AVAILABLE"}) == 3
        codes.count({status -> status.toString() == "SAMPLE_QC_FAIL"}) == 2
    }

    def "when fetchSampleStatusForProject with a project code and no samples were found an empty list is returned" () {
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        sampleTracking.requestProjectSamplesStatus(_) >> []

        when:
        List codes = connector.fetchSampleStatusesForProject("QABCD")

        then:
        codes.size() == 0
    }

    def "when getLatestChanges for a project, then the time point of the latest change for this project is returned" () {
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        sampleTracking.requestProjectSamplesStatus(_) >> [
                new SampleTracking.TrackedSample("QABCD0215","DATA_AVAILABLE","2022-08-10T10:37:30.00Z"),
                new SampleTracking.TrackedSample("QABCD0216","DATA_AVAILABLE","2022-08-10T10:35:30.00Z"),
                new SampleTracking.TrackedSample("QABCD0217","DATA_AVAILABLE","2022-08-10T09:37:30.00Z"),
                new SampleTracking.TrackedSample("QABCD0218","SAMPLE_QC_FAIL","2022-08-09T10:37:30.00Z"),
                new SampleTracking.TrackedSample("QABCD0219","SAMPLE_QC_FAIL","2021-08-10T10:37:30.00Z"),
        ]

        when:
        Instant time = connector.getLatestChange("QABCD")

        then:
        time == Instant.parse("2022-08-10T10:37:30.00Z")
    }

    def "when fetchSampleStatusesFor a collection of sample codes then a map of sample to status is returned"(){
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        List sampleCodes = ["QABCD0215", "QABCD0216", "QABCD0217", "QABCD0218"]

        and:
        sampleTracking.requestSampleStatus("QABCD0215") >> Optional.of(new SampleTracking.TrackedSample("QABCD0215","DATA_AVAILABLE","2022-08-10T10:37:30.00Z"))

        sampleTracking.requestSampleStatus("QABCD0216") >> Optional.of(new SampleTracking.TrackedSample("QABCD0216","SAMPLE_QC_FAIL","2022-08-10T10:37:30.00Z"))

        sampleTracking.requestSampleStatus("QABCD0218") >> Optional.of(new SampleTracking.TrackedSample("QABCD0218","SAMPLE_QC_PASS","2022-08-10T10:37:30.00Z"))

        sampleTracking.requestSampleStatus("QABCD0217") >> Optional.of(new SampleTracking.TrackedSample("QABCD0217","SAMPLE_RECEIVED","2022-08-10T10:37:30.00Z"))

        when:
        Map sampleToStatus = connector.fetchSampleStatusesFor(sampleCodes)

        then:
        sampleToStatus.size() == 4
        sampleToStatus.get("QABCD0215") == Status.DATA_AVAILABLE
        sampleToStatus.get("QABCD0216") == Status.SAMPLE_QC_FAIL
        sampleToStatus.get("QABCD0218") == Status.SAMPLE_QC_PASS
        sampleToStatus.get("QABCD0217") == Status.SAMPLE_RECEIVED
    }

    def "when fetchSampleStatusesFor a collection of sample codes and a sample was not found its not part of the map"(){
        given:
        ConnectionProvider connectionProvider = Mock()
        SampleTrackingService sampleTracking = Mock()

        SamplesDbConnector connector = new SamplesDbConnector(connectionProvider,sampleTracking)

        and:
        List sampleCodes = ["QABCD0215", "QABCD0216", "QABCD0217", "QABCD0218"]

        and:
        sampleTracking.requestSampleStatus("QABCD0215") >> Optional.of(new SampleTracking.TrackedSample("QABCD0215","DATA_AVAILABLE","2022-08-10T10:37:30.00Z"))

        sampleTracking.requestSampleStatus("QABCD0216") >> Optional.of(new SampleTracking.TrackedSample("QABCD0216","SAMPLE_QC_FAIL","2022-08-10T10:37:30.00Z"))

        sampleTracking.requestSampleStatus("QABCD0218") >> Optional.of(new SampleTracking.TrackedSample("QABCD0218","SAMPLE_QC_PASS","2022-08-10T10:37:30.00Z"))

        sampleTracking.requestSampleStatus("QABCD0217") >> Optional.empty()

        when:
        Map sampleToStatus = connector.fetchSampleStatusesFor(sampleCodes)

        then:
        sampleToStatus.size() == 3
        sampleToStatus.get("QABCD0215") == Status.DATA_AVAILABLE
        sampleToStatus.get("QABCD0216") == Status.SAMPLE_QC_FAIL
        sampleToStatus.get("QABCD0218") == Status.SAMPLE_QC_PASS
    }
}
