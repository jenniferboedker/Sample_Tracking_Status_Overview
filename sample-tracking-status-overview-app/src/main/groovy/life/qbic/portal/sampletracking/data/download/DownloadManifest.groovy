package life.qbic.portal.sampletracking.data.download


/**
 * <b>A download manifest for download using our postman-cli</b>
 *
 * <p>Represents a download manifest. This manifest can be printed and used with the <a href="https://github.com/qbicsoftware/postman-cli#provide-a-file-with-several-qbic-ids">postman-cli.</a></p>
 *
 * @since 1.0.0
 */
class DownloadManifest {

    private Set<String> sampleCodes

    /**
     * Please use {@link DownloadManifest#from(Collection < String >)} to create a DownloadManifest
     * @see DownloadManifest#from
     */
    private DownloadManifest() {
        sampleCodes = new LinkedHashSet<>()
    }

    /**
     * Creates a download manifest from the provided sample codes
     * @param sampleCodes a list of sample codes that are contained in this manifest
     * @return a DownloadManifest containing the provided sample codes
     * @since 1.0.0
     */
    static DownloadManifest from(Collection<String> sampleCodes) {
        DownloadManifest downloadManifest = new DownloadManifest()
        Collection<String> uniqueSampleCodes = sampleCodes.unique()
        downloadManifest.sampleCodes.addAll(uniqueSampleCodes)
        return downloadManifest
    }

    /**
     * list all sample codes associated directly to this manifest
     * @return a list of sample codes
     * @since 1.0.0
     */
    List<String> listSampleCodes() {
        // we do not use `getSampleCodes` since this would lead to errors in the `from`
        // method caused by groovy using the getter instead of the field
        return this.sampleCodes.toList()
    }

    /**
     * <p>We need this to ensure that Groovy accesses the field internally correctly.</p>
     * <p><b>PLEASE NOTE:</b> Implementing this method as a public getter with a new set as a return
     * value breaks internal functionality due to Groovy calling the getter instead of accessing the
     *  field directly.</p>
     * @return the {@link #sampleCodes} field (the same instance)
     */
    private Set<String> getSampleCodes() {
        return sampleCodes
    }

    @Override
    String toString() {
        return "DownloadManifest{" +
                "sampleCodes=" + sampleCodes +
                '}'
    }
}
