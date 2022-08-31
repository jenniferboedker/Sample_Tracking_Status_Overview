package life.qbic.portal.sampletracking.components.samples.viewmodel;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleStatus {

  private final String value;

  public SampleStatus(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
