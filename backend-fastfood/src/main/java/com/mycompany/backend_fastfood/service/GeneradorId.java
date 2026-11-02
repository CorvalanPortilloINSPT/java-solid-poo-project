package com.mycompany.backend_fastfood.service;

import java.util.Collection;

/**
 * Genera IDs secuenciales con prefijo, con la misma regla
 * que el sistema anterior: el mayor número existente + 1.
 *
 * Ejemplos:
 *   siguiente(["ING01", "ING04"], "ING", 2)  → "ING05"
 *   siguiente([], "PR", 3)                   → "PR001"
 *   siguiente(["E0001", "E0003"], "E", 4)    → "E0004"
 *
 * Los IDs que no tienen el formato esperado se ignoran
 * (por ejemplo "PRTEST01" no cuenta para el prefijo "PR").
 */
public final class GeneradorId {

    private GeneradorId() {
        // Clase utilitaria: no se instancia.
    }

    public static String siguiente(
            Collection<String> idsExistentes,
            String prefijo,
            int digitos) {

        int mayor = 0;

        for (String id : idsExistentes) {

            if (id == null || !id.startsWith(prefijo)) {
                continue;
            }

            try {
                int numero = Integer.parseInt(id.substring(prefijo.length()));

                if (numero > mayor) {
                    mayor = numero;
                }

            } catch (NumberFormatException e) {
                // Ignorar IDs que no tengan el formato esperado.
            }
        }

        return prefijo + String.format("%0" + digitos + "d", mayor + 1);
    }
}