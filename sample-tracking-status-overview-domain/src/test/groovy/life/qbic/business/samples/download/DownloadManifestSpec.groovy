package life.qbic.business.samples.download

import spock.lang.Specification

/**
 * <p>Tests the creation of download manifests</p>
 *
 * @since 1.0.0
 */
class DownloadManifestSpec extends Specification {
    /**
     * @since 1.0.0
     */
    def "When sample codes are provided, a DownloadManifest print contains each on a single line"() {
        when:
        def sampleCodes = ["This", "Is", "A", "Test"]
        DownloadManifest downloadManifest = DownloadManifest.from(sampleCodes)
        then:
        downloadManifest.print() == expectedResult
        where:
        expectedResult =
                """\
                This
                Is
                A
                Test
                """.stripIndent()
    }
}
