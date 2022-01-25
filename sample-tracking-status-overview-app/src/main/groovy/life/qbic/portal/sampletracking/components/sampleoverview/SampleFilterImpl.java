package life.qbic.portal.sampletracking.components.sampleoverview;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import life.qbic.business.samples.Sample;

/**
 * <b>A filter for samples</b>
 *
 * <p>This filter can be used to filter sample objects. It can be configured and later retrieved as a predicate.</p>
 *
 * <pre>
 * {@code
 * var condition = new SampleFilterImpl().withStatus("MY_STATUS").asPredicate();
 * samples.filter(condition); // only samples with status "MY_STATUS"
 * }
 * </pre>
 *
 * @since 1.0.0
 */
public class SampleFilterImpl implements SampleFilter {

    private final List<String> allowedStatuses = new ArrayList<>();


    @Override
    public SampleFilter withStatus(String status) {
        allowedStatuses.clear();
        allowedStatuses.add(status);
        return this;
    }

    @Override
    public Predicate<? extends Sample> asPredicate() {
        return it -> allowedStatuses.isEmpty() || allowedStatuses.contains(it.getStatus().toString());
    }


    @Override
    public SampleFilter clearStatus() {
        allowedStatuses.clear();
        return this;
    }
}
