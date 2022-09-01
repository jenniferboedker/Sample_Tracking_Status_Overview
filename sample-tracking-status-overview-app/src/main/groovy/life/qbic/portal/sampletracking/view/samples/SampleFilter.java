package life.qbic.portal.sampletracking.view.samples;

import java.util.Objects;
import life.qbic.portal.sampletracking.view.GridFilter;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleFilter implements GridFilter<Sample> {

  private String containedText = "";

  public SampleFilter containingText(String substring) {
    this.containedText = substring;
    return this;
  }

  @Override
  public boolean test(Sample sample) {
    if (Objects.isNull(sample)) {
      return false;
    }
    return containsText(sample.code())
        || containsText(sample.label());
  }

  private boolean containsText(String value) {
    if (Objects.isNull(value)) {
      return false;
    }
    return value.toLowerCase().contains(containedText.toLowerCase());
  }

  @Override
  public void clear() {

  }
}
