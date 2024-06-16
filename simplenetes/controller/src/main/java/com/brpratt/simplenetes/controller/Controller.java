package com.brpratt.simplenetes.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;

@Component
public class Controller {
    private final RestClient serverClient;
    private final DockerClient dockerClient;

    public Controller(RestClient.Builder builder) {
        this.serverClient = builder.baseUrl("http://localhost:8080").build();

        var dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        var dockerHttpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(dockerConfig.getDockerHost())
            .sslConfig(dockerConfig.getSSLConfig())
            .build();

        this.dockerClient = DockerClientImpl.getInstance(dockerConfig, dockerHttpClient);
    }

    @Scheduled(fixedRate = 1000)
    public void reconcile() {
        var desired = getDesiredContainers();
        var actual = getActualContainers();
        var actions = calculateActions(desired, actual);
        process(actions);
    }

    private List<Container> getDesiredContainers() {
        return serverClient.get()
            .uri("/containers")
            .retrieve()
            .body(new ParameterizedTypeReference<List<Container>>() { });
    }

    private List<Container> getActualContainers() {
        return dockerClient.listContainersCmd()
            .withLabelFilter(Arrays.asList("simplenetes"))
            .exec()
            .stream()
            .map(c -> new Container(c.getNames()[0], c.getImage()))
            .toList();
    }

    private List<ControllerAction> calculateActions(List<Container> desired, List<Container> actual) {
        var toCreate = desired.stream()
            .filter(d -> actual.stream().noneMatch(a -> a.equals(d)))
            .map(d -> new ControllerAction(ControllerActionKind.CREATE, d));

        var toDelete = actual.stream()
            .filter(a -> desired.stream().noneMatch(d -> d.equals(a)))
            .map(a -> new ControllerAction(ControllerActionKind.DELETE, a));

        return Stream.concat(toCreate, toDelete).toList();
    }

    private void process(List<ControllerAction> actions) {
        actions.forEach(a -> {
            switch (a.kind()) {
                case CREATE -> createContainer(a.container());
                case DELETE -> deleteContainer(a.container());
            }
        });
    }

    private void createContainer(Container container) {
        dockerClient.pullImageCmd(container.image()).wait();

        dockerClient.createContainerCmd(container.image())
            .withName(container.name())
            .withLabels(Map.of("simplenetes", "true"))
            .exec();
    }

    private void deleteContainer(Container container) {
        dockerClient.removeContainerCmd(container.name())
            .exec();
    }
}
