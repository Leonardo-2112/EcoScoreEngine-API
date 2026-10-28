package br.com.soulprime.bo;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import br.com.soulprime.exception.DadosInvalidosException;

//Validacoes que o Bean Validation nao faz sozinho: digito verificador do CPF/CNPJ, idade minima e UF
public class ValidadorDados {

    public static final int IDADE_MINIMA = 16;

    private static final List<String> UFS = List.of("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
            "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");

    //Remove tudo que nao for numero (mascara de CPF, CEP, telefone)
    public static String somenteDigitos(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.replaceAll("\\D", "");
    }

    //Remove espacos extras; texto vazio vira null
    public static String texto(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim().replaceAll("\\s+", " ");
    }

    //CPF com 11 digitos e digitos verificadores validos. Retorna o CPF sem mascara
    public static String validarCpf(String cpf) {
        String digitos = somenteDigitos(cpf);
        if (digitos == null || digitos.length() != 11 || digitos.chars().distinct().count() == 1) {
            throw new DadosInvalidosException("CPF invalido.");
        }
        for (int posicao = 9; posicao <= 10; posicao++) {
            int soma = 0;
            for (int i = 0; i < posicao; i++) {
                soma += (digitos.charAt(i) - '0') * (posicao + 1 - i);
            }
            int digito = (soma * 10) % 11;
            if (digito == 10) {
                digito = 0;
            }
            if (digito != digitos.charAt(posicao) - '0') {
                throw new DadosInvalidosException("CPF invalido: digito verificador nao confere.");
            }
        }
        return digitos;
    }

    //CNPJ com 14 digitos e digitos verificadores validos. Retorna no formato 00.000.000/0000-00
    public static String validarCnpj(String cnpj) {
        String d = somenteDigitos(cnpj);
        if (d == null || d.length() != 14 || d.chars().distinct().count() == 1) {
            throw new DadosInvalidosException("CNPJ invalido.");
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        if (digitoCnpj(d, pesos1) != d.charAt(12) - '0' || digitoCnpj(d, pesos2) != d.charAt(13) - '0') {
            throw new DadosInvalidosException("CNPJ invalido: digito verificador nao confere.");
        }
        return d.substring(0, 2) + "." + d.substring(2, 5) + "." + d.substring(5, 8) + "/" + d.substring(8, 12) + "-" + d.substring(12);
    }

    private static int digitoCnpj(String d, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (d.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    public static void validarIdade(LocalDate nascimento) {
        if (Period.between(nascimento, LocalDate.now()).getYears() < IDADE_MINIMA) {
            throw new DadosInvalidosException("Idade minima para cadastro: " + IDADE_MINIMA + " anos.");
        }
    }

    public static String validarUf(String uf) {
        String sigla = uf.trim().toUpperCase();
        if (!UFS.contains(sigla)) {
            throw new DadosInvalidosException("UF invalida.");
        }
        return sigla;
    }

    //Converte o texto recebido no enum, com mensagem clara se o valor nao existir
    public static <E extends Enum<E>> E converterEnum(Class<E> tipo, String valor) {
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DadosInvalidosException("Valor invalido: '" + valor + "'. Valores aceitos: " + List.of(tipo.getEnumConstants()));
        }
    }
}
