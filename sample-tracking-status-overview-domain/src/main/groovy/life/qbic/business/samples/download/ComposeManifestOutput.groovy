package life.qbic.business.samples.download

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
interface ComposeManifestOutput {

    /**
     * Reads a printed download manifest
     * @param printedManifest a printed download manifest
     * @since 1.0.0
     */
    void readManifest(String printedManifest)

    /**
     * To be called when no download manifest could be composed
     * @param reason the reason why the composition failed
     * @since 1.0.0
     */
    void failedExecution(String reason)

}