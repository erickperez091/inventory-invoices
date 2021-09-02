package com.example.invoices.handler;

import com.example.invoices.entity.InvoiceLine;
import org.springframework.http.ResponseEntity;

public interface InvoiceLineHandler {

    ResponseEntity<Object> getInvoiceLinesByInvoice ( String invoice_id );

    ResponseEntity<Object> addInvoiceLineByInvoiceAndProductId( InvoiceLine invoiceLine, String invoiceId );
}
