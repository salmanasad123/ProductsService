package com.appsdeveloperblog.estore.ProductsService.query;

import com.appsdeveloperblog.estore.ProductsService.core.data.ProductEntity;
import com.appsdeveloperblog.estore.ProductsService.core.data.ProductsRepository;
import com.appsdeveloperblog.estore.ProductsService.query.rest.ProductRestModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// handler class that will handle the query object sent to query bus
@Component
public class ProductsQueryHandler {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsQueryHandler(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    // to be able to handle findProductQuery this method needs to be annotated with QueryHandler annotation
    @QueryHandler
    List<ProductRestModel> findProducts(FindProductQuery findProductQuery) {

        List<ProductRestModel> productRestModelList = new ArrayList<>();

        List<ProductEntity> productEntityList = productsRepository.findAll();

        for (ProductEntity productEntity : productEntityList) {
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(productEntity, productRestModel);
            productRestModelList.add(productRestModel);
        }

        return productRestModelList;
    }

}
