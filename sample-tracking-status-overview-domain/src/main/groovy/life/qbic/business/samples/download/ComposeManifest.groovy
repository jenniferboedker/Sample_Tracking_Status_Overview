package life.qbic.business.samples.download

/**
 * <b>Compose a download manifest based on the output of {@link DownloadSamplesOutput}</b>
 *
 * <p>This class composes a download manifest based on the output of {@link DownloadSamplesOutput}.</p>
 *
 * @since 1.0.0
 */
class ComposeManifest implements DownloadSamplesOutput{

    private final ComposeManifestOutput output

    /**
     * @param output
     * @since 1.0.0
     */
    ComposeManifest(ComposeManifestOutput output) {
        this.output = output
    }

    @Override
    void failedExecution(String reason) {
        output.failedExecution(reason)
    }

    @Override
    void foundDownloadableSamples(String projectCode, List<String> sampleCodes) {
        if (sampleCodes.empty) {
            output.failedExecution("There is no sample code provided for download.")
        }
        DownloadManifest downloadManifest = DownloadManifest.from(sampleCodes)
        output.readManifest(downloadManifest.print())
    }
}
