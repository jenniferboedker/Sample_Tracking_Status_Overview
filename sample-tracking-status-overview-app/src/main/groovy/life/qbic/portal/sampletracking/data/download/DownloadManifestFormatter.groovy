package life.qbic.portal.sampletracking.data.download

/**
 * <b>Formatter for DownloadManifest</b>
 *
 * @since 1.0.0
 */
class DownloadManifestFormatter {

    /**
     * Formats the download Manifest
     * @param downloadManifest the manifest to be formatted
     * @return a multi-line formatted String for the download manifest
     * @since 1.0.0
     */
    static String format(DownloadManifest downloadManifest) {
        String result = downloadManifest.listSampleCodes().collect { formatSampleRow(it) }.sum("")
        return result
    }


    private static String formatSampleRow(String sampleCode) {
        return "${sampleCode}\n"
    }
}
