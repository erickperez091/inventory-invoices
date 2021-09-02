package com.example.invoices.consumer.processor;

import com.example.common.utilities.ConverterUtil;
import com.example.invoices.entity.Invoice;
import com.example.invoices.service.InvoiceLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InvoiceLineProcessor {

    private static final Logger logger = LoggerFactory.getLogger( InvoiceLineProcessor.class );

    @Autowired
    private InvoiceLineService invoiceLineService;

    public void storeOrRefresh ( Map<String, Object> payload ) {
        logger.info( "START | Save or Update Invoice Line {}", payload );
    }

    public void refresh ( Map<String, Object> payload ) {
        logger.info( "START | Update Invoice {}", payload );
        Invoice invoice = ConverterUtil.mapToObject( payload, Invoice.class );
        Invoice invoiceFromDb = new Invoice(  );//invoiceService.findById( invoice.getId( ) ).get( );
        ConverterUtil.copyProperties( invoice, invoiceFromDb);
        //invoiceLineService.save( invoiceFromDb );
        logger.info( "FINISH | Update Invoice {}", payload );
    }

    public void delete ( Map<String, Object> payload ) {
        logger.info( "START | Delete Product {}", payload );
        String id = ( String ) payload.get( "id" );
        //invoiceLineService.delete( id );
        logger.info( "FINISH | Delete Product {}", payload );
    }
}
