package life.qbic.portal.sampletracking.components.projectoverview

import groovy.transform.EqualsAndHashCode
import life.qbic.business.project.Project

/**
 * <b>Project Summary POJO</b>
 *
 * <p>A simple java object to be used in the project overview grid.</p>
 * <p>This class hold information to be shown to the user concerning a specific project.</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class ProjectSummary {
    final String code
    final String title
    boolean hasSubscription = false
    int totalSampleCount
    int samplesReceived
    int samplesQcFailed
    int sampleDataAvailable
    int samplesLibraryPrepFinished

    ProjectSummary(String code, String title, boolean hasSubscription) {
        this.code = code
        this.title = title
        this.hasSubscription = hasSubscription
        this.samplesReceived = 0
        this.samplesQcFailed = 0
        this.sampleDataAvailable = 0
        this.samplesLibraryPrepFinished = 0
        this.totalSampleCount = 0
    }

    static ProjectSummary of(Project project) {
        return new ProjectSummary(project.code, project.title, project.hasSubscription)
    }

    String getCode() {
        return code
    }

    String getTitle() {
        return title
    }
}
