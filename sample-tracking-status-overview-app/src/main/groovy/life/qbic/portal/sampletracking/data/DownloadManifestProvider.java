package life.qbic.portal.sampletracking.data;

import java.io.ByteArrayInputStream;

/**
 * Provides a {@link life.qbic.business.download.DownloadManifest} given a set of properties.
 *
 * @since 1.1.4
 */
public interface DownloadManifestProvider {

  /**
   * Provides a download manifest as input stream given a project code.
   * @param projectCode the project code for the provided manifest
   * @return an byte array input stream containing the manifest
   */
  ByteArrayInputStream getManifestForProject(String projectCode);

}
