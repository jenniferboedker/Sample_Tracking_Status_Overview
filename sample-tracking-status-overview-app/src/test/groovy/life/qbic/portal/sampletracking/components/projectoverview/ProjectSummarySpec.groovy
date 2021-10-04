package life.qbic.portal.sampletracking.components.projectoverview

import java.util.Collections
import java.time.Instant
import java.sql.Timestamp

import life.qbic.portal.sampletracking.components.projectoverview.LastChangedComparator.SortOrder

import spock.lang.Specification

/**
 * <b>Test for comparator</b>
 *
 * @since 1.0.0
 */
class ProjectSummarySpec extends Specification {

    def "summaries can be sorted ascending using the timestamp comparison"() {
        given: "A list of ProjectSummary items"
        ProjectSummary oldest = new ProjectSummary("QOLDY", "the ancient one") //oldest time is set in constructor
        ProjectSummary middle = new ProjectSummary("QBTWN", "a time in between")
        middle.lastChanged = new Timestamp(30000009).toInstant()
        ProjectSummary now = new ProjectSummary("QNOWY", "created just now")
        now.lastChanged = Instant.now()
        List<ProjectSummary> summaries = [now, oldest, middle]
        
        when: "we sort ascendingly"
        Collections.sort(summaries, new LastChangedComparator(SortOrder.ASCENDING))

        then: "the order is as expected"
        summaries.get(0).title == "the ancient one"
        summaries.get(1).title == "a time in between"
        summaries.get(2).title == "created just now"
    }
    
    
    def "summaries can be sorted descending using the timestamp comparison"() {
        given: "A list of ProjectSummary items"
        ProjectSummary oldest = new ProjectSummary("QOLDY", "the ancient one") //oldest time is set in constructor
        ProjectSummary middle = new ProjectSummary("QBTWN", "a time in between")
        middle.lastChanged = new Timestamp(30000009).toInstant()
        ProjectSummary now = new ProjectSummary("QNOWY", "created just now")
        now.lastChanged = Instant.now()
        List<ProjectSummary> summaries = [oldest, now, middle]
        
        when: "we sort ascendingly"
        Collections.sort(summaries, new LastChangedComparator(SortOrder.DESCENDING))

        then: "the order is as expected"
        summaries.get(2).title == "the ancient one"
        summaries.get(1).title == "a time in between"
        summaries.get(0).title == "created just now"
    }
}
