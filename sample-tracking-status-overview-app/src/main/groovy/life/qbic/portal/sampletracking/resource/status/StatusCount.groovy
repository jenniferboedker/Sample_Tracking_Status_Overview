package life.qbic.portal.sampletracking.resource.status

import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.samples.Status

/**
 * <p>A DTO representing a status count for samples of a specific project</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class StatusCount {

    final String projectCode
    final Status status
    final int count
    final int totalSampleCount

    StatusCount(String projectCode, Status status, int count, int totalSampleCount) {
        this.projectCode = Objects.requireNonNull(projectCode, "The project id must not be null.")
        this.status = Objects.requireNonNull(status, "The sample status must not be null.")
        this.count = Objects.requireNonNull(count, "The count must not be null.")
        this.totalSampleCount = Objects.requireNonNull(totalSampleCount, "The total number of samples must not be null")
    }
}
