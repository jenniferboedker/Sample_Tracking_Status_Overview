package life.qbic.portal.sampletracking.components.projectoverview

import spock.lang.Specification

import java.time.Instant

class LastChangedComparatorSpec extends Specification {

    Instant iceAge = Instant.MIN
    Instant middleAges = Instant.now().minus(500)
    Instant now = Instant.now()

    ProjectSummary oldest = fakeProjectSummary("QOLDY", iceAge)
    ProjectSummary middle = fakeProjectSummary("QBTWN", middleAges)
    ProjectSummary newest = fakeProjectSummary("QNOWY", now)

    def "summaries can be sorted ascending using the timestamp comparison"() {
        given:"An order not ascending"
        def projectOverviews = [newest, oldest, middle]

        when: "we sort ascendingly"
        Collections.sort(projectOverviews, new LastChangedComparator(LastChangedComparator.SortOrder.ASCENDING))

        then: "the order is as expected"
        projectOverviews == [oldest, middle, newest]
    }

    def "summaries can be sorted descending using the timestamp comparison"() {
        given: "An order not descending"
        def projectOverviews = [newest, oldest, middle]

        when: "we sort descending"
        Collections.sort(projectOverviews, new LastChangedComparator(LastChangedComparator.SortOrder.DESCENDING))

        then: "the order is as expected"
        projectOverviews == [newest, middle, oldest]
    }

    private static ProjectSummary fakeProjectSummary(String code, Instant instant) {
        ProjectSummary summary = new ProjectSummary(code, "test2", false)
        summary.lastChanged = instant
        return summary
    }
}
