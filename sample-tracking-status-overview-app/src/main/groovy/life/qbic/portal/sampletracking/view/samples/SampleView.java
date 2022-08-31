package life.qbic.portal.sampletracking.view.samples;

import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderRow;
import life.qbic.portal.sampletracking.data.SampleRepository;
import life.qbic.portal.sampletracking.data.SampleStatusProvider;
import life.qbic.portal.sampletracking.view.ResponsiveGrid;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleView extends SampleDesign {

  private final ResponsiveGrid<Sample> sampleGrid;

  private final SampleRepository sampleRepository;
  private final SampleStatusProvider sampleStatusProvider;

  public SampleView(SampleRepository sampleRepository, SampleStatusProvider sampleStatusProvider) {
    this.sampleRepository = sampleRepository;
    this.sampleStatusProvider = sampleStatusProvider;
    avoidElementOverlap();
    sampleGrid = createSampleGrid();
    addSampleGrid();
    setHeaderRowStyle();
  }

  private void addSampleGrid() {
    this.addComponent(sampleGrid);
  }

  private void avoidElementOverlap() {
    this.addStyleName("responsive-grid-layout");
    this.setWidthFull();
  }

  private ResponsiveGrid<Sample> createSampleGrid() {
    ResponsiveGrid<Sample> grid = new ResponsiveGrid<>();
    grid.addColumn(Sample::label)
        .setId("label")
        .setCaption("Sample Label");
    grid.addColumn(Sample::code)
        .setId("code")
        .setCaption("QBiC Code");
    grid.addComponentColumn(
            it -> new SampleStatusComponent(sampleStatusProvider.getForSample(it.code()).toString(),
                sampleStatusProvider))
        .setId("status")
        .setCaption("Status");
    grid.setSizeFull();
    grid.setSelectionMode(SelectionMode.NONE);
    return grid;
  }



  private void setHeaderRowStyle() {
    HeaderRow headerRow = sampleGrid.getDefaultHeaderRow();
    headerRow.getCell("label").setStyleName("cell-min-width");
    headerRow.getCell("code").setStyleName("cell-min-width");
    headerRow.getCell("status").setStyleName("cell-min-width");
  }

  @Override
  public void attach() {
    super.attach();
    sampleGrid.setItems(sampleRepository.findAll());
  }
}
