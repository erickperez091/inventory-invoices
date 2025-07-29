package com.example.invoices.consumer;

import com.example.common.entity.EnumUtil;
import com.example.common.entity.MessageEvent;
import com.example.invoices.consumer.processor.InvoiceLineProcessor;
import com.example.invoices.consumer.processor.InvoiceProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
@Log4j2
public class InvoiceConsumer {

    private final InvoiceProcessor invoiceProcessor;
    private final InvoiceLineProcessor invoiceLineProcessor;

    @KafkaListener( topics = { "${topic-name}" } )
    public void handleInvoiceEvent( @Payload final MessageEvent messageEvent ) throws URISyntaxException {
        logger.info( "Message received: {}", messageEvent.getEventName() );
        EnumUtil.EventType eventType = messageEvent.getEventName();
        switch ( eventType ) {
            case CREATE_INVOICE -> {
                invoiceProcessor.store( messageEvent.getPayload() );
            }
            case UPDATE_INVOICE -> {
                invoiceProcessor.refresh( messageEvent.getPayload() );
            }
            case DELETE_INVOICE -> {
                invoiceProcessor.delete( messageEvent.getPayload() );
            }
            case ADD_MODIFY_INVOICE_LINE -> {
                invoiceLineProcessor.storeOrRefresh( messageEvent.getPayload() );
            }
        }
    }

}
