package com.example.invoices.controller;

import com.example.invoices.entity.InvoiceLine;
import com.example.invoices.handler.InvoiceLineHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/invoice/v1" )
public class InvoiceLineController {

    private InvoiceLineHandler invoiceLineHandler;

    @Autowired
    public InvoiceLineController(InvoiceLineHandler invoiceLineHandler) {
        this.invoiceLineHandler = invoiceLineHandler;
    }

    @GetMapping( name = "get invoice lines by invoice", path = "/{invoice_id}/lines", value = "/{invoice_id}/lines", produces = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity<Object> getInvoiceLinesByInvoice ( @PathVariable( name = "invoice_id" ) String invoice_id ) {
        return invoiceLineHandler.getInvoiceLinesByInvoice( invoice_id );
    }

    @PatchMapping( value = "/{invoice_id}/line" )
    public ResponseEntity<Object> addInvoiceLineByInvoice ( @PathVariable( name = "invoice_id" ) String invoice_id, @RequestBody InvoiceLine invoiceLine ) {
        return invoiceLineHandler.addInvoiceLineByInvoiceAndProductId( invoiceLine, invoice_id );
    }

}
