package com.example.invoices.entity;

import com.example.common.entitty.EnumUtil.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Table( name = "invoice" )
@Entity
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//       property = "id" )
public class Invoice implements Serializable {

    @Id
    @Column( name = "id", nullable = false, unique = true )
    private String id;

    @DateTimeFormat( pattern = "yyyy-MM-dd HH:mm:ss", iso = DATE_TIME )
    @JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss" )
    @Column( name = "invoice_date", columnDefinition = "TIMESTAMP" )
    @JsonDeserialize( using = LocalDateTimeDeserializer.class )
    @JsonSerialize( using = LocalDateTimeSerializer.class )
    private LocalDateTime invoiceDate;

    @Column( name = "invoice_tax", nullable = true )
    private BigDecimal invoiceTax; //

    @Column( name = "invoice_discount", nullable = true )
    private BigDecimal invoiceDiscount;

    @Column( name = "invoice_total", nullable = false )
    private BigDecimal invoiceTotal;

    @Column( name = "invoice_subtotal", nullable = false )
    private BigDecimal invoiceSubTotal;

    @Column( name = "status" )
    @Enumerated( EnumType.STRING )
    private InvoiceStatus invoiceStatus;

    @OneToMany( mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    //@JsonManagedReference
    @JsonIgnoreProperties( { "invoice" } )
    private Set< InvoiceLine > invoiceLines;


    public Invoice( String id, LocalDateTime invoiceDate, BigDecimal invoiceTax, BigDecimal invoiceDiscount, BigDecimal invoiceTotal, BigDecimal invoiceSubTotal, InvoiceStatus invoiceStatus, Set< InvoiceLine > invoiceLines ) {
        this.id = id;
        this.invoiceDate = invoiceDate;
        this.invoiceTax = invoiceTax;
        this.invoiceDiscount = invoiceDiscount;
        this.invoiceTotal = invoiceTotal;
        this.invoiceSubTotal = invoiceSubTotal;
        this.invoiceStatus = invoiceStatus;
        this.invoiceLines = invoiceLines;
    }

    public Invoice() {
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate( LocalDateTime invoiceDate ) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getInvoiceTax() {
        return invoiceTax;
    }

    public void setInvoiceTax( BigDecimal invoiceTax ) {
        this.invoiceTax = invoiceTax;
    }

    public BigDecimal getInvoiceDiscount() {
        return invoiceDiscount;
    }

    public void setInvoiceDiscount( BigDecimal invoiceDiscount ) {
        this.invoiceDiscount = invoiceDiscount;
    }

    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal( BigDecimal invoiceTotal ) {
        this.invoiceTotal = invoiceTotal;
    }

    public BigDecimal getInvoiceSubTotal() {
        return invoiceSubTotal;
    }

    public void setInvoiceSubTotal( BigDecimal invoiceSubTotal ) {
        this.invoiceSubTotal = invoiceSubTotal;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus( InvoiceStatus invoiceStatus ) {
        this.invoiceStatus = invoiceStatus;
    }

    public Set< InvoiceLine > getInvoiceLines() {
        return invoiceLines;
    }

    public void setInvoiceLines( Set< InvoiceLine > invoiceLines ) {
        this.invoiceLines = invoiceLines;
    }

    private void calculateInvoiceSubtotal() {
        this.invoiceSubTotal = BigDecimal.ZERO;
        if ( CollectionUtils.isNotEmpty( invoiceLines ) ) {
            this.invoiceSubTotal = invoiceLines.stream().map( InvoiceLine::getTotalInvoiceLine ).reduce( BigDecimal.ZERO, BigDecimal::add );
            this.invoiceSubTotal = this.invoiceSubTotal.setScale( 2, RoundingMode.HALF_EVEN );
        }
    }

    private void calculateDiscountTotal( BigDecimal discountPercentage ) {
        this.invoiceDiscount = this.invoiceSubTotal.multiply( discountPercentage.divide( new BigDecimal( 100 ) ) );
        this.invoiceDiscount = this.invoiceDiscount.setScale( 2, RoundingMode.HALF_EVEN );
    }

    private void calculateTaxTotal( BigDecimal taxPercentage ) {
        BigDecimal subTotalAux = invoiceSubTotal.subtract( invoiceDiscount );
        this.invoiceTax = subTotalAux.multiply( taxPercentage.divide( new BigDecimal( 100 ) ) );
        this.invoiceTax = this.invoiceTax.setScale( 2, RoundingMode.HALF_EVEN );
    }

    public void calculateTotal( BigDecimal discountPercentage, BigDecimal taxesPercentage ) {
        this.calculateInvoiceSubtotal();
        this.calculateDiscountTotal( discountPercentage );
        this.calculateTaxTotal( taxesPercentage );
        this.invoiceTotal = this.invoiceSubTotal.subtract( this.invoiceDiscount ).add( this.invoiceTax );
        this.invoiceTotal = this.invoiceTotal.setScale( 2, RoundingMode.HALF_EVEN );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals( id, invoice.id ) &&
                Objects.equals( invoiceDate, invoice.invoiceDate ) &&
                invoiceStatus == invoice.invoiceStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, invoiceDate, invoiceTax, invoiceDiscount, invoiceTotal, invoiceSubTotal, invoiceStatus );
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id='" + id + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", invoiceTax=" + invoiceTax +
                ", invoiceDiscount=" + invoiceDiscount +
                ", invoiceSubTotal=" + invoiceSubTotal +
                ", invoiceTotal=" + invoiceTotal +
                ", invoiceStatus=" + invoiceStatus +
                ", invoiceLines=" + invoiceLines +
                '}';
    }
}
