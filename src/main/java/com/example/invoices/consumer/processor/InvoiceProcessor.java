package com.example.invoices.consumer.processor;

import com.example.common.entity.EnumUtil.UUIDType;
import com.example.common.utilities.ConverterUtil;
import com.example.common.utilities.IdGeneratorService;
import com.example.common.utilities.impl.FriendlyIdServiceImpl;
import com.example.common.utilities.PropertiesUtil;
import com.example.invoices.entity.Invoice;
import com.example.invoices.entity.InvoiceLine;
import com.example.invoices.service.InvoiceService;
import com.example.invoices.service.clients.ProductServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
public class InvoiceProcessor {

    private final InvoiceService invoiceService;
    private final ConverterUtil converterUtil;
    private final IdGeneratorService idGeneratorService;
    private final PropertiesUtil propertiesUtil;
    private final ProductServiceClient productServiceClient;


    public void store( Map< String, Object > payload ) throws URISyntaxException {
        logger.info( "START | Create Invoice {}", payload );
        Invoice invoice = this.converterUtil.mapToObject( payload, Invoice.class );
        invoice.getInvoiceLines().stream().forEach( invoiceLine -> {
            invoiceLine.setId( this.idGeneratorService.generateId( UUIDType.SHORT ) );
            invoiceLine.setInvoice( invoice );
        } );
        this.productServiceClient.updateProductsInventory( invoice );
        this.invoiceService.save( invoice );
        logger.info( "FINISH | Create Invoice {}", payload );
    }

    @Transactional( propagation = Propagation.REQUIRED )
    public void refresh( Map< String, Object > payload ) {
        logger.info( "START | Update Invoice {}", payload );
        Invoice invoice = this.converterUtil.mapToObject( payload, Invoice.class );
        Invoice invoiceFromDb = invoiceService.findById( invoice.getId() ).get();
        this.converterUtil.copyProperties( invoice, invoiceFromDb, this.propsToIgnore( invoiceFromDb, "invoiceLines" ) );
        this.copyLinesToInvoice( invoice.getInvoiceLines(), invoiceFromDb.getInvoiceLines() );
        this.generateIdInvoiceLines( invoiceFromDb );
        //invoiceService.save( invoiceFromDb );
        logger.info( "FINISH | Update Invoice {}", payload );
    }

    public void delete( Map< String, Object > payload ) {
        logger.info( "START | Delete Product {}", payload );
        String id = (String) payload.get( "id" );
        invoiceService.delete( id );
        logger.info( "FINISH | Delete Product {}", payload );
    }


    // TODO Copy elements from Request to Target for both cases (Add or Remove a new line)
    private void copyLinesToInvoice( Set< InvoiceLine > invoiceLinesRequest, Set< InvoiceLine > invoiceLinesTarget ) {
        for ( InvoiceLine invoiceLine : invoiceLinesRequest ) {
            Optional< InvoiceLine > optionalInvoiceLine = invoiceLinesTarget.stream().filter( il -> il.getProductId().equalsIgnoreCase( invoiceLine.getProductId() ) ).findFirst();
            optionalInvoiceLine.ifPresentOrElse( invoiceLineTarget -> {
                this.converterUtil.copyProperties( invoiceLine, invoiceLineTarget, "invoice" );
            }, () ->
            {
                invoiceLinesTarget.add( invoiceLine );
            } );
        }
    }

    private void generateIdInvoiceLines( Invoice invoice ) {
        if ( CollectionUtils.isNotEmpty( invoice.getInvoiceLines() ) ) {
            invoice.getInvoiceLines().forEach( invoiceLine -> {
                if ( StringUtils.isBlank( invoiceLine.getId() ) ) {
                    invoiceLine.setId( this.idGeneratorService.generateId( UUIDType.SHORT ) );
                }
                invoiceLine.setInvoice( invoice );
            } );
        }
    }

    private String[] propsToIgnore( Object source, String... props ) {
        String[] propsToIgnore = this.propertiesUtil.getNullProperties( source );
        Set< String > properties = new HashSet<>();
        properties.add( "id" );
        properties.addAll( Arrays.asList( props ) );
        properties.addAll( Arrays.asList( propsToIgnore ) );
        return properties.toArray( new String[ 0 ] );
    }
}
