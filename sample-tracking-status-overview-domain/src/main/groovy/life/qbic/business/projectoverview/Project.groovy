package life.qbic.business.projectoverview

import life.qbic.datamodel.samples.Sample

/**
 * <h1><short description></h1>
 *
 * <p><detailed description></p>
 *
 * @since 1.0.0
 *
*/
class Project {

    final String projectCode
    final String projectDescription
    final List<Sample> samples
    float progress
    int failedSamples
    Date lastUpdate

    static class Builder {
        private final String projectCode
        private final String projectDescription
        private final List<Sample> samples
        private float progress = 0
        private int failedSamples = 0
        private Date lastUpdate = new Date()

        Builder(String projectCode, String projectDescription, List<Sample> samples) {
            //todo verify the projectCode??
            this.projectCode = projectCode
            this.projectDescription = projectDescription
            this.samples = samples
        }

        Builder progress(float value) {
            //todo check that values are between 1-10
            progress = value
            return this
        }

        Builder failedSamples(int value) {
            failedSamples = value
            return this
        }

        Project build() {
            return new Project(this)
        }
    }

    private Project(Builder builder) {
        projectCode = builder.projectCode
        projectDescription = builder.projectDescription
        samples = builder.samples
        progress = builder.progress
        failedSamples = builder.failedSamples
        lastUpdate = builder.lastUpdate
    }
}