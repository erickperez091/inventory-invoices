package com.example.invoices.entity;

import com.example.common.entity.EnumUtil.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Table( name = "invoice" )
@Entity
@DynamicUpdate
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//       property = "id" )
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
