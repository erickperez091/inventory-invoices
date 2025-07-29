package com.example.invoices.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Log4j2
public class WebClientFilter{

    private static void logMethodAndUrl( ClientRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append( "METHOD: " );
        sb.append( request.method().name() );
        sb.append(" to ");
        sb.append(request.url());
        sb.append( " | Payload: " );
        sb.append( request.body() );
        logger.info(sb.toString());
    }

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            logMethodAndUrl(request);
            return Mono.just(request);
        });
    }
}
