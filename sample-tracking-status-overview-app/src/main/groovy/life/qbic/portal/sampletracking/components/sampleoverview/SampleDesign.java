package life.qbic.portal.sampletracking.components.sampleoverview;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Grid;

/**
 * !! DO NOT EDIT THIS FILE !!
 * <p>
 * This class is generated by Vaadin Designer and will be overwritten.
 * <p>
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class SampleDesign extends VerticalLayout {
    protected HorizontalLayout hotbarLayout;
    protected TextField searchField;
    protected ComboBox status;
    protected Button projectsButton;
    protected Button samplesButton;
    protected Grid<life.qbic.business.samples.Sample> sampleGrid;

    public SampleDesign() {
        Design.read(this);
    }

}
