package com.brpratt.simplenetes.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableScheduling
public class SimplenetesControllerApplication {
  public static void main(String[] args) {
    SpringApplication.run(SimplenetesControllerApplication.class, args);
  }

  @Configuration
  public static class AppConfig {
    @Bean
    public DockerClient dockerClient() {
      var dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

      var dockerHttpClient =
          new ApacheDockerHttpClient.Builder()
              .dockerHost(dockerConfig.getDockerHost())
              .sslConfig(dockerConfig.getSSLConfig())
              .build();

      return DockerClientImpl.getInstance(dockerConfig, dockerHttpClient);
    }

    @Bean
    public RestClient serverClient(
        RestClient.Builder builder, @Value("${simplenetes.server-host}") String serverHost) {
      return builder.baseUrl(String.format("http://%s:8080", serverHost)).build();
    }
  }
}
