package life.qbic.portal.experimentalcode;

import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import life.qbic.datamodel.dtos.portal.PortalUser;
import life.qbic.portal.sampletracking.DependencyManager;
import life.qbic.portal.sampletracking.QBiCPortletUI;
import life.qbic.portal.sampletracking.old.system.SystemContext;
import life.qbic.portal.sampletracking.old.system.TestingSystemContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ExperimentalApp extends QBiCPortletUI {


  private static final Logger log = LogManager.getLogger(ExperimentalApp.class);
  private static final int THREAD_LIMIT = 2;
  private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_LIMIT);
  private final RandomNumberProvider randomNumberProvider = new RandomNumberGenerator();
  private Grid<Pair> pairGrid;

  static class Pair {

    private final int first;
    private final int last;

    private final int id;
    private int randomNumber = -1;

    private Pair(int id, int first, int last) {
      this.id = id;
      this.first = first;
      this.last = last;
    }

    public int id() {
      return id;
    }

    public int first() {
      return first;
    }

    public int last() {
      return last;
    }

    public int randomNumber() {
      return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
      this.randomNumber = randomNumber;
    }
  }


  @Override
  protected Layout getPortletContent(VaadinRequest request) {
    DependencyManager dependencyManager = new DependencyManager(loadUser());
    return dependencyManager.getPortletView();
  }

  private static PortalUser loadUser() {
    return determinePortalUser().orElseThrow(() ->
        new RuntimeException("Could not determine portal user.")
    );
  }

  private static Optional<PortalUser> determinePortalUser() {
    if(Objects.equals(System.getProperty("environment"), "testing")) {
      log.info("Running app in test mode...");
      return TestingSystemContext.getUser();
    } else {
      return SystemContext.getUser();
    }
  }

  private VerticalLayout someMethod() {
    VerticalLayout layout = new VerticalLayout();
    layout.setMargin(false);
    pairGrid = new Grid<>();
    pairGrid.setSelectionMode(SelectionMode.MULTI);
    List<Pair> pairs = new ArrayList<>();
    for (int i = 0; i < 2000; i++) {
      int first = new Random().nextInt(100);
      int last = new Random().nextInt(100) + first;
      pairs.add(new Pair(i, first, last));
    }
    pairGrid.setItems(pairs);
    pairGrid.addColumn(Pair::id);
    pairGrid.addComponentColumn(
        it -> new RandomNumberView(randomNumberProvider, executorService, it).withUpdateListener(
            val -> pairGrid.getDataProvider().refreshAll()));
    layout.addComponent(pairGrid);
    layout.addComponent(new Button("show only between 10-15", it -> {
      ListDataProvider<Pair> dataProvider = (ListDataProvider<Pair>) pairGrid.getDataProvider();
      dataProvider.setFilter(it2 -> it2.id() >= 1250 && it2.id() <=1500);
      pairGrid.setDataProvider(dataProvider);
    }));
    layout.addComponent(new Button("hide and show", it -> {
      UI ui = getUI();
      hideElement();
      CompletableFuture.runAsync(() -> {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          return;
        }
        ui.access(this::showElement);
      });
    }));
    Column<Pair, Integer> pairIntegerColumn = pairGrid.addColumn(Pair::randomNumber)
        .setId("_randomNumber");
    pairIntegerColumn.setHidden(true);
    pairGrid.setSortOrder(GridSortOrder.desc(pairIntegerColumn));
    return layout;
  }

  private void doNothing() {

  }


  private void showElement() {
    ListDataProvider<Pair> dataProvider = (ListDataProvider<Pair>) pairGrid.getDataProvider();
    dataProvider.setFilter(null);
    dataProvider.refreshAll();
  }

  private void hideElement() {
    if (pairGrid != null) {
      Set<Pair> selectedItems = pairGrid.getSelectedItems();
      ListDataProvider<Pair> dataProvider = (ListDataProvider<Pair>) pairGrid.getDataProvider();
      dataProvider.setFilter(it -> !selectedItems.contains(it));
      pairGrid.setDataProvider(dataProvider);
    }
  }

  @Override
  public void detach() {
    super.detach();
    executorService.shutdownNow();
  }


  protected void finalize() {
    executorService.shutdownNow();
  }


}
