package com.example.invoices.handler;

import com.example.invoices.entity.Invoice;
import org.springframework.http.ResponseEntity;

public interface InvoiceHandler {

    ResponseEntity<Object> createInvoice ( Invoice invoice );

    ResponseEntity<Object> updateInvoice ( Invoice invoice );

    ResponseEntity<Object> getInvoiceById ( String id );
}
