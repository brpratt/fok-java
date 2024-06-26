package com.brpratt.simplenetes.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class Controller {

  private final Logger logger = LoggerFactory.getLogger(Controller.class);
  private final RestClient serverClient;
  private final DockerClient dockerClient;

  public Controller(DockerClient dockerClient, RestClient serverClient) {
    this.dockerClient = dockerClient;
    this.serverClient = serverClient;
  }

  @Scheduled(fixedRate = 1000)
  public void reconcile() {
    var desired = getDesiredContainers();
    var actual = getActualContainers();
    var actions = calculateActions(desired, actual);
    process(actions);
  }

  private List<Container> getDesiredContainers() {
    return serverClient
        .get()
        .uri("/containers")
        .retrieve()
        .body(new ParameterizedTypeReference<List<Container>>() {});
  }

  private List<Container> getActualContainers() {
    return dockerClient
        .listContainersCmd()
        .withLabelFilter(Map.of("simplenetes", "true"))
        .exec()
        .stream()
        .map(c -> new Container(c.getNames()[0].substring(1), c.getImage()))
        .toList();
  }

  private List<ControllerAction> calculateActions(List<Container> desired, List<Container> actual) {
    var toCreate =
        desired.stream()
            .filter(d -> actual.stream().noneMatch(a -> a.equals(d)))
            .map(d -> new ControllerAction(ControllerActionKind.CREATE, d));

    var toDelete =
        actual.stream()
            .filter(a -> desired.stream().noneMatch(d -> d.equals(a)))
            .map(a -> new ControllerAction(ControllerActionKind.DELETE, a));

    return Stream.concat(toCreate, toDelete).toList();
  }

  private void process(List<ControllerAction> actions) {
    actions.forEach(
        a -> {
          switch (a.kind()) {
            case CREATE -> createContainer(a.container());
            case DELETE -> deleteContainer(a.container());
          }
        });
  }

  private void createContainer(Container container) {
    try {
      dockerClient
          .pullImageCmd(container.image())
          .exec(new ResultCallback.Adapter<>() {})
          .awaitCompletion();
    } catch (InterruptedException e) {
      logger.error("Failed to pull image: {}", container.image());
      return;
    }

    dockerClient
        .createContainerCmd(container.image())
        .withName(container.name())
        .withLabels(Map.of("simplenetes", "true"))
        .exec();

    dockerClient.startContainerCmd(container.name()).exec();
  }

  private void deleteContainer(Container container) {
    dockerClient.removeContainerCmd(container.name()).withForce(true).exec();
  }
}
