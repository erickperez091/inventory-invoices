package com.example.invoices.repository;

import com.example.invoices.entity.Invoice;
import com.example.invoices.entity.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, String> {

    Optional<List<InvoiceLine>> findAllByInvoice ( Invoice invoice );

    Optional<InvoiceLine> findByInvoiceAndProductId( @Param("invoice") Invoice invoice, @Param ( "product_id" ) String product_id);
}
