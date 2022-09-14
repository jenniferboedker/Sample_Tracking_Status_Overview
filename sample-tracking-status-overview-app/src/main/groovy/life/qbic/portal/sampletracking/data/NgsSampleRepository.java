package life.qbic.portal.sampletracking.data;

import java.util.List;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface NgsSampleRepository {

    /**
     *
     * @param projectCode
     * @return
     */
    public List<String> findNGSSamplesForProject(String projectCode);
}
