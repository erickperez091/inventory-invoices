package com.example.invoices.consumer.processor;

import com.example.common.utilities.ConverterUtil;
import com.example.invoices.entity.Invoice;
import com.example.invoices.service.InvoiceLineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class InvoiceLineProcessor {

    private final InvoiceLineService invoiceLineService;
    private final ConverterUtil converterUtil;


    public void storeOrRefresh( Map< String, Object > payload ) {
        logger.info( "START | Save or Update Invoice Line {}", payload );
    }

    public void refresh( Map< String, Object > payload ) {
        logger.info( "START | Update Invoice {}", payload );
        Invoice invoice = this.converterUtil.mapToObject( payload, Invoice.class );
        Invoice invoiceFromDb = new Invoice();//invoiceService.findById( invoice.getId( ) ).get( );
        this.converterUtil.copyProperties( invoice, invoiceFromDb );
        //invoiceLineService.save( invoiceFromDb );
        logger.info( "FINISH | Update Invoice {}", payload );
    }

    public void delete( Map< String, Object > payload ) {
        logger.info( "START | Delete Product {}", payload );
        String id = (String) payload.get( "id" );
        //invoiceLineService.delete( id );
        logger.info( "FINISH | Delete Product {}", payload );
    }
}
