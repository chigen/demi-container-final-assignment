package com.example.orderservice.config;

import feign.FeignException;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, feign.Response response) {
        log.error("Feign client error - Method: {}, Status: {}, Reason: {}",
                methodKey, response.status(), response.reason());

        switch (response.status()) {
            case 400:
                return new RuntimeException("Bad request to inventory service");
            case 404:
                return new RuntimeException("Inventory service endpoint not found");
            case 500:
                return new RuntimeException("Internal server error in inventory service");
            default:
                return new RuntimeException("Unexpected error from inventory service: " + response.status());
        }
    }
}
