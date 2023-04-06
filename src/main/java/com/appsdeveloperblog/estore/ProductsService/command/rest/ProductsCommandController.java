package com.appsdeveloperblog.estore.ProductsService.command.rest;

import com.appsdeveloperblog.estore.ProductsService.command.CreateProductCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {
    private final Environment environment;

    // command gateway is used to send commands to the command bus, command bus is a mechanism to
    // receive the commands and route then to appropriate command handler
    private final CommandGateway commandGateway;

    @Autowired
    public ProductsCommandController(Environment environment, CommandGateway commandGateway) {
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {

        // create our command object, then we need to send this command object to command bus for
        // processing, command is just a java class containing information to create a product
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .price(createProductRestModel.getPrice())
                .quantity(createProductRestModel.getQuantity())
                .title(createProductRestModel.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();

        String returnValue;
        returnValue = commandGateway.sendAndWait(createProductCommand);
        /*try {
            // send the createProductCommand to command bus


        } catch (Exception ex) {
            returnValue = ex.getLocalizedMessage();
        }*/
        return returnValue;

    }

    /*@GetMapping
    public String getProduct() {

        // if we print server.port it will print 0, because port will be assigned randomly. to access that we need to
        // use local.server.port
        return "HTTP GET handled " + environment.getProperty("local.server.port");
    }

    @PutMapping
    public String updateProduct() {
        return "HTTP PUT handled";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "HTTP DELETE handled";
    }*/
}
