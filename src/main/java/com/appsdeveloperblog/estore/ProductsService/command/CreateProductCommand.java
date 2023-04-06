package com.appsdeveloperblog.estore.ProductsService.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

// this is our command class, this indicates that we show an intent to change/modify something
// so in our case we show an intent to create new product
// since this is a read only class, so we set all the properties to final
@Builder
@Data
public class CreateProductCommand {

    @TargetAggregateIdentifier
    private final String productId;
    private final String title;
    private final BigDecimal price;
    private final Integer quantity;
}
