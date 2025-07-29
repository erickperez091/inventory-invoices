package com.example.invoices.service;

import com.example.invoices.entity.Invoice;
import com.example.invoices.entity.InvoiceLine;
import com.example.invoices.repository.InvoiceLineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class InvoiceLineService {


    private final InvoiceLineRepository repository;

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
