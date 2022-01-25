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

  def "when the sample filter status configuration is changed then only samples matching the changed status pass"() {
    when: "the sample filter status configuration is changed"
    sampleFilter = new SampleFilterImpl()
    sampleFilter.withStatus("DATA_AVAILABLE")
    sampleFilter.withStatus("SAMPLE_RECEIVED")
    Sample sampleWithCond1 = new Sample("QABCD001A0", "test sample", Status.DATA_AVAILABLE)
    Sample sampleWithCond2 = new Sample("QABCD001A0", "test sample", Status.SAMPLE_RECEIVED)

    then: "only samples matching the changed status pass"
    !sampleFilter.asPredicate().test(sampleWithCond1)
    sampleFilter.asPredicate().test(sampleWithCond2)
  }

  def "when the sample filter is configured for a status then samples without this status fail"() {
    when: "the sample filter is configured for a status"
    sampleFilter = new SampleFilterImpl()
    sampleFilter.withStatus("DATA_AVAILABLE")
    Sample sample = new Sample("QABCD001A0", "test sample", Status.SAMPLE_RECEIVED)

    then: "samples without this status fail"
    !sampleFilter.asPredicate().test(sample)

  }

  def "when the SampleFilter was not configured then all samples pass"() {
    when: "the SampleFilter was not configured"
    sampleFilter = new SampleFilterImpl()
    then: "all samples pass"
    sampleFilter.asPredicate().test(sample)
    where:
    status << Status.values()
    sample = new Sample("QABCD001A0", "test sample", status as Status)
  }
}
