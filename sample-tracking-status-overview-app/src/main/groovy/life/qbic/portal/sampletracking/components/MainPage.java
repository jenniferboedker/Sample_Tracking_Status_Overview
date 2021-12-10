package life.qbic.portal.sampletracking.components;

import life.qbic.portal.sampletracking.components.projectoverview.ProjectView;
import com.vaadin.ui.VerticalLayout;
import life.qbic.portal.sampletracking.components.sampleoverview.SampleView;


public class MainPage extends VerticalLayout {


    public MainPage(ProjectView projectLayout, SampleView sampleLayout) {

        this.addComponents(projectLayout,sampleLayout);
        sampleLayout.setVisible(false);
    }
}
