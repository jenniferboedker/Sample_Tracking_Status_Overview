package life.qbic.portal.sampletracking.view.samples.viewmodel;

/**
 * <p>The view model for a sample. It holds all information related to a sample that is relevant for display.</p>
 *
 * @since 1.1.4
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
