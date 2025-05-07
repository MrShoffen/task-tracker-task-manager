package org.mrshoffen.tasktracker.task;


import org.mrshoffen.tasktracker.task.model.dto.links.TaskDtoLinksInjector;
import org.mrshoffen.tasktracker.task.client.DeskClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TaskBeansConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public DeskClient workspaceClient(WebClient.Builder webClientBuilder) {
        return new DeskClient(webClientBuilder.baseUrl("http://desk-manager-rs").build());
    }

    @Bean
    public TaskDtoLinksInjector taskDtoLinksInjector(@Value("${app.gateway.api-prefix}") String apiPrefix) {
        return new TaskDtoLinksInjector(apiPrefix);
    }

}
