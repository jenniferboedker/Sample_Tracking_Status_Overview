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
@EqualsAndHashCode(includeFields = true)
class ProjectSummary {
    String code
    String title
    int samplesReceived

    private ProjectSummary(Builder builder) {
        this.code = builder.code
        this.title = builder.title
        this.samplesReceived = builder.samplesReceived
    }

    static class Builder {
        private String code
        private String title
        private int samplesReceived

        /**
         * Constructs a builder and sets the code and title.
         * @param code the project code to be used to build a ProjectSummary
         * @param title the project title to be used to build a ProjectSummary
         * @since 1.0.0
         */
        Builder(String code, String title) {
            this.code = code
            this.title = title
            this.samplesReceived = 0
        }

        /**
         * Constructs a builder using infromation from a project dto
         * @param projectDto the project dto to be read
         * @since 1.0.0
         */
        Builder(Project projectDto) {
            this.code = projectDto.projectId.projectCode.toString()
            this.title = projectDto.projectTitle
            this.samplesReceived = 0
        }

        /**
         * Sets the number of received samples
         * @param number
         * @return the current Builder with the number set
         * @since 1.0.0
         */
        Builder samplesReceived(int number) {
            this.samplesReceived = number
            return this
        }

        /**
         * Builds a ProjectSummary using this builder
         * @return a new instance of a ProjectSummary
         * @since 1.0.0
         */
        ProjectSummary build() {
            return new ProjectSummary(this)
        }
    }
}
