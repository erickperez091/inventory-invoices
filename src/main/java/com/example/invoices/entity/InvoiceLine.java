package com.example.invoices.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Table(name = "invoice_line")
@Entity
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
public class InvoiceLine implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_description", nullable = false)
    private String productDescription;

    @Column(name = "units", nullable = false)
    private int units;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(name = "total_line", nullable = false)
    private BigDecimal totalInvoiceLine;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false, updatable = false)
    //@JsonBackReference
    @JsonIgnoreProperties({"invoiceLines"})
    private Invoice invoice;

    public InvoiceLine ( String id, String productId, String productDescription, int units, BigDecimal productPrice, BigDecimal totalInvoiceLine, Invoice invoice ) {
        this.id = id;
        this.productId = productId;
        this.productDescription = productDescription;
        this.units = units;
        this.productPrice = productPrice;
        this.totalInvoiceLine = totalInvoiceLine;
        this.invoice = invoice;
    }

    public InvoiceLine ( ) {
    }

    public String getId ( ) {
        return id;
    }

    public void setId ( String id ) {
        this.id = id;
    }

    public String getProductId ( ) {
        return productId;
    }

    public void setProductId ( String productId ) {
        this.productId = productId;
    }

    public String getProductDescription ( ) {
        return productDescription;
    }

    public void setProductDescription ( String productDescription ) {
        this.productDescription = productDescription;
    }

    public int getUnits ( ) {
        return units;
    }

    public void setUnits ( int units ) {
        this.units = units;
    }

    public BigDecimal getProductPrice ( ) {
        return productPrice;
    }

    public void setProductPrice ( BigDecimal productPrice ) {
        this.productPrice = productPrice;
    }

    public BigDecimal getTotalInvoiceLine ( ) {
        return totalInvoiceLine;
    }

    public void setTotalInvoiceLine ( BigDecimal totalInvoiceLine ) {
        this.totalInvoiceLine = totalInvoiceLine;
    }

    public Invoice getInvoice ( ) {
        return invoice;
    }

    public void setInvoice ( Invoice invoice ) {
        this.invoice = invoice;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass( ) != o.getClass( ) ) return false;
        InvoiceLine that = ( InvoiceLine ) o;
        return units == that.units &&
                Objects.equals( id, that.id ) &&
                Objects.equals( productId, that.productId ) &&
                Objects.equals( productDescription, that.productDescription ) &&
                Objects.equals( productPrice, that.productPrice ) &&
                Objects.equals( totalInvoiceLine, that.totalInvoiceLine );
    }

    @Override
    public int hashCode ( ) {
        return Objects.hash( id, productId, productDescription, units, productPrice, totalInvoiceLine );
    }

    @Override
    public String toString ( ) {
        return "InvoiceLine{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", units=" + units +
                ", productPrice=" + productPrice +
                ", totalInvoiceLine=" + totalInvoiceLine +
                '}';
    }
}
