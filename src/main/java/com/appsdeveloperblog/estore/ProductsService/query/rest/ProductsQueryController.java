package com.appsdeveloperblog.estore.ProductsService.query.rest;

import com.appsdeveloperblog.estore.ProductsService.query.FindProductQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductsQueryController {

    // We need query gateway to send findProductQuery to query bus
    private final QueryGateway queryGateway;

    @Autowired
    public ProductsQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<ProductRestModel> getProducts(){

        // send the query to query bus so that it will be routed to query handler
        // and query handler will use the jpa repository to get the list of products

        FindProductQuery findProductQuery = new FindProductQuery();

        // query method needs a query object and response type object
        List<ProductRestModel> products = queryGateway.query(findProductQuery,
                ResponseTypes.multipleInstancesOf(ProductRestModel.class)).join();

        return products;
    }

}
