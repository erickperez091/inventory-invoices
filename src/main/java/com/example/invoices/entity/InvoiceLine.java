package com.example.invoices.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Table( name = "invoice_line" )
@Entity
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceLine implements Serializable {

    @Id
    @Column( name = "id", unique = true, nullable = false )
    private String id;

    @Column( name = "product_id", nullable = false )
    private String productId;

    @Column( name = "product_description", nullable = false )
    private String productDescription;

    @Column( name = "units", nullable = false )
    private int units;

    @Column( name = "product_price", nullable = false )
    private BigDecimal productPrice;

    @Column( name = "total_line", nullable = false )
    private BigDecimal totalInvoiceLine;

    @ManyToOne
    @JoinColumn( name = "invoice_id", nullable = false, updatable = false )
    //@JsonBackReference
    @JsonIgnoreProperties( { "invoiceLines" } )
    private Invoice invoice;

}
