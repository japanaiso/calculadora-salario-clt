import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void salarioZeroNaoTemDescontos() {
        conferirSaida("0\nN\n",
                "INSS: R$ 0,00",
                "Total de descontos: R$ 0,00",
                "Salário líquido: R$ 0,00");
    }

    @Test
    void salarioAbaixoDaPrimeiraFaixaCalculaInss() {
        conferirSaida("1000\nN\n",
                "INSS: R$ 75,00");
    }

    @Test
    void salarioNaSegundaFaixaCalculaInssProgressivo() {
        conferirSaida("2000\nN\n",
                "INSS: R$ 155,69");
    }

    @Test
    void salarioAcimaDoTetoLimitaInss() {
        conferirSaida("10000\nN\n",
                "INSS: R$ 988,09");
    }

    @Test
    void baseDoIrrfNaFaixaIsentaNaoGeraImposto() {
        conferirSaida("3000\nN\n",
                "Base de cálculo do IRRF: R$ 2.392,80",
                "IRRF efetivamente descontado: R$ 0,00");
    }

    @Test
    void salarioDeAteCincoMilTemIrrfZeradoPelaReducao() {
        conferirSaida("5000\nN\n",
                "IRRF antes da redução: R$ 312,89",
                "Redução do IRRF: R$ 312,89",
                "IRRF efetivamente descontado: R$ 0,00");
    }

    @Test
    void salarioEntreCincoMilESeteMilTrezentosECinquentaTemReducaoDecrescente() {
        conferirSaida("6000\nN\n",
                "Redução do IRRF: R$ 179,75",
                "IRRF efetivamente descontado: R$ 385,10");
    }

    @Test
    void salarioAcimaDeSeteMilTrezentosECinquentaNaoTemReducaoAdicional() {
        conferirSaida("8000\nN\n",
                "Redução do IRRF: R$ 0,00",
                "IRRF efetivamente descontado: R$ 1.037,85");
    }

    @Test
    void entradaNegativaERecusadaEProgramaPedeOutroValor() {
        conferirSaida("-100\n4000\nN\n",
                "Entrada inválida.",
                "Salário bruto: R$ 4.000,00");
    }

    @Test
    void salarioComCentavosMantemPrecisaoMonetaria() {
        conferirSaida("4000,50\nN\n",
                "Salário bruto: R$ 4.000,50",
                "INSS: R$ 368,66");
    }

    @Test
    void calculoDoInssParaQuatroMilRetornaValorEsperado() {
        BigDecimal salario = new BigDecimal("4000.00");
        BigDecimal inssEsperado = new BigDecimal("368.60");

        BigDecimal inssCalculado = Main.calcularInss(salario);

        assertEquals(0, inssEsperado.compareTo(inssCalculado));
    }

    private void conferirSaida(String entrada, String... trechosEsperados) {
        String saida = executarPrograma(entrada);

        for (String trecho : trechosEsperados) {
            assertTrue(saida.contains(trecho),
                    "Não encontrei \"" + trecho + "\" na saída:\n" + saida);
        }
    }

    private String executarPrograma(String entrada) {
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream textoCapturado = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(
                    entrada.getBytes(StandardCharsets.UTF_8)));

            System.setOut(new PrintStream(
                    textoCapturado, true, StandardCharsets.UTF_8));

            Main.main(new String[0]);

        } finally {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }

        return textoCapturado.toString(StandardCharsets.UTF_8)
                .replace('\u00A0', ' ')
                .replace('\u202F', ' ');
    }
    @Test
    void respostaDiferenteDeSeNMostraErro() {
        conferirSaida("1000\nX\nN\n",
                "Resposta inválida. Digite apenas S ou N.");
    }
}