package com.brpratt.diceroll;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.kubernetes.client.extended.controller.Controller;
import io.kubernetes.client.extended.controller.builder.ControllerBuilder;
import io.kubernetes.client.informer.SharedIndexInformer;
import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.generic.GenericKubernetesApi;

@SpringBootApplication
@EnableScheduling
public class DiceRollOperatorApplication {
    public static void main(String[] args) {
		SpringApplication.run(DiceRollOperatorApplication.class, args);
	}

	@Configuration
  	public static class AppConfig {
		@Bean
		public CommandLineRunner runner(SharedInformerFactory sharedInformerFactory, Controller diceRollController) {
			return args -> {
				sharedInformerFactory.startAllRegisteredInformers();
				diceRollController.run();
			};
		}

		@Bean
		public SharedIndexInformer<DiceRoll> podInformer(ApiClient apiClient, SharedInformerFactory sharedInformerFactory) {
			GenericKubernetesApi<DiceRoll, DiceRollList> genericApi =
				new GenericKubernetesApi<>(DiceRoll.class, DiceRollList.class, "example.com", "v1", "dicerolls", apiClient);
			return sharedInformerFactory.sharedIndexInformerFor(genericApi, DiceRoll.class, 0);
		}

		@Bean
		public Controller diceRollController(SharedInformerFactory sharedInformerFactory, DiceRollReconciler reconciler) {
			return ControllerBuilder.defaultBuilder(sharedInformerFactory)
				.watch((q) -> {
					return ControllerBuilder.controllerWatchBuilder(DiceRoll.class, q)
						.build();
				})
				.withReconciler(reconciler)
				.withWorkerCount(2)
				.build();
		}
  	}
}
