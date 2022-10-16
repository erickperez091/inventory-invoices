package com.example.invoices.service.clients.impl;

import com.example.common.utilities.ConverterUtil;
import com.example.invoices.configuration.WebClientFilter;
import com.example.invoices.entity.InvoiceLine;
import com.example.invoices.service.clients.ProductServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductServiceClientImpl implements ProductServiceClient {

    private static final Logger logger = LoggerFactory.getLogger( ProductServiceClientImpl.class );

    private final WebClient.Builder webClientBuilder;
    private final ConverterUtil converterUtil;

    private String inventoryServiceUrl;
    private WebClient webClient;

    @Value( "${service.general.protocol}" )
    private String serviceGeneralProtocol;

    @Value( "${product.service.name}" )
    private String inventoryServiceName;

    @Value( "${product.service.base-url}" )
    private String invetoryServiceBaseUrl;

    @Autowired
    public ProductServiceClientImpl( WebClient.Builder webClientBuilder, ConverterUtil converterUtil ) {
        this.webClientBuilder = webClientBuilder;
        this.converterUtil = converterUtil;
    }

    @PostConstruct
    private void init() {
        this.inventoryServiceUrl = String.format( "%s%s/%s", this.serviceGeneralProtocol, this.inventoryServiceName, this.invetoryServiceBaseUrl );
        this.webClient = this.webClientBuilder
                .baseUrl( this.inventoryServiceUrl )
                .filter( WebClientFilter.logRequest() )
                .build();
    }

    @Override
    public void updateProductsInventory( Set< InvoiceLine > invoiceLines ) throws URISyntaxException {

        List< Map< String, Object > > invoiceLinesMapList = new ArrayList<>();

        invoiceLinesMapList = invoiceLines
                .stream()
                .map( invoiceLine -> {
                    Map< String, Object > invoiceLineMap = new HashMap<>();
                    invoiceLineMap.put( "id", invoiceLine.getProductId() );
                    invoiceLineMap.put( "description", invoiceLine.getProductDescription() );
                    invoiceLineMap.put( "units", invoiceLine.getUnits() );
                    return invoiceLineMap;
                } )
                .collect( Collectors.toList() );

        String response = this.webClient
                .patch()
                .uri( uriBuilder -> uriBuilder.path( "/products-stock" ).build() )
                .contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON )
                .body( BodyInserters.fromValue( invoiceLinesMapList ) )
                .exchange()
                .timeout( Duration.ofSeconds( 3 ) )
                .flatMap( clientResponse -> {
                    if ( clientResponse.statusCode().is5xxServerError() ) {
                        clientResponse.body( ( clientHttpResponse, context ) -> {
                            return clientHttpResponse.getBody();
                        } );
                        return clientResponse.bodyToMono( String.class );
                    } else
                        return clientResponse.bodyToMono( String.class );
                } )
                .block();

        System.out.println( response );
    }
}
