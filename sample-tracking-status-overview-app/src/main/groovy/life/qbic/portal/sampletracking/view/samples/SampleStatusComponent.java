package life.qbic.portal.sampletracking.view.samples;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.data.SampleStatusProvider;
import life.qbic.portal.sampletracking.view.Spinner;
import life.qbic.portal.sampletracking.view.projects.State;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleStatusComponent extends Composite implements Comparable<SampleStatusComponent> {

  private static final Logger log = LogManager.getLogger(SampleStatusComponent.class);

  private final SampleStatusProvider sampleStatusProvider;

  private final ExecutorService executorService;
  private final Label label;
  private final Spinner spinner = new Spinner();
  private final Sample sample;
  private String loadedData = "";

  public SampleStatusComponent(Sample sample, SampleStatusProvider sampleStatusProvider,
      ExecutorService executorService) {
    this.sampleStatusProvider = sampleStatusProvider;
    this.sample = sample;
    this.executorService = executorService;
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
    if (Objects.nonNull(loadedData) && Objects.nonNull(sample.sampleStatus())) {
      if (loadedData.equals(sample.sampleStatus())) {
        return;
      } else {
        showSampleStatus(sample.sampleStatus());
        return;
      }
    }
    UI ui = UI.getCurrent();
    ui.setPollInterval(200);
    getCompositionRoot().removeStyleNames(State.FAILED.getCssClass(), State.IN_PROGRESS.getCssClass(), State.COMPLETED.getCssClass());
    spinner.setVisible(true);
    label.setVisible(false);
    executorService.submit(() -> {
      try {
        Optional<String> retrieved = sampleStatusProvider.getForSample(sample.code());
        ui.access(() -> {
          retrieved.ifPresent(it -> {
            showSampleStatus(it);
            sample.setSampleStatus(it);
          });
          if (!retrieved.isPresent()) {
            showError();
          }
          spinner.setVisible(false);
          label.setVisible(true);
        });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        ui.access(() -> {
          showError();
          spinner.setVisible(false);
          label.setVisible(true);
        });
      }
    });
  }

  private void showSampleStatus(String sampleStatus) {
    if (Objects.nonNull(loadedData) && Objects.nonNull(sampleStatus)) {
      if (sampleStatus.equals(loadedData)) {
        return;
      }
    }
    label.setValue(sampleStatus);
    getCompositionRoot().setStyleName(determineStyleName(sampleStatus));
    loadedData = sampleStatus;
  }

  private void showError() {
    label.setValue("Information not available");
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

  @Override
  public int compareTo(SampleStatusComponent o) {
    return this.loadedData.compareTo(o.loadedData);
  }
}
