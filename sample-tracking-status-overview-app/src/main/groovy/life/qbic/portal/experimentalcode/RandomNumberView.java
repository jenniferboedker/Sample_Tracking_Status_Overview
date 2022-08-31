package life.qbic.portal.experimentalcode;

import static org.apache.logging.log4j.LogManager.getLogger;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import life.qbic.portal.experimentalcode.ExperimentalApp.Pair;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class RandomNumberView extends HorizontalLayout {

  private static final Logger log = getLogger(RandomNumberView.class);

  private final RandomNumberProvider randomNumberProvider;
  private final TextField textField = new TextField();
  private final Button button = new Button("Generate");
  private final ProgressBar spinner = createSpinner();
  private final ExecutorService executorService;

  private Future<?> loadingFuture;

  private final Pair pair;

  private final List<UpdateListener> updateListeners = new ArrayList<>();

  public RandomNumberView(RandomNumberProvider randomNumberProvider,
      ExecutorService executorService, Pair pair) {
    this.executorService = executorService;
    this.randomNumberProvider = randomNumberProvider;
    this.pair = pair;

    textField.setReadOnly(true);
    this.addComponent(textField);
    button.setClickShortcut(KeyCode.ENTER);
    button.addClickListener(it -> {
      doSomeWork();
    });
    this.addComponent(button);
    this.addComponent(spinner);
    textField.setValue(String.valueOf(pair.randomNumber()));
    spinner.setVisible(false);
    button.setVisible(true);
  }


  @Override
  public void attach() {
    super.attach();
    if (pair.randomNumber() < 0) {
      doSomeWork();
    }
  }

  private void doSomeWork() {
    UI ui = getUI();
    ui.setPollInterval(200);
    spinner.setVisible(true);
    textField.clear();
    button.setVisible(false);
    loadingFuture = executorService.submit(() -> {
      int randomNumber = randomNumberProvider.randomNumber(pair.first(), pair.last());
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        //
      }
      pair.setRandomNumber(randomNumber);
      ui.access(() -> {
        textField.setValue(String.valueOf(randomNumber));
        spinner.setVisible(false);
        button.setVisible(true);
        fireUpdateEvent(new UpdateEvent(this));
      });
    });
  }


  @Override
  public void detach() {
    super.detach();
  }

  private ProgressBar createSpinner() {
    ProgressBar spinner = new ProgressBar();
    spinner.setIndeterminate(true);
    return spinner;
  }

  public RandomNumberView withUpdateListener(UpdateListener listener) {
    updateListeners.add(listener);
    return this;
  }

  public void removeUpdateListener(UpdateListener listener) {
    updateListeners.remove(listener);
  }

  private void fireUpdateEvent(UpdateEvent event) {
    updateListeners.forEach(it -> it.handle(event));
  }

  public class UpdateEvent {
    private final RandomNumberView source;

    public UpdateEvent(RandomNumberView source) {
      this.source = source;
    }
  }

  @FunctionalInterface
  public interface UpdateListener {
    void handle(UpdateEvent event);
  }

  public Pair pair() {
    return pair;
  }
}
