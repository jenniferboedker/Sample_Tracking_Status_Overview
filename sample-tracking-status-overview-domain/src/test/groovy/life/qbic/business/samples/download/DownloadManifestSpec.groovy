package life.qbic.business.samples.download

import spock.lang.Specification

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class DownloadManifestSpec extends Specification {
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
