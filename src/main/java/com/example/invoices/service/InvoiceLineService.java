package com.example.invoices.service;

import com.example.invoices.entity.Invoice;
import com.example.invoices.entity.InvoiceLine;
import com.example.invoices.repository.InvoiceLineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceLineService {

    private static final Logger logger = LoggerFactory.getLogger( InvoiceLineService.class );

    private final InvoiceLineRepository repository;

    @Autowired
    public InvoiceLineService( InvoiceLineRepository repository ) {
        this.repository = repository;
    }

    public void save( InvoiceLine invoiceLine ) {
        repository.save( invoiceLine );
    }

    public Optional< List< InvoiceLine > > findInvoiceLinesByInvoice( Invoice invoice ) {
        return repository.findAllByInvoice( invoice );
    }

    public Optional< InvoiceLine > findInvoiceLineByInvoiceAndProductId( Invoice invoice, String productId ) {
        return repository.findByInvoiceAndProductId( invoice, productId );
    }
}
