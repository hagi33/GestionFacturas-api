package com.fabio.GestionFacturas.domain.dashboard;

import com.fabio.GestionFacturas.domain.gasto.Gasto;
import com.fabio.GestionFacturas.domain.ingreso.Ingreso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure-domain calculator: groups gastos and ingresos (already filtered to the period by the caller)
 * by clienteId and delegates each group's figures to {@link CalculadoraResumenPeriodo}, so the
 * facturado/cobrado/beneficio rules are defined in one place only.
 * <p>
 * Result: one entry per client that appears in the input, ordered by clienteId, plus a trailing
 * "sin cliente" entry (clienteId null) only if some gasto has no client.
 */
public class CalculadoraRentabilidadClientes {

    private final CalculadoraResumenPeriodo calculadoraResumen;

    public CalculadoraRentabilidadClientes(CalculadoraResumenPeriodo calculadoraResumen) {
        this.calculadoraResumen = calculadoraResumen;
    }

    public List<RentabilidadCliente> calcular(List<Gasto> gastos, List<Ingreso> ingresos) {
        Map<Long, List<Gasto>> gastosPorCliente = new HashMap<>();
        Map<Long, List<Ingreso>> ingresosPorCliente = new HashMap<>();
        List<Gasto> gastosSinCliente = new ArrayList<>();

        for (Gasto gasto : gastos) {
            if (gasto.getClienteId() == null) {
                gastosSinCliente.add(gasto);
            } else {
                gastosPorCliente.computeIfAbsent(gasto.getClienteId(), id -> new ArrayList<>()).add(gasto);
            }
        }
        for (Ingreso ingreso : ingresos) {
            ingresosPorCliente.computeIfAbsent(ingreso.getClienteId(), id -> new ArrayList<>()).add(ingreso);
        }

        List<Long> clienteIds = new ArrayList<>(gastosPorCliente.keySet());
        for (Long clienteId : ingresosPorCliente.keySet()) {
            if (!gastosPorCliente.containsKey(clienteId)) {
                clienteIds.add(clienteId);
            }
        }
        clienteIds.sort(Comparator.naturalOrder());

        List<RentabilidadCliente> resultado = new ArrayList<>();
        for (Long clienteId : clienteIds) {
            ResumenPeriodo resumen = calculadoraResumen.calcular(
                    gastosPorCliente.getOrDefault(clienteId, List.of()),
                    ingresosPorCliente.getOrDefault(clienteId, List.of()));
            resultado.add(new RentabilidadCliente(clienteId, resumen));
        }
        if (!gastosSinCliente.isEmpty()) {
            resultado.add(new RentabilidadCliente(null, calculadoraResumen.calcular(gastosSinCliente, List.of())));
        }
        return resultado;
    }
}
