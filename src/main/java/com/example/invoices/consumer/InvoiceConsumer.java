package com.example.invoices.consumer;

import com.example.common.entitty.EnumUtil.EventType;
import com.example.common.entitty.MessageEvent;
import com.example.invoices.consumer.processor.InvoiceLineProcessor;
import com.example.invoices.consumer.processor.InvoiceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class InvoiceConsumer {

    private static final Logger logger = LoggerFactory.getLogger( InvoiceConsumer.class );
    private final InvoiceProcessor invoiceProcessor;
    private final InvoiceLineProcessor invoiceLineProcessor;

    @Autowired
    InvoiceConsumer( InvoiceProcessor invoiceProcessor, InvoiceLineProcessor invoiceLineProcessor ) {
        this.invoiceProcessor = invoiceProcessor;
        this.invoiceLineProcessor = invoiceLineProcessor;
    }

    @KafkaListener( topics = { "${topic-name}" } )
    public void handleInvoiceEvent( @Payload final MessageEvent messageEvent ) throws URISyntaxException {
        logger.info( "Message received: {}", messageEvent.getEventName() );
        EventType eventType = messageEvent.getEventName();
        switch ( eventType ) {
            case CREATE_INVOICE: {
                invoiceProcessor.store( messageEvent.getPayload() );
                break;
            }
            case UPDATE_INVOICE: {
                invoiceProcessor.refresh( messageEvent.getPayload() );
                break;
            }
            case DELETE_INVOICE: {
                invoiceProcessor.delete( messageEvent.getPayload() );
                break;
            }
            case ADD_MODIFY_INVOICE_LINE: {
                invoiceLineProcessor.storeOrRefresh( messageEvent.getPayload() );
                break;
            }
        }
    }

}
