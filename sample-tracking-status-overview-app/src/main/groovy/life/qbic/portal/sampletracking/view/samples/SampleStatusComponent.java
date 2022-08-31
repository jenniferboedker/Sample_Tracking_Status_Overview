package life.qbic.portal.sampletracking.view.samples;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import java.util.concurrent.CompletableFuture;
import life.qbic.portal.sampletracking.data.SampleStatusProvider;
import life.qbic.portal.sampletracking.view.Spinner;
import life.qbic.portal.sampletracking.view.projects.State;
import life.qbic.portal.sampletracking.view.samples.viewmodel.SampleStatus;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleStatusComponent extends Composite {

  private final SampleStatusProvider sampleStatusProvider;
  private final Label label;
  private Spinner spinner = new Spinner();
  
  private final String sampleCode;

  public SampleStatusComponent(String sampleCode, SampleStatusProvider sampleStatusProvider) {
    this.sampleStatusProvider = sampleStatusProvider;
    this.sampleCode = sampleCode;
    label = new Label();
    HorizontalLayout rootLayout = new HorizontalLayout();
    rootLayout.setMargin(new MarginInfo(false, true));
    rootLayout.addComponents(spinner,label);
    rootLayout.setSizeUndefined();
    setCompositionRoot(rootLayout);
  }

  @Override
  public void attach() {
    super.attach();
    loadStatusInformation();
  }

  private void loadStatusInformation() {
    UI ui = UI.getCurrent();
    spinner.setVisible(true);
    label.setVisible(false);
    CompletableFuture.runAsync(() -> {
      SampleStatus sampleStatus = sampleStatusProvider.getForSample(sampleCode);
      ui.access(() -> {
        label.setValue(sampleStatus.toString());
        getCompositionRoot().setStyleName(determineStyleName(sampleStatus.toString()));
        spinner.setVisible(false);
        label.setVisible(true);
      });
    });
  }

  private String determineStyleName(String status) {
    State state;
    switch (status) {
      case "DATA_AVAILABLE":
        state = State.COMPLETED;
        break;
      case "SAMPLE_QC_FAIL":
        state = State.FAILED;
        break;
      default:
        state = State.IN_PROGRESS;
    }
    return state.getCssClass();
  }

}
