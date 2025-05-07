package org.mrshoffen.tasktracker.task.config;


import org.mrshoffen.tasktracker.task.util.client.DeskClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public DeskClient workspaceClient(WebClient.Builder webClientBuilder) {
        return new DeskClient(webClientBuilder.baseUrl("http://desk-manager-rs").build());
    }

}
