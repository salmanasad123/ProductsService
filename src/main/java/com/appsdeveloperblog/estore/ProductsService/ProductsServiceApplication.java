package com.appsdeveloperblog.estore.ProductsService;

import com.appsdeveloperblog.estore.ProductsService.command.interceptors.CreateProductCommandInterceptor;
import com.appsdeveloperblog.estore.ProductsService.core.errorHandling.ProductServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.Configuration;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsServiceApplication.class, args);
    }

    // register CreateProductCommandInterceptor on the command bus
    @Autowired
    public void registerCreateProductCommandInterceptor(ApplicationContext applicationContext,
                                                        CommandBus commandBus) {

        commandBus.registerDispatchInterceptor(applicationContext.getBean(CreateProductCommandInterceptor.class));

    }

    // this method will be triggered when our application is starting up, spring framework will create
    // an object to eventProcessingConfigurer, and we can use this object to register ListenerInvocationErrorHandler
    // for a specific processing group
    @Autowired
    public void configure(EventProcessingConfigurer eventProcessingConfigurer) {

        eventProcessingConfigurer.registerListenerInvocationErrorHandler("product-group",
                (Configuration configuration) -> {
                    return new ProductServiceEventsErrorHandler();
                });
    }
}
