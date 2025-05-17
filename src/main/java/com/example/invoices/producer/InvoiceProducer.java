package com.example.invoices.producer;

import com.example.common.entity.MessageEvent;
import com.example.common.service.KafkaSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceProducer {

    private final KafkaSenderService senderService;

    public void sendMessage( MessageEvent messageEvent ) {
        senderService.sendMessage( messageEvent );
    }

}
