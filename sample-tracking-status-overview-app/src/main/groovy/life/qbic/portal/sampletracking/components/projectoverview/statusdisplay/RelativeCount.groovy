package life.qbic.portal.sampletracking.components.projectoverview.statusdisplay

import groovy.transform.EqualsAndHashCode

/**
 * <b>A relative count of something.</b>
 *
 * <p>This class represents a relative count. It has a value that is a part of a total.</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class RelativeCount implements Comparable<RelativeCount> {
    final int value
    final int total

    RelativeCount(int value, int total) {
        this.value = value
        this.total = total
    }

    @Override
    String toString() {
        return "$value / $total"
    }
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception if
     * <tt>y.compareTo(x)</tt> throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     *
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     *
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    @Override
    int compareTo(RelativeCount other) {
        if (this.equals(other))  return 0
        int valueComparison = this.value.compareTo(other.value)
        if (valueComparison == 0) {
            //if the value is equal compare the total
            return this.total.compareTo(other.total)
        } else {
            return valueComparison
        }
    }
}
