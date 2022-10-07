package com.example.invoices.controller;

import com.example.invoices.entity.Invoice;
import com.example.invoices.handler.InvoiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/invoice/v1" )
public class InvoiceController {

    private InvoiceHandler invoiceHandler;

    @Autowired
    public InvoiceController( InvoiceHandler invoiceHandler ) {
        this.invoiceHandler = invoiceHandler;
    }

    @GetMapping( value = "ping" )
    public ResponseEntity< String > ping() {
        return new ResponseEntity<>( "Microservice is up and running", HttpStatus.OK );
    }

    @PostMapping( name = "create", value = "/", path = "/", consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity< Object > create( @RequestBody Invoice invoice ) {
        return invoiceHandler.createInvoice( invoice );
    }

    @PutMapping( name = "update", value = "/", path = "/", consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity< Object > update( @RequestBody Invoice invoice ) {
        return invoiceHandler.updateInvoice( invoice );
    }

    @GetMapping( name = "get invoice by id", value = "/{id}", path = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE } )
    public ResponseEntity< Object > get( @PathVariable( name = "id" ) String id ) {
        return invoiceHandler.getInvoiceById( id );
    }
}
