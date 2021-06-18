package life.qbic.business.projectoverview

import life.qbic.datamodel.samples.Sample

/**
 * <h1>A QBiC project</h1>
 *
 * <p>A project contains a unique project code, a description and the samples registered for this project.
 * Furthermore, a projects progress can be determined form the {@link FailedSamplesRatio}.</p>
 *
 * @since 1.0.0
 *
*/
class Project {

    final String projectCode
    final String projectDescription
    final List<Sample> samples
    double projectProgress
    FailedSamplesRatio failedSamples
    Date lastUpdate

    static class Builder {
        private final String projectCode
        private final String projectDescription
        private final List<Sample> samples
        private double progress = 0
        private FailedSamplesRatio failedSamples
        private Date lastUpdate = new Date()

        Builder(String projectCode, String projectDescription, List<Sample> samples) {
            //todo verify the projectCode??
            this.projectCode = projectCode
            this.projectDescription = projectDescription
            this.samples = samples

            failedSamples = new FailedSamplesRatio(0,samples.size())
        }

        Builder progress(double value) {
            //todo check that values are between 1-10
            progress = value
            return this
        }

        Builder failedSamples(int value) {
            failedSamples = new FailedSamplesRatio(value,samples.size())
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
        projectProgress = builder.progress
        failedSamples = builder.failedSamples
        lastUpdate = builder.lastUpdate
    }
}