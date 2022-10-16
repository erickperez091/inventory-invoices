package com.example.invoices.service.clients;

import com.example.invoices.entity.InvoiceLine;

import java.net.URISyntaxException;
import java.util.Set;

public interface ProductServiceClient {

    void updateProductsInventory( Set<InvoiceLine> invoiceLines ) throws URISyntaxException;

}
