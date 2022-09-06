package life.qbic.portal.sampletracking.components.projectoverview.download

import life.qbic.portal.sampletracking.data.download.DownloadManifest
import life.qbic.portal.sampletracking.data.download.DownloadManifestFormatter
import spock.lang.Specification

/**
 * <p>Tests the creation of download manifests</p>
 *
 * @since 1.0.0
 */
class DownloadManifestFormatterSpec extends Specification {
    /**
     * @since 1.0.0
     */
    def "When sample codes are provided, a formatted DownloadManifest contains each on a single line"() {
        when:
        def sampleCodes = ["This", "Is", "A", "Test"]
        DownloadManifest downloadManifest = DownloadManifest.from(sampleCodes)
        then:
        DownloadManifestFormatter.format(downloadManifest) == expectedResult
        where:
        expectedResult =
                """\
                This
                Is
                A
                Test
                """.stripIndent()
    }

    /**
     * @since 1.0.0
     */
    def "When no sample codes are provided, formatting a DownloadManifest leads to an empty String"() {
        when:
        def sampleCodes = []
        DownloadManifest downloadManifest = DownloadManifest.from(sampleCodes)
        then:
        DownloadManifestFormatter.format(downloadManifest) == expectedResult
        where:
        expectedResult = ""
    }
}
