package life.qbic.portal.sampletracking.components.projectoverview

import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.dtos.projectmanagement.Project

/**
 * <b>Project Overview DTO</b>
 *
 * <p>A simple java DTO to be used in the project overview grid</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode(includeFields = true)
class ProjectOverview {
    String code
    String title
    int samplesReceived

    private ProjectOverview(Builder builder) {
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
         * @param code the project code to be used to build a ProjectOverview
         * @param title the project title to be used to build a ProjectOverview
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
         * Builds a ProjectOverview using this builder
         * @return a new instance of a ProjectOverview
         * @since 1.0.0
         */
        ProjectOverview build() {
            return new ProjectOverview(this)
        }
    }
}
