package com.appsdeveloperblog.estore.ProductsService.command;

import com.appsdeveloperblog.estore.ProductsService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.core.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.core.events.ProductReserveEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

// this class needs to be annotated with aggregate annotation so axon knows that this is an aggregate class
@Aggregate
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;


    // we need a default constructor so axon framework can create an instance of this class
    public ProductAggregate() {

    }

    // when create product command is dispatched this constructor will be invoked, and we can validate
    // create product command here
    // CreateProductCommand createProductCommand is the command handled by this constructor
    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {

        // Validate Create Product Command
        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be less or equal than zero");
        }

        if (createProductCommand.getTitle() == null ||
                createProductCommand.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        // create new instance of product created event
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();

        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        // publish the event, by calling the apply method will dispatch the event to all the event
        // handlers in this aggregate class, so that state of this aggregate can be updated with new information.
        // this apply will trigger methods annotated with EventSourcingHandler in this aggregate class.
        AggregateLifecycle.apply(productCreatedEvent);

        // if we throw exception here even after the apply method is called the event will not
        // be published and command will be rolled back because axon framework doesn't update eventStore
        // immediately rather it stages the event to be executed

    }

    // event sourcing handler method as these methods are used to update the state in the aggregate
    // (update the current state of the aggregate with the latest).
    // by naming convention we named with method as on.
    // to allow this method to handle events we need to mark it with @EventSourcingHandler annotation.
    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {

        // avoid adding business logic here, use this to only update aggregate state
        this.productId = productCreatedEvent.getProductId();
        this.title = productCreatedEvent.getTitle();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
    }

    // this annotation will make this method handle commands
    // we do not need to query database to get the product detail, this will be handled by axon for us,
    // when this aggregate is loaded its state is automatically restored by axon framework.
    // Axon will create new object of this class, and it will replay the events from the event store
    // to bring this aggregate to current state.
    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand) {

        if (quantity < reserveProductCommand.getQuantity()) {
            throw new IllegalArgumentException("Insufficient number of items in stock");
        }
        // if everything is ok we will publish the product reserve event that will be handled by Saga class
        // in orders microservice
        ProductReserveEvent productReserveEvent = ProductReserveEvent.builder()
                .productId(reserveProductCommand.getProductId())
                .quantity(reserveProductCommand.getQuantity())
                .userId(reserveProductCommand.getUserId())
                .orderId(reserveProductCommand.getOrderId())
                .build();

        // when productReserveEvent is applied axon framework will persis the productReserveEvent into the
        // event store.
        // since the quantity is changed by this event , we need to update the read database as well.
        AggregateLifecycle.apply(productReserveEvent);
    }

    @EventSourcingHandler
    public void on(ProductReserveEvent productReserveEvent) {

        // avoid adding business logic here, use this to only update aggregate state
        // update aggregate values here which will be persisted to the event store
        // we will only update the quantity values because other values are not effected by this event.
        this.quantity = quantity - productReserveEvent.getQuantity();
    }
}
