package life.qbic.portal.sampletracking.components.projectoverview.statusdisplay


import spock.lang.Specification

/**
 * <b>Tests for equals and compareTo methods</b>
 *
 * @since 1.0.0
 */
class RelativeCountSpec extends Specification {

    def "Equals is false for different relative counts"() {
        given: "Two ids with different content"
        def relativeCountA = new RelativeCount(3, 42)
        def relativeCountB = new RelativeCount(value, total)

        expect: "them not to be equal"
        relativeCountA != relativeCountB

        where: "all properties are different"
        [value, total] << [[0, 4, 33, 9, 7, 100], [100, 333, 124]].combinations()
    }

    def "Equals is true for equal RelativeCounts"() {
        given: "Two ids with different content"
        def relativeCountA = new RelativeCount(value, total)
        def relativeCountB = new RelativeCount(value, total)

        expect: "them not to be equal"
        relativeCountA == relativeCountB

        where: "all properties are different"
        [value, total] << [[0, 4, 33, 9, 7, 100], [100, 333, 124]].combinations()
    }

    def "compareTo is transitive: (#x > #y and #y > #z) then #x > #z"() {
        when: "x.compareTo(y) > 0 && y.compareTo(z) > 0"
        assert x.compareTo(y) > 0 && y.compareTo(z) > 0
        then: "x.compareTo(z) > 0"
        x.compareTo(z) > 0
        where: "x, y and z are as follows"
        x | y | z
        new RelativeCount(2,2) | new RelativeCount(1,2) | new RelativeCount(0,2)
        new RelativeCount(0,4) | new RelativeCount(0,3) | new RelativeCount(0,2)
        new RelativeCount(3,3) | new RelativeCount(2,4) | new RelativeCount(1,5)
    }


    def "compareTo is symmetric: sgn(x.compareTo(y)) = -sgn(y.compareTo(x)) for x=#x and y=#y"() {
        expect: "sgn(#x.compareTo(#y)) = -sgn(#y.compareTo(#x))"
        Integer.signum(x.compareTo(y)) == -Integer.signum(y.compareTo(x))
        where:
        x | y
        new RelativeCount(2,2) | new RelativeCount(2,2)
        new RelativeCount(2,2) | new RelativeCount(1,2)
        new RelativeCount(2,2) | new RelativeCount(3,2)
    }

    def "compareTo is reflexive: equals itself"() {
        given:
        RelativeCount relativeCount = new RelativeCount(value, total)
        expect:
        relativeCount.equals(relativeCount)
        where:
        [value, total] << [[0, 4, 33, 9, 7, 100], [100, 333, 124]].combinations()
    }

    def "compareTo returns 0 for equal objects"() {
        given:
        RelativeCount relativeCount = new RelativeCount(1,4)
        expect:
        relativeCount.equals(relativeCount)
        relativeCount.compareTo(relativeCount) == 0
    }

    def "compareTo does not returns 0 for unequal objects"() {
        given:
        RelativeCount relativeCountA = new RelativeCount(1,4)
        RelativeCount relativeCount2 = new RelativeCount(4,99)

        expect:
        !relativeCountA.equals(relativeCount2)
        relativeCountA.compareTo(relativeCount2) != 0
    }

    def "compareTo #x <=> #y = #expectedResult"() {
        when:
        int result = x <=> y
        then:
        result == expectedResult

        where:
        x | y | expectedResult
        new RelativeCount(2,2) | new RelativeCount(2,2) | 0
        new RelativeCount(2,2) | new RelativeCount(1,2) | 1
        new RelativeCount(2,3) | new RelativeCount(3,3) | -1
        new RelativeCount(1,2) | new RelativeCount(1,1) | 1
        new RelativeCount(1,2) | new RelativeCount(1,3) | -1
    }
}
