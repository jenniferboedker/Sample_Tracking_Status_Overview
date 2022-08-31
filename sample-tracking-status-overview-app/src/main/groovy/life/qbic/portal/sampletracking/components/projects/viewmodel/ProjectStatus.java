package life.qbic.portal.sampletracking.components.projects.viewmodel;

import java.time.Instant;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ProjectStatus {

  private final int totalCount;
  private final int countReceived;
  private final int countPassedQc;
  private final int countFailedQc;
  private final int countLibraryPrepared;
  private final int countDataAvailable;

  private final Instant lastModified;


  public ProjectStatus(int totalCount, int countReceived, int countPassedQc, int countFailedQc,
      int countLibraryPrepared, int countDataAvailable, Instant lastModified) {
    this.totalCount = totalCount;
    this.countReceived = countReceived;
    this.countPassedQc = countPassedQc;
    this.countFailedQc = countFailedQc;
    this.countLibraryPrepared = countLibraryPrepared;
    this.countDataAvailable = countDataAvailable;
    this.lastModified = lastModified;
  }

  public int totalCount() {
    return totalCount;
  }

  public int countReceived() {
    return countReceived;
  }

  public int countPassedQc() {
    return countPassedQc;
  }

  public int countFailedQc() {
    return countFailedQc;
  }

  public int countLibraryPrepared() {
    return countLibraryPrepared;
  }

  public int countDataAvailable() {
    return countDataAvailable;
  }

  public Instant getLastModified() {
    return lastModified;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProjectStatus that = (ProjectStatus) o;

    if (totalCount != that.totalCount) {
      return false;
    }
    if (countReceived != that.countReceived) {
      return false;
    }
    if (countPassedQc != that.countPassedQc) {
      return false;
    }
    if (countFailedQc != that.countFailedQc) {
      return false;
    }
    if (lastModified != that.lastModified) {
      return false;
    }
    if (countLibraryPrepared != that.countLibraryPrepared) {
      return false;
    }
    return countDataAvailable == that.countDataAvailable;
  }

  @Override
  public int hashCode() {
    int result = totalCount;
    result = 31 * result + countReceived;
    result = 31 * result + countPassedQc;
    result = 31 * result + countFailedQc;
    result = 31 * result + countLibraryPrepared;
    result = 31 * result + countDataAvailable;
    result = 31 * result + (getLastModified() != null ? getLastModified().hashCode() : 0);
    return result;
  }
}
