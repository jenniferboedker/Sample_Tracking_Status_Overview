package life.qbic.portal.sampletracking.data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import life.qbic.portal.sampletracking.view.projects.viewmodel.ProjectStatus;

public class DummyTrackingConnector implements SampleStatusProvider, ProjectStatusProvider {

  private final Map<String, ProjectStatus> knownStatuses = new HashMap<>();

  @Override
  public Optional<ProjectStatus> getForProject(String projectCode) {
    ProjectStatus knownStatus = knownStatuses.get(projectCode);
    if (Objects.nonNull(knownStatus)) {
      return Optional.of(knownStatus);
    }
    try {
      Thread.sleep(new Random().nextInt(100) * 10);
    } catch (InterruptedException e) {
      return Optional.empty();
    }
    ProjectStatus status = randomStatus();
    knownStatuses.put(projectCode, status);
    return Optional.of(status);
  }

  private ProjectStatus randomStatus() {
    Random random = new Random();
    int someNumber = random.nextInt(10) * random.nextInt(100);

    if (someNumber < 50) {
      return new ProjectStatus(someNumber, someNumber, 1, 0, 0, 0, Instant.now());
    } else if (someNumber < 500) {
      return new ProjectStatus(someNumber, someNumber, someNumber, 0, someNumber, someNumber,
          Instant.now());
    } else {
      return new ProjectStatus(someNumber, someNumber, someNumber - 3, 3, 0, 0, Instant.now());
    }
  }

  @Override
  public Optional<String> getForSample(String sampleCode) {
    int someNumber = new Random().nextInt(30);
    if (someNumber < 5) {
      return Optional.empty();
    } else if (someNumber < 10) {
      return Optional.of("METADATA_REGISTERED");
    } else if (someNumber < 20) {
      return Optional.of("SAMPLE_QC_FAIL");
    } else {
      return Optional.of("DATA_AVAILABLE");
    }
  }
}
