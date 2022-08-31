package life.qbic.portal.sampletracking.components.samples.viewmodel;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class Sample {

  private final String code;
  private String label;
  private SampleStatus status;

  public Sample(String code) {
    this.code = code;
  }

  public String label() {
    return label;
  }

  public String status() {
    return status.toString();
  }

  public String code() {
    return code;
  }

  public Sample setLabel(String label) {
    this.label = label;
    return this;
  }

  public Sample setStatus(SampleStatus status) {
    this.status = status;
    return this;
  }

}
