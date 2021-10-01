package life.qbic.portal.sampletracking.components.projectoverview

import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.dtos.projectmanagement.Project

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
    int totalSampleCount
    SampleCount samplesReceived
    SampleCount samplesQc
    SampleCount samplesLibraryPrepFinished
    SampleCount sampleDataAvailable

    ProjectSummary(String code, String title) {
        this.code = code
        this.title = title
        this.samplesReceived = new SampleCount(0,0)
        this.samplesQc = new SampleCount(0,0)
        this.samplesLibraryPrepFinished = new SampleCount(0,0)
        this.sampleDataAvailable = new SampleCount(0,0)
        this.totalSampleCount = 0
    }

    static ProjectSummary of(Project project) {
        String code = project.projectId.projectCode.toString()
        String title = project.projectTitle
        return new ProjectSummary(code, title)
    }

    String getCode() {
        return code
    }

    String getTitle() {
        return title
    }
}
