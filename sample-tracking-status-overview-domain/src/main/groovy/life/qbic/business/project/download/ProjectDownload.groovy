package life.qbic.business.project.download

import javax.swing.text.html.Option

/**
 * <b>Project Download Description</b>
 *
 * <p>Holds information on the downloadable parts of a project</p>
 *
 * @since 1.0.0
 */
class ProjectDownload {
    private final String projectCode
    private final List<String> sampleCodes

    private ProjectDownload(Builder builder) {
        this.projectCode = builder.projectCode
        this.sampleCodes = builder.sampleCodes
    }

    /**
     * Returns a list of sample codes for this project download
     * @return a list of sample codes for this project download
     * @since 1.0.0
     */
    Iterator<String> listSamples() {
        this.sampleCodes.iterator()
    }

    static class Builder {
        private String projectCode
        private List<String> sampleCodes

        /**
         * @param projectCode the project code of the project in this download
         * @since 1.0.0
         */
        Builder(String projectCode) {
            this.projectCode = projectCode
            this.sampleCodes = new ArrayList<>()
        }

        Builder addSamples(Iterable<String> sampleCodes) {
            this.sampleCodes.addAll(sampleCodes.collect())
            return this
        }

        Builder removeSamples(Iterable<String> sampleCodes) {
            this.sampleCodes.removeAll{it in sampleCodes}
            return this
        }

        ProjectDownload build() {
            return new ProjectDownload(this)
        }
    }
}
