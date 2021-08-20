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
    String code
    String title
    int samplesReceived
    int samplesQcFailed
    int sampleDataAvailable

    ProjectSummary(Project project, int samplesReceived, int samplesQCFailed, int samplesDataAvailable) {
        this.code = project.projectId.projectCode.toString()
        this.title = project.projectTitle
        this.samplesReceived = samplesReceived
        this.samplesQcFailed = samplesQCFailed
        this.sampleDataAvailable = samplesDataAvailable
    }

    ProjectSummary(Project project) {
        this.code = project.projectId.projectCode.toString()
        this.title = project.projectTitle
        this.samplesReceived = 0
        this.samplesQcFailed = 0
        this.sampleDataAvailable = 0
    }

}
