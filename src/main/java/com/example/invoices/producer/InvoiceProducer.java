package com.example.invoices.producer;

import com.example.common.entitty.MessageEvent;
import com.example.common.service.KafkaSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceProducer {

    @Autowired
    private KafkaSenderService senderService;

    public InvoiceProducer ( ) {
    }

    public void sendMessage ( MessageEvent messageEvent ) {
        senderService.sendMessage( messageEvent );
    }

}
