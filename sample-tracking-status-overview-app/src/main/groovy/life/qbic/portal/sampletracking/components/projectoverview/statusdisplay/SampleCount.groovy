package life.qbic.portal.sampletracking.components.projectoverview.statusdisplay

import groovy.transform.EqualsAndHashCode

/**
 * <b>Stores the number of failing and passing sample counts for a status</b>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class SampleCount {

    int passingSamples
    int failingSamples
    int totalSampleCount

    void setPassingSamples(int passingSamples) {
        this.passingSamples = passingSamples
    }

    SampleCount(int passing, int failing, int totalCount){
        this.passingSamples = passing
        this.failingSamples = failing
        this.totalSampleCount = totalCount
    }


    @Override
    String toString() {
        return "$passingSamples / $totalSampleCount"
    }
}