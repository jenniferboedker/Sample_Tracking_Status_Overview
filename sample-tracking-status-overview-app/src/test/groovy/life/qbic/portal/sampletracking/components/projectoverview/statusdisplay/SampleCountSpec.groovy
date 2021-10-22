package life.qbic.portal.sampletracking.components.projectoverview.statusdisplay

import spock.lang.Specification

/**
 * <b>Tests for the SampleCount object</b>
 *
 * @since 1.0.0
 */
class SampleCountSpec extends Specification{

    def "Equal SampleCounts are equal"(){
        expect: "when comparing two objects with the same content they are equal"
        samplecount1 == samplecount2

        where: "two sample count objects with the same content"
        samplecount1 | samplecount2
        new SampleCount(1,1,1) |  new SampleCount(1,1,1)
        new SampleCount(0,1,1) |  new SampleCount(0,1,1)
        new SampleCount(1,0,1) |  new SampleCount(1,0,1)
        new SampleCount(1,1,0) |  new SampleCount(1,1,0)
    }

    def "Different SampleCounts are not equal"(){
        expect: "when comparing two different objects are not equal"
        samplecount1 != samplecount2

        where: "two sample count objects with the different content"
        samplecount1 | samplecount2
        new SampleCount(1,1,1) |  new SampleCount(1,0,1)
        new SampleCount(1,1,1) |  new SampleCount(1,1,0)
        new SampleCount(1,1,1) |  new SampleCount(0,1,1)
    }
}