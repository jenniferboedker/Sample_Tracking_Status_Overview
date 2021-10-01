package life.qbic.portal.sampletracking.components.projectoverview

import groovy.transform.EqualsAndHashCode

/**
 * <b>Stores the number of failing and passing sample counts for a status</b>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class SampleCount {

    int passingSamples = 0
    int failingSamples = 0

    SampleCount(int passing, int failing){
        passingSamples = passing
        failingSamples = failing
    }
}