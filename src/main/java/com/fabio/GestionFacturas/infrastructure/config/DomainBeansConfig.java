package com.fabio.GestionFacturas.infrastructure.config;

import com.fabio.GestionFacturas.domain.dashboard.CalculadoraResumenPeriodo;
import com.fabio.GestionFacturas.domain.gasto.FacturaTextParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers plain domain classes as Spring beans so they can be constructor-injected
 * (e.g. into {@code DigitalizarFacturaService}) without the domain itself depending on Spring —
 * {@link FacturaTextParser} has no {@code @Component}, staying a framework-free POJO.
 */
@Configuration
public class DomainBeansConfig {

    @Bean
    public FacturaTextParser facturaTextParser() {
        return new FacturaTextParser();
    }

    @Bean
    public CalculadoraResumenPeriodo calculadoraResumenPeriodo() {
        return new CalculadoraResumenPeriodo();
    }
}
