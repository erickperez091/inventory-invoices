package com.example.invoices.service.clients.impl;

import com.example.common.utilities.ConverterUtil;
import com.example.invoices.configuration.WebClientFilter;
import com.example.invoices.entity.Invoice;
import com.example.invoices.service.clients.ProductServiceClient;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.invoices.util.RequestLogEnhancer.enhance;

@Component
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

    @Autowired
    public ProductServiceClientImpl( WebClient.Builder webClientBuilder, ConverterUtil converterUtil ) {
        this.webClientBuilder = webClientBuilder;
        this.converterUtil = converterUtil;
    }

    @PostConstruct
    private void init() {
        String inventoryServiceUrl = String.format( "%s%s/%s", this.serviceGeneralProtocol, this.inventoryServiceName, this.inventoryServiceBaseUrl );
        SslContextFactory sslContextFactory = new SslContextFactory.Client();
        HttpClient httpClient = new HttpClient( sslContextFactory ) {
            @Override
            public Request newRequest( URI uri ) {
                Request request = super.newRequest( uri );
                return enhance( request );
            }
        };

        this.webClient = this.webClientBuilder
                .baseUrl( inventoryServiceUrl )
                .clientConnector( new JettyClientHttpConnector( httpClient ) )
                .filter( WebClientFilter.logRequest() )
                .build();
    }

    @Override
    public void updateProductsInventory( Invoice invoice ) {

        Map< String, Object > invoiceMap = this.invoiceLineToListMap( invoice );

        Mono< String > response = this.webClient
                .patch()
                .uri( uriBuilder -> uriBuilder.path( updateInventoryUrl ).build() )
                .contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON )
                .body( BodyInserters.fromValue( invoiceMap ) )
                .exchange()
                .timeout( Duration.ofSeconds( 3 ) )
                .flatMap( clientResponse -> {
                    if ( clientResponse.statusCode().is5xxServerError() ) {
                        clientResponse.body( ( clientHttpResponse, context ) -> clientHttpResponse.getBody() );
                    }
                    return clientResponse.bodyToMono( String.class );
                } );
        response.subscribe( message -> {
            System.out.println("RESPONSE FROM SERVER: " + message);
        } );
    }

    private Map< String, Object > invoiceLineToListMap( Invoice invoice ) {
        Map< String, Object > invoiceDTOMap = new HashMap<>();
        List< Map< String, Object > > productsDTO = new ArrayList<>();
        invoiceDTOMap.put( "invoiceStatus", invoice.getInvoiceStatus().name() );
        if ( !CollectionUtils.isEmpty( invoice.getInvoiceLines() ) ) {
            productsDTO = invoice.getInvoiceLines()
                    .stream()
                    .map( invoiceLine -> {
                        Map< String, Object > invoiceLineMap = new HashMap<>();
                        invoiceLineMap.put( "id", invoiceLine.getProductId() );
                        invoiceLineMap.put( "description", invoiceLine.getProductDescription() );
                        invoiceLineMap.put( "units", invoiceLine.getUnits() );
                        return invoiceLineMap;
                    } ).collect( Collectors.toList() );
        }
        invoiceDTOMap.put( "products", productsDTO );

        return invoiceDTOMap;
    }

}
