package com.fabio.GestionFacturas.infrastructure.config;

import com.fabio.GestionFacturas.domain.gasto.FacturaTextParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainBeansConfig {

    @Bean
    public FacturaTextParser facturaTextParser() {
        return new FacturaTextParser();
    }
}
