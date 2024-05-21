package com.example.invoices.service.clients;

import com.example.invoices.entity.Invoice;

import java.net.URISyntaxException;

public interface ProductServiceClient {

    void updateProductsInventory( Invoice invoice ) throws URISyntaxException;

}
