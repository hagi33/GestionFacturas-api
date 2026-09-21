package com.fabio.GestionFacturas.infrastructure.adapter.in.web.ingreso.dto;

import java.time.LocalDate;

/** Request DTO for registering the collection of an invoice. */
public record RegistrarCobroRequest(LocalDate fechaCobro) {}
