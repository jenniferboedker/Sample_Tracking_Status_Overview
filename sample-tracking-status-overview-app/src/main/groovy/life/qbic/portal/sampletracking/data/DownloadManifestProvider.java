package life.qbic.portal.sampletracking.data;

import java.io.InputStream;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface DownloadManifestProvider {

  InputStream getManifestForProject(String projectCode);

}
