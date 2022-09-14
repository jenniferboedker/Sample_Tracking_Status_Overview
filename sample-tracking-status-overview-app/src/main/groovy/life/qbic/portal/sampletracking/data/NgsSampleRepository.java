package life.qbic.portal.sampletracking.data;

import java.util.List;

/**
 * <b>Repository for loading NGS samples</b>
 *
 * @since 1.1.3
 */
public interface NgsSampleRepository {

    /**
     * Finds all NGS samples for a given project
     * @param projectCode the code specifying a project
     * @return a list of sample codes from all NGS samples of that project
     */
    public List<String> findNGSSamplesForProject(String projectCode);
}
