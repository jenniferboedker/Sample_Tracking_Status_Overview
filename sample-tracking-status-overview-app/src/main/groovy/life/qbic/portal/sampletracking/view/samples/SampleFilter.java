package life.qbic.portal.sampletracking.view.samples;

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
  public boolean test(Sample item) {
    return false;
  }

  @Override
  public void clear() {

  }
}
