package com.example.invoices.messaging;

import com.example.common.entity.MessageEvent;
import com.example.common.service.messaging.MessagingProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoicePublisher {

    private final MessagingProducer messagingProducer;

    public void sendEvent(MessageEvent messageEvent) {
        messagingProducer.send(messageEvent);
    }
}
