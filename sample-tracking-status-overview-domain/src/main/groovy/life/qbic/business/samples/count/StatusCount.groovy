package life.qbic.business.samples.count

import groovy.transform.EqualsAndHashCode

/**
 * <p>A DTO representing a status count for samples of a specific project</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class StatusCount {

    final String projectCode
    final int samplesReceived
    final int samplesQcPass
    final int samplesQcFail
    final int libraryPrepFinished
    final int dataAvailable
    final int samplesInProject

    StatusCount(String projectCode, int samplesReceived, int samplesQcPass, int samplesQcFail, int libraryPrepFinished, int dataAvailable, int samplesInProject) {
        this.projectCode = Objects.requireNonNull(projectCode)
        this.samplesReceived = Objects.requireNonNull(samplesReceived)
        this.samplesQcPass = Objects.requireNonNull(samplesQcPass)
        this.samplesQcFail = Objects.requireNonNull(samplesQcFail)
        this.libraryPrepFinished = Objects.requireNonNull(libraryPrepFinished)
        this.dataAvailable = Objects.requireNonNull(dataAvailable)
        this.samplesInProject = Objects.requireNonNull(samplesInProject)
    }
}
