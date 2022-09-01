package life.qbic.portal.sampletracking.view.samples.viewmodel;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class Sample {

  private final String code;
  private final String label;

  private String sampleStatus;

  public Sample(String code, String label) {
    this.code = code;
    this.label = label;
  }

  public String sampleStatus() {
    return sampleStatus;
  }

  public void setSampleStatus(
      String sampleStatus) {
    this.sampleStatus = sampleStatus;
  }

  public String label() {
    return label;
  }

  public String code() {
    return code;
  }

}
