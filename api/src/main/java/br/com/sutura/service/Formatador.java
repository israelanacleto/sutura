package br.com.sutura.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * O banco guarda documento sem máscara — é assim que se compara CNS e CPF sem depender
 * de formatação. A máscara é assunto de apresentação e mora aqui.
 */
public final class Formatador {

    public static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String AUSENTE = "—";

    private Formatador() {
    }

    /** 708004288150003 -> 708 0042 8815 0003 */
    public static String cns(String valor) {
        if (valor == null || valor.isBlank()) {
            return AUSENTE;
        }
        String limpo = somenteDigitos(valor);
        if (limpo.length() != 15) {
            return valor;
        }
        return "%s %s %s %s".formatted(
                limpo.substring(0, 3), limpo.substring(3, 7),
                limpo.substring(7, 11), limpo.substring(11));
    }

    /** 31748590211 -> 317.485.902-11 */
    public static String cpf(String valor) {
        if (valor == null || valor.isBlank()) {
            return AUSENTE;
        }
        String limpo = somenteDigitos(valor);
        if (limpo.length() != 11) {
            return valor;
        }
        return "%s.%s.%s-%s".formatted(
                limpo.substring(0, 3), limpo.substring(3, 6),
                limpo.substring(6, 9), limpo.substring(9));
    }

    public static String data(LocalDate valor) {
        return valor == null ? AUSENTE : valor.format(DATA);
    }

    public static String texto(String valor) {
        return valor == null || valor.isBlank() ? AUSENTE : valor;
    }

    public static String ausente() {
        return AUSENTE;
    }

    private static String somenteDigitos(String valor) {
        return valor.replaceAll("[^0-9]", "");
    }
}
