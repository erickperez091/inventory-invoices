package com.example.invoices.handler.impl;

import com.example.common.entitty.EnumUtil.EventType;
import com.example.common.entitty.EnumUtil.UUIDType;
import com.example.common.entitty.MessageEvent;
import com.example.common.utilities.ConverterUtil;
import com.example.common.utilities.IdUtil;
import com.example.invoices.entity.Invoice;
import com.example.invoices.handler.InvoiceHandler;
import com.example.invoices.producer.InvoiceProducer;
import com.example.invoices.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class InvoiceHandlerImpl implements InvoiceHandler {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceProducer invoiceProducer;

    @Value( "${invoice.discount.percent:0.0}" )
    private BigDecimal discountPercentage;

    @Value( "${invoice.tax.percent:0.0}" )
    private BigDecimal taxPercentage;

    @Override
    public ResponseEntity<Object> createInvoice ( Invoice invoice ) {
        invoice.setId( IdUtil.generateId( UUIDType.SHORT ) );
        invoice.calculateTotal( discountPercentage, taxPercentage );
        Map<String, Object> invoicePayload = ConverterUtil.objectToMap( invoice );
        MessageEvent messageEvent = new MessageEvent( EventType.CREATE_INVOICE, invoicePayload );
        invoiceProducer.sendMessage( messageEvent );
        return new ResponseEntity<>( invoice, HttpStatus.OK );
    }

    @Override
    public ResponseEntity<Object> updateInvoice ( Invoice invoice ) {
        Optional<Invoice> optionalInvoiceFromDb = invoiceService.findById( invoice.getId( ) );
        if( optionalInvoiceFromDb.isEmpty( ) ){
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, String.format( "Unable to find Invoice Lines for Invoice with ID %s Not Found", invoice.getId( ) ) );
        }
        invoice.calculateTotal( discountPercentage, taxPercentage );
        Map<String, Object> payload = ConverterUtil.objectToMap( invoice );
        MessageEvent messageEvent = new MessageEvent( EventType.UPDATE_INVOICE, payload );
        invoiceProducer.sendMessage( messageEvent );
        return new ResponseEntity<>( invoice, HttpStatus.OK );
    }

    @Override
    public ResponseEntity<Object> getInvoiceById ( String id ) {
        Optional<Invoice> invoiceOptional = invoiceService.findById( id );
        if ( invoiceOptional.isEmpty( ) ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, String.format( "Unable to find Invoice, Invoice with ID %s Not Found", id ) );
        }
        Invoice invoice = invoiceOptional.get( );
        return new ResponseEntity<>( invoice, HttpStatus.FOUND );
    }


}
