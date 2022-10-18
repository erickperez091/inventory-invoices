package com.example.invoices.configuration;

import com.example.invoices.consumer.InvoiceConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class WebClientFilter{
    private static final Logger logger = LoggerFactory.getLogger( InvoiceConsumer.class );

    private static void logMethodAndUrl( ClientRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.method().name());
        sb.append(" to ");
        sb.append(request.url());
        /*sb.append( " | Payload: " );
        sb.append( BodyInserters.fromValue( request.body() ) );*/
        logger.info(sb.toString());
    }

    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            logMethodAndUrl(request);
            return Mono.just(request);
        });
    }
}
