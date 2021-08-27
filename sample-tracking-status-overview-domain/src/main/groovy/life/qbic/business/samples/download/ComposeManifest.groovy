package life.qbic.business.samples.download

import life.qbic.business.OutputException

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
     * @param output the output receiving the printed manifest
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
        try {
            if (sampleCodes.empty) {
                output.failedExecution("There are no downloadable samples for ${projectCode}.")
                return
            }
            DownloadManifest downloadManifest = DownloadManifest.from(sampleCodes)
            String printableManifest = DownloadManifestFormatter.format(downloadManifest)
            output.readManifest(printableManifest)
        } catch (Exception ignored) {
            throw new OutputException("Could not generate manifest for samples: ${sampleCodes.toListString()}")
        }
    }
}
