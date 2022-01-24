package life.qbic.portal.sampletracking.components.sampleoverview

import life.qbic.business.samples.Sample
import life.qbic.datamodel.samples.Status
import spock.lang.Specification

class SampleFilterImplSpec extends Specification {
  SampleFilter sampleFilter

  def "when the sample filter is configured for a status then samples with this status pass"() {
    when: "the sample filter is configured for a status"
    sampleFilter = new SampleFilterImpl()
    sampleFilter.withStatus("DATA_AVAILABLE")
    Sample sample = new Sample("QABCD001A0", "test sample", Status.DATA_AVAILABLE)

    then: "samples with this status pass"
    sampleFilter.asPredicate().test(sample)

  }

  def "when the sample filter is configured for a status then samples without this status fail"() {
    when: "the sample filter is configured for a status"
    sampleFilter = new SampleFilterImpl()
    sampleFilter.withStatus("DATA_AVAILABLE")
    Sample sample = new Sample("QABCD001A0", "test sample", Status.SAMPLE_RECEIVED)

    then: "samples without this status fail"
    !sampleFilter.asPredicate().test(sample)

  }
}
