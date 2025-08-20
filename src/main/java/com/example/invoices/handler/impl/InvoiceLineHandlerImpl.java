package com.example.invoices.handler.impl;

import com.example.common.entity.EnumUtil.EventType;
import com.example.common.entity.MessageEvent;
import com.example.common.utilities.ConverterUtil;
import com.example.invoices.entity.Invoice;
import com.example.invoices.entity.InvoiceLine;
import com.example.invoices.handler.InvoiceLineHandler;
import com.example.invoices.messaging.InvoicePublisher;
import com.example.invoices.service.InvoiceLineService;
import com.example.invoices.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
public class InvoiceLineHandlerImpl implements InvoiceLineHandler {

    private final InvoiceLineService invoiceLineService;
    private final InvoicePublisher invoicePublisher;
    private final InvoiceService invoiceService;
    private final ConverterUtil converterUtil;

    @Value( "${invoice.discount.percent:0.0}" )
    private BigDecimal discountPercentage;

    @Value( "${invoice.tax.percent:0.0}" )
    private BigDecimal taxPercentage;

    @Override
    public ResponseEntity< Object > getInvoiceLinesByInvoice( String invoice_id ) {
        Invoice invoice = new Invoice();
        invoice.setId( invoice_id );
        Optional< List< InvoiceLine > > optionalInvoiceLineList = invoiceLineService.findInvoiceLinesByInvoice( invoice );
        if ( optionalInvoiceLineList.isEmpty() ) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, String.format( "Unable to find Invoice Lines for Invoice with ID %s Not Found", invoice.getId() ) );
        }
        List< InvoiceLine > invoiceLines = optionalInvoiceLineList.get();
        return new ResponseEntity<>( invoiceLines, HttpStatus.FOUND );
    }

    @Override
    public ResponseEntity< Object > addInvoiceLineByInvoiceAndProductId( InvoiceLine invoiceLine, String invoiceId ) {
        Optional< Invoice > optionalInvoiceFromDb = invoiceService.findById( invoiceId );
        Invoice invoiceFromDb = null;
        if ( optionalInvoiceFromDb.isPresent() ) {
            invoiceFromDb = optionalInvoiceFromDb.get();
            if ( CollectionUtils.isNotEmpty( invoiceFromDb.getInvoiceLines() ) ) {
                Optional< InvoiceLine > optionalInvoiceLine = invoiceFromDb.getInvoiceLines().stream().filter( il -> il.getProductId().equals( invoiceLine.getProductId() ) ).findFirst();
                if ( optionalInvoiceLine.isPresent() ) {
                    InvoiceLine invoiceLineFromDb = optionalInvoiceLine.get();
                    this.converterUtil.copyProperties( invoiceLine, invoiceLineFromDb );
                } else {
                    invoiceFromDb.getInvoiceLines().add( invoiceLine );
                }
            }
        }
        assert invoiceFromDb != null;
        invoiceFromDb.calculateTotal( discountPercentage, taxPercentage );
        Map< String, Object > payload = this.converterUtil.objectToMap( invoiceFromDb );
        MessageEvent messageEvent = new MessageEvent( EventType.UPDATE_INVOICE, payload );
        this.invoicePublisher.sendEvent( messageEvent );
        return new ResponseEntity<>( invoiceFromDb, HttpStatus.FOUND );
    }
}
