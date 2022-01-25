package life.qbic.portal.sampletracking.components.sampleoverview;

import java.util.function.Predicate;

import life.qbic.business.samples.Sample;

/**
 * <b>A filter for samples</b>
 *
 * <p>This filter can be used to filter sample objects. It can be configured and later retrieved as a predicate.</p>
 *
 * <pre>
 * {@code
 * var condition = sampleFilter.withStatus("MY_STATUS").asPredicate();
 * samples.filter(condition); // only samples with status "MY_STATUS"
 * }
 * </pre>
 *
 * @since 1.0.0
 */
public interface SampleFilter {

    /**
     * Configures the filter to pass samples with the specific status only.
     *
     * @param status the String representation of the Status
     * @return a SampleFilter configured to pass the provided status
     * @since 1.0.0
     */
    SampleFilter withStatus(String status);

    /**
     * Turns this SampleFilter into a Predicate based on its configuration
     *
     * @return a predicate based on the configuration of the filter
     * @since 1.0.0
     */
    Predicate<? extends Sample> asPredicate();

    /**
     * Clears the current configuration regarding sample status.
     *
     * @return the sample filter with no configured status
     */
    SampleFilter clearStatus();

}
