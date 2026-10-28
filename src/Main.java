import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.text.NumberFormat;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BigDecimal sl = BigDecimal.ZERO;
        BigDecimal ir, ae, a1, a2, a3;
        String cont;
        NumberFormat moeda = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("pt-BR")
        );
        do {
        System.out.println("\n=========================70=");
        System.out.println("CALCULADORA DE SALÁRIO CLT");
        System.out.println("==========================\n");
            boolean salarioValido = false;

            while (!salarioValido) {
                System.out.print("Insira sua Renda Bruta: R$ ");
                System.out.flush();
                // Lê a linha toda como texto para verificar o que foi digitado.
                String entradaSalario = scanner.nextLine().trim();

                // Aceita números inteiros ou com até duas casas decimais.
                // Exemplos aceitos: 4000, 4000.50 ou 4000,50.
                if (entradaSalario.matches("\\d+([.,]\\d{1,2})?")) {
                    // Converte a vírgula decimal para ponto, que é o formato do BigDecimal.
                    entradaSalario = entradaSalario.replace(',', '.');

                    // Só converte para número depois de validar o texto.
                    sl = new BigDecimal(entradaSalario);
                    salarioValido = true;
                } else {
                    // Letras, sinais, R$, número negativo ou mais de duas casas são rejeitados.
                    System.out.println("Entrada inválida. Digite, por exemplo, 4000 ou 4000,50.");
                }
            }
            // Limites das faixas de contribuição do INSS em 2026.
            BigDecimal limite1 = new BigDecimal("1621.00");
            BigDecimal limite2 = new BigDecimal("2902.84");
            BigDecimal limite3 = new BigDecimal("4354.27");
            BigDecimal teto = new BigDecimal("8475.55");
// O INSS não é calculado sobre a parte que passa do teto.
            BigDecimal baseInss = sl.min(teto);
// Começamos com zero e vamos somar a contribuição de cada faixa.
            BigDecimal inss = BigDecimal.ZERO;
// A primeira faixa vai do início do salário até R$ 1.621,00.
            BigDecimal faixa1 = baseInss.min(limite1);
// As outras faixas começam vazias.
            BigDecimal faixa2 = BigDecimal.ZERO;
            BigDecimal faixa3 = BigDecimal.ZERO;
            BigDecimal faixa4 = BigDecimal.ZERO;
// Se o salário passou de R$ 1.621,00, calculamos o pedaço da segunda faixa.
            if (baseInss.compareTo(limite1) > 0) {
                faixa2 = baseInss.min(limite2).subtract(limite1);
            }
// Se passou de R$ 2.902,84, calculamos o pedaço da terceira faixa.
            if (baseInss.compareTo(limite2) > 0) {
                faixa3 = baseInss.min(limite3).subtract(limite2);
            }
// Se passou de R$ 4.354,27, calculamos o pedaço da quarta faixa.
            if (baseInss.compareTo(limite3) > 0) {
                faixa4 = baseInss.subtract(limite3);
            }
// Aplicamos a alíquota de cada faixa somente ao pedaço correspondente.
            inss = faixa1.multiply(new BigDecimal("0.075"))
                    .add(faixa2.multiply(new BigDecimal("0.09")))
                    .add(faixa3.multiply(new BigDecimal("0.12")))
                    .add(faixa4.multiply(new BigDecimal("0.14")));
// Arredondamos a soma final para centavos.
            inss = inss.setScale(2, RoundingMode.HALF_UP);
            //base de calculo irrf
            BigDecimal descontoSimplificado = new BigDecimal("607.20");
            BigDecimal deducaoIrrf;
// Escolhe a maior dedução.
            if (inss.compareTo(descontoSimplificado) > 0) {
                deducaoIrrf = inss;
            } else {
                deducaoIrrf = descontoSimplificado;
            }
// Calcula a base do IRRF e evita que ela fique negativa.
            BigDecimal baseIrrf = sl.subtract(deducaoIrrf).max(BigDecimal.ZERO);
            System.out.println("\n======================");
            System.out.println("DEMONSTRATIVO SALARIAL");
            System.out.println("======================\n");
            System.out.println("Salário bruto: " + moeda.format(sl));
            System.out.println("\nDESCONTOS");
            System.out.println("\n------------------------------------\n");
// Mostramos o desconto de INSS.
            System.out.println("INSS: " + moeda.format(inss));
            System.out.println("Base de cálculo do IRRF: " + moeda.format(baseIrrf));
            // Calcula o IRRF antes da redução, usando a baseIrrf.
            if (baseIrrf.compareTo(new BigDecimal("2428.80")) <= 0) {
                ir = BigDecimal.ZERO;
            } else if (baseIrrf.compareTo(new BigDecimal("2826.65")) <= 0) {
                ir = baseIrrf.multiply(new BigDecimal("0.075"))
                        .subtract(new BigDecimal("182.16"));
            } else if (baseIrrf.compareTo(new BigDecimal("3751.05")) <= 0) {
                ir = baseIrrf.multiply(new BigDecimal("0.15"))
                        .subtract(new BigDecimal("394.16"));
            } else if (baseIrrf.compareTo(new BigDecimal("4664.68")) <= 0) {
                ir = baseIrrf.multiply(new BigDecimal("0.225"))
                        .subtract(new BigDecimal("675.49"));
            } else {
                ir = baseIrrf.multiply(new BigDecimal("0.275"))
                        .subtract(new BigDecimal("908.73"));
            }
// Impede que o imposto fique negativo e arredonda para centavos.
            ir = ir.max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
// Começa a redução em zero.
            BigDecimal reducaoIrrf = BigDecimal.ZERO;
// Para salário bruto de até R$ 5.000, a redução zera o imposto.
            if (sl.compareTo(new BigDecimal("5000.00")) <= 0) {
                reducaoIrrf = ir;
            }
// Entre R$ 5.000 e R$ 7.350, a redução diminui conforme o salário aumenta.
            else if (sl.compareTo(new BigDecimal("7350.00")) <= 0) {
                reducaoIrrf = new BigDecimal("978.62")
                        .subtract(new BigDecimal("0.133145").multiply(sl));
                // A redução não pode ser negativa nem maior que o imposto calculado.
                reducaoIrrf = reducaoIrrf.max(BigDecimal.ZERO)
                        .min(ir)
                        .setScale(2, RoundingMode.HALF_UP);
            }
// Calcula o valor que realmente será descontado.
            BigDecimal irrfFinal = ir.subtract(reducaoIrrf)
                    .max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);
            // Soma somente os descontos que saem do salário.
            BigDecimal totalDescontos = inss.add(irrfFinal)
                    .setScale(2, RoundingMode.HALF_UP);
// Subtrai os descontos do salário bruto.
            BigDecimal salarioLiquido = sl.subtract(totalDescontos)
                    .setScale(2, RoundingMode.HALF_UP);
// Evita dividir por zero se o salário bruto for zero.
            BigDecimal percentualDescontos;
            if (sl.compareTo(BigDecimal.ZERO) == 0) {
                percentualDescontos = BigDecimal.ZERO;
            } else {
                // Divide o total descontado pelo bruto e converte o resultado em percentual.
                percentualDescontos = totalDescontos
                        .multiply(new BigDecimal("100"))
                        .divide(sl, 8, RoundingMode.HALF_UP)
                        .setScale(2, RoundingMode.HALF_UP);
            }
// Mostra o cálculo no demonstrativo.
            System.out.println("IRRF antes da redução: " + moeda.format(ir));
            System.out.println("Redução do IRRF: " + moeda.format(reducaoIrrf));
            System.out.println("IRRF efetivamente descontado: " + moeda.format(irrfFinal));
            System.out.println("\n------------------------------------\n");
            System.out.println("Total de descontos: " + moeda.format(totalDescontos));
            System.out.println("Salário líquido: " + moeda.format(salarioLiquido));
            System.out.println("Percentual de descontos: "
                    + percentualDescontos.toPlainString() + "%");
            System.out.println("\n------------------------------------\n");
            System.out.println("Quer Calcular Denovo? S/N");
            cont = scanner.nextLine();
        }while (cont.equalsIgnoreCase("S"));
    }
}