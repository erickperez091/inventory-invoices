package com.example.invoices.service;

import com.example.invoices.entity.Invoice;
import com.example.invoices.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class InvoiceService {

    private final InvoiceRepository repository;

    @Transactional( propagation = Propagation.REQUIRED )
    public void save( Invoice invoice ) {
        logger.info( "START | Save Invoice {}", invoice.getId() );
        repository.save( invoice );
        logger.info( "FINISH | Save Invoice {}", invoice.getId() );
    }

    @Transactional( propagation = Propagation.REQUIRED, readOnly = true )
    public Optional< Invoice > findById( String id ) {
        return repository.findById( id );
    }

    public void delete( String id ) {
        logger.info( "START | Delete Invoice {}", id );
        repository.deleteById( id );
        logger.info( "FINISH | Delete Invoice {}", id );
    }
}
