package com.example.invoices.service.clients.impl;

import com.example.common.utilities.ConverterUtil;
import com.example.invoices.configuration.WebClientFilter;
import com.example.invoices.entity.Invoice;
import com.example.invoices.service.clients.ProductServiceClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.invoices.util.RequestLogEnhancer.enhance;

@Component
@RequiredArgsConstructor
public class ProductServiceClientImpl implements ProductServiceClient {

    private static final Logger logger = LoggerFactory.getLogger( ProductServiceClientImpl.class );

    private final WebClient.Builder webClientBuilder;
    private final ConverterUtil converterUtil;

    private WebClient webClient;

    @Value( "${service.general.protocol}" )
    private String serviceGeneralProtocol;

    @Value( "${product.service.name}" )
    private String inventoryServiceName;

    @Value( "${product.service.base-url}" )
    private String inventoryServiceBaseUrl;

    @Value( "${product.service.update-inventory-url}" )
    private String updateInventoryUrl;


    @PostConstruct
    private void init() {
        String inventoryServiceUrl = String.format( "%s%s/%s", this.serviceGeneralProtocol, this.inventoryServiceName, this.inventoryServiceBaseUrl );
        HttpClient httpClient = new HttpClient( ) {
            @Override
            public Request newRequest( URI uri ) {
                Request request = super.newRequest( uri );
                return enhance( request );
            }
        };

        this.webClient = this.webClientBuilder
                .baseUrl( inventoryServiceUrl )
                .clientConnector( new JettyClientHttpConnector(httpClient) )
                .filter( WebClientFilter.logRequest() )
                .build();
    }

    @Override
    public void updateProductsInventory( Invoice invoice ) {
        Map<String, Object> invoiceMap = this.invoiceLineToListMap( invoice );

        Mono<String> result = this.webClient
                .patch().uri( uriBuilder -> uriBuilder.path( updateInventoryUrl ).build(  ) )
                .contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON )
                .body( BodyInserters.fromValue( invoiceMap ) )
                .exchangeToMono( response -> {
                    if(response.statusCode().is2xxSuccessful()){
                        return response.bodyToMono( String.class );
                    } else if(response.statusCode().is4xxClientError()){
                        return Mono.just( "There was a 4xx Error" + response.statusCode() );
                    } else if(response.statusCode().is5xxServerError()){
                        return Mono.just( "There was a 5xx Error" + response.statusCode() );
                    } else {
                        return Mono.empty();
                    }
                });

        result.subscribe(s -> {
            System.out.println("Response FROM products: " + s);
        });

    }

    private Map< String, Object > invoiceLineToListMap( Invoice invoice ) {
        Map< String, Object > invoiceDTOMap = new HashMap<>();
        List< Map< String, Object > > productsDTO = new ArrayList<>();
        invoiceDTOMap.put( "invoiceStatus", invoice.getInvoiceStatus().name() );

        if ( CollectionUtils.isNotEmpty( invoice.getInvoiceLines() ) ) {
            productsDTO = invoice.getInvoiceLines()
                    .stream()
                    .map( invoiceLine -> {
                        Map< String, Object > invoiceLineMap = new HashMap<>();
                        invoiceLineMap.put( "id", invoiceLine.getProductId() );
                        invoiceLineMap.put( "description", invoiceLine.getProductDescription() );
                        invoiceLineMap.put( "units", invoiceLine.getUnits() );
                        return invoiceLineMap;
                    } ).toList();
        }
        invoiceDTOMap.put( "products", productsDTO );

        return invoiceDTOMap;
    }

}
