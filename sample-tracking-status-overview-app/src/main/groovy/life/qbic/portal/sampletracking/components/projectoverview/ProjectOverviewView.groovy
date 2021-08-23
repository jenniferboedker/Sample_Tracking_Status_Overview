package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.portal.sampletracking.components.download.DownloadProjectComponent

import java.util.function.Consumer

/**
 * <h1>This class generates the layout for the ProductOverview use case</h1>
 *
 * <p>This view will be the entry point for the user and provides an overview of her projects. And the projects overall status.
 * From here, the user can navigate to the StatusOverview.</p>
 *
 * @since 1.0.0
 *
 */
class ProjectOverviewView extends VerticalLayout {

    private Label titleLabel
    private ProjectOverviewViewModel viewModel
    private Grid<ProjectSummary> projectGrid
    private final Map<ProjectSummary, DownloadProjectComponent> downloadComponents
    private final Consumer<String> projectDownload

    final static int MAX_CODE_COLUMN_WIDTH = 400
    final static int MAX_STATUS_COLUMN_WIDTH = 200

    ProjectOverviewView(ProjectOverviewViewModel viewModel) {
        this.viewModel = viewModel
        this.downloadComponents = new HashMap<>()
        this.projectDownload = {
            //TODO call download controller here
            projectCode -> println("Starting download of $projectCode")
        }
        initLayout()
        fillGrid()
    }

    private void initLayout() {
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)
        projectGrid = new Grid<>()

        //FIXME remove test button
        Button button = new Button("Generate")
        button.addClickListener({
            viewModel.updateCount("QSTTS", new Random().nextInt(100))
        })
        this.addComponent(button)
        //end of fixme
        this.addComponents(titleLabel, projectGrid)
    }

    private void fillGrid() {
        projectGrid.addColumn({ it.code })
                .setCaption("Project Code").setId("ProjectCode").setMaximumWidth(
                MAX_CODE_COLUMN_WIDTH)
        projectGrid.addColumn({ it.title })
                .setCaption("Project Title").setId("ProjectTitle")
        projectGrid.addColumn({ it.samplesReceived })
                .setCaption("Samples Received").setId("SamplesReceived")
        projectGrid.addColumn({ it.samplesQcFailed })
                .setCaption("Samples Failed QC").setId("SamplesFailedQc")
        setupDataProvider()
        projectGrid.addComponentColumn({ projectSummary ->
            getDownloadComponent(projectSummary)
        })
        //specify size of grid and layout
        projectGrid.setWidthFull()
        projectGrid.getColumn("ProjectTitle")
                .setMinimumWidth(200)
        projectGrid.getColumn("SamplesReceived")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.getColumn("SamplesFailedQc")
                .setMaximumWidth(MAX_STATUS_COLUMN_WIDTH).setExpandRatio(1)
        projectGrid.setHeightMode(HeightMode.ROW)
    }

    private void setupDataProvider() {
        def dataProvider = new ListDataProvider(viewModel.projectOverviews)
        projectGrid.setDataProvider(dataProvider)
    }

    private DownloadProjectComponent getDownloadComponent(ProjectSummary projectSummary) {
        // see if the component was generated
        if (downloadComponents.containsKey(projectSummary)) {
            // we can reuse the component
            return downloadComponents.get(projectSummary)
        } else {
            // remove all old information on the project summary form the map
            downloadComponents.removeAll { (it.getKey().getCode() == projectSummary.getCode()) }
            // generate new component
            DownloadProjectComponent generatedComponent = DownloadProjectComponent.from(projectSummary, projectDownload)
            downloadComponents.put(projectSummary, generatedComponent)
            return generatedComponent
        }
    }
}
