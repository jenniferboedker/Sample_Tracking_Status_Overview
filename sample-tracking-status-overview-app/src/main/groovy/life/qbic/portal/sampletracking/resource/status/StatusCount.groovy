package life.qbic.portal.sampletracking.resource.status

import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.samples.Status

/**
 * <p>A DTO representing a status count for a specific project</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode(includeFields = true)
class StatusCount {

    final String projectId
    final Status status
    final int count

    StatusCount(String projectId, Status status, int count) {
        this.projectId = Objects.requireNonNull(projectId, "The project id must not be null.")
        this.status = Objects.requireNonNull(status, "The sample status must not be null.")
        this.count = Objects.requireNonNull(count, "The count must not be null.")
    }
}
