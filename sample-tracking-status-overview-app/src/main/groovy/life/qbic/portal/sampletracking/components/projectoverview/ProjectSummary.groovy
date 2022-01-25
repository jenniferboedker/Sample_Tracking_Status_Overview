package life.qbic.portal.sampletracking.components.projectoverview

import groovy.transform.EqualsAndHashCode
import life.qbic.business.project.Project
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.SampleCount

import java.time.Instant

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
    SampleCount samplesReceived
    SampleCount samplesQc
    SampleCount samplesLibraryPrepFinished
    SampleCount sampleDataAvailable
    Instant lastChanged

    ProjectSummary(String code, String title, boolean hasSubscription) {
        this.code = code
        this.title = title
        this.hasSubscription = hasSubscription
        this.samplesReceived = new SampleCount(0, 0, 0)
        this.samplesQc = new SampleCount(0, 0, 0)
        this.samplesLibraryPrepFinished = new SampleCount(0, 0, 0)
        this.sampleDataAvailable = new SampleCount(0, 0, 0)
        this.totalSampleCount = 0
        this.lastChanged = Instant.MIN
    }

    static ProjectSummary of(Project project) {
        String code = project.code
        String title = project.title
        boolean hasSubscription = project.hasSubscription
        ProjectSummary summary = new ProjectSummary(code, title, hasSubscription)
        summary.lastChanged = project.lastChanged
        return summary
    }

    String getCode() {
        return code
    }

    String getTitle() {
        return title
    }
}
