package com.example.invoices.service;

import com.example.invoices.entity.Invoice;
import com.example.invoices.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger( InvoiceService.class );

    @Autowired
    private InvoiceRepository repository;

    @Transactional( propagation = Propagation.REQUIRED )
    public void save ( Invoice invoice ) {
        logger.info( "START | Save Invoice {}", invoice.getId( ) );
        repository.save( invoice );
        logger.info( "FINISH | Save Invoice {}", invoice.getId( ) );
    }

    @Transactional( propagation = Propagation.REQUIRED, readOnly = true )
    public Optional<Invoice> findById ( String id ) {
        return repository.findById( id );
    }

    public void delete ( String id ) {
        logger.info( "START | Delete Invoice {}", id );
        repository.deleteById( id );
        logger.info( "FINISH | Delete Invoice {}", id );
    }
}
