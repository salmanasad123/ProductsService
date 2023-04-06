package com.appsdeveloperblog.estore.ProductsService.core.errorHandling;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

public class ProductServiceEventsErrorHandler implements ListenerInvocationErrorHandler {

    @Override
    public void onError(Exception exception, EventMessage<?> event, EventMessageHandler eventHandler)
            throws Exception {

        // throw exception further up so this will lead to transaction roll back.
        throw exception;
    }
}
