package com.appsdeveloperblog.estore.ProductsService.query;

import com.appsdeveloperblog.estore.ProductsService.core.data.ProductEntity;
import com.appsdeveloperblog.estore.ProductsService.core.data.ProductsRepository;
import com.appsdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.core.events.ProductReserveEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// this class is event handler that will handle productCreatedEvent
@Component
@ProcessingGroup("product-group")
public class ProductsEventHandler {
    private final ProductsRepository productsRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(ProductsEventHandler.class);

    @Autowired
    public ProductsEventHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {

        // persist the product details into the database
        ProductEntity productEntity = new ProductEntity();

        BeanUtils.copyProperties(productCreatedEvent, productEntity);

        productsRepository.save(productEntity);
    }

    // this method will be triggered when the productReserveEvent is published.
    @EventHandler
    public void on(ProductReserveEvent productReserveEvent) {

        ProductEntity productEntity = productsRepository.findByProductId(productReserveEvent.getProductId());

        productEntity.setQuantity(productEntity.getQuantity() - productReserveEvent.getQuantity());

        productsRepository.save(productEntity);

        LOGGER.info("ProductReserveEvent is called for productId: " + productReserveEvent.getProductId() +
                " and orderId: " + productReserveEvent.getOrderId());
    }

    // if we need to roll back transactions and undo the changes made by the event handler methods (on method in
    // our case) , we need to either not handle the exception or if we handle it we need to propagate it further
    // up the chain so this exception can be handled by another exception handler, and propagate it further up
    // which will eventually roll back our transaction, and this is possible if our processing group is configured
    // to use subscribing event processor which we have set in properties file.
    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {

        // re-throwing the exception, but re-throwing is not enough, axon framework will handle the exception
        // log it and will continue the execution. we need to handle it again and propagate it further up
        // to roll back transaction, and to do that we need to create our own error handler class that will\
        // catch exception thrown from here and propagate it further up.
        throw exception;
    }

    // annotate this method, so it can act as an exception handler
    // this will only handle exceptions that are thrown from the event handling functions within this class only
    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException illegalArgumentException) {
        // log error message
    }
}
