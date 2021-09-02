package com.example.invoices.repository;

import com.example.invoices.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository <Invoice, String> {
}
