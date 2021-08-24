package life.qbic.business.samples.download
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
     * Please use {@link DownloadManifest#from(Collection<String>)} to create a DownloadManifest
     * @see DownloadManifest#from
     */
    private DownloadManifest() {
        sampleCodes = new LinkedHashSet<>()
    }

    private static String printSampleRow(String sampleCode) {
        return "${sampleCode}\n"
    }


    /**
     * Creates a download manifest from the provided sample codes
     * @param sampleCodes a list of sample codes that are contained in this manifest
     * @return a DownloadManifest containing the provided sample codes
     * @since 1.0.0
     */
    static DownloadManifest from(Collection<String> sampleCodes) {
        DownloadManifest downloadManifest = new DownloadManifest()
        downloadManifest.sampleCodes.addAll(sampleCodes.unique())
        return downloadManifest
    }

    /**
     * Print the download Manifest
     * @return a multi-line String print of the download manifest
     * @since 1.0.0
     */
    String print() {
        sampleCodes.collect {printSampleRow(it)}.sum()
    }


    @Override
    String toString() {
        return print()
    }
}
