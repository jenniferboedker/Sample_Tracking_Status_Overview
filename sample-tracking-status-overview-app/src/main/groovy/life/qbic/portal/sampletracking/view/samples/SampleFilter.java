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

  private String allowedStatus = "";

  private String containedText = "";

  public SampleFilter containingText(String substring) {
    if (Objects.isNull(substring)) {
      substring = "";
    }
    this.containedText = substring;
    return this;
  }

  public SampleFilter withStatus(String allowedStatus) {
    if (Objects.isNull(allowedStatus)) {
      allowedStatus = "";
    }
    this.allowedStatus = allowedStatus;
    return this;
  }

  @Override
  public boolean test(Sample sample) {
    if (Objects.isNull(sample)) {
      return false;
    }
    return containsText(sample.code()) && matchesStatus(sample.sampleStatus())
        || containsText(sample.label()) && matchesStatus(sample.sampleStatus());
  }

  private boolean containsText(String value) {
    if (containedText.isEmpty()) {
      return true;
    }
    if (Objects.isNull(value)) {
      return false;
    }
    return value.toLowerCase().contains(containedText.toLowerCase());
  }

  private boolean matchesStatus(String value) {
    if (allowedStatus.isEmpty()) {
      return true;
    }
    return allowedStatus.equalsIgnoreCase(value);
  }

  @Override
  public void clear() {
    allowedStatus = "";
    containedText = "";
  }
}
