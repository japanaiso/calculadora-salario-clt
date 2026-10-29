# Calculadora de Salário CLT

Programa de terminal em Java que recebe o salário bruto mensal e exibe um demonstrativo com INSS, base do IRRF, IRRF antes e depois da redução, total de descontos, salário líquido e percentual descontado.

## Requisitos

- JDK instalado (Java 8 ou superior).
- Um terminal, como o Terminal do IntelliJ IDEA.

## Como executar

Salve o código em um arquivo chamado `Main.java`. Abra o terminal na pasta que contém esse arquivo e execute:

```text
javac Main.java
java Main
```

O programa pede o salário bruto. Depois de exibir o demonstrativo, digite `S` para fazer outro cálculo. Qualquer outra resposta encerra o programa.

## Formatos de entrada

São aceitos salários inteiros ou com até duas casas decimais, usando ponto ou vírgula decimal:

```text
4000
4000.50
4000,50
```

Não digite `R$`, sinal de mais ou menos, letras, separador de milhar ou mais de duas casas decimais. Entradas fora do formato são recusadas e o programa solicita o valor novamente.

## Regras de cálculo

- **INSS:** cálculo progressivo pelas faixas de 2026 e limitado ao teto previdenciário de R$ 8.475,55.
- **Base do IRRF:** salário bruto menos a maior dedução entre o INSS calculado e o desconto simplificado mensal de R$ 607,20. A base mínima é zero.
- **IRRF:** cálculo pela tabela mensal de 2026, seguido da redução adicional: o imposto é zerado para salários de até R$ 5.000,00; entre R$ 5.000,01 e R$ 7.350,00 aplica-se a redução decrescente; acima disso não há redução adicional.
- **Salário líquido:** salário bruto menos INSS e IRRF efetivamente descontado.
- **Valores monetários:** calculados com `BigDecimal` e arredondados para centavos com `RoundingMode.HALF_UP`. A moeda é exibida no formato brasileiro.

## Exemplos para conferência manual

Execute o programa uma vez para cada valor e compare os campos indicados:

| Salário digitado | INSS esperado | Base do IRRF | IRRF descontado | Líquido esperado |
|---:|---:|---:|---:|---:|
| `0` | R$ 0,00 | R$ 0,00 | R$ 0,00 | R$ 0,00 |
| `1000` | R$ 75,00 | R$ 392,80 | R$ 0,00 | R$ 925,00 |
| `4000` | R$ 368,60 | R$ 3.392,80 | R$ 0,00 | R$ 3.631,40 |
| `6000` | R$ 641,51 | R$ 5.358,49 | R$ 385,10 | R$ 4.973,39 |
| `8000` | R$ 921,51 | R$ 7.078,49 | R$ 1.037,85 | R$ 6.040,64 |

Os casos acima são conferências manuais. O código colado nesta versão não inclui uma suíte de testes automatizados.

## Observação sobre o exemplo de R$ 4.000,00 do desafio

O PDF mostra INSS de R$ 373,41 para salário bruto de R$ 4.000,00. Aplicando progressivamente as faixas oficiais de 2026 indicadas pelo próprio desafio, o resultado é R$ 368,60. Por isso, o líquido calculado por este programa difere do valor ilustrativo do PDF.

## Testes automatizados

O projeto usa JUnit 5 para verificar os cálculos e as validações da calculadora.

No IntelliJ IDEA, abra `MainTest.java`, clique com o botão direito dentro do arquivo e selecione **Run 'MainTest'**.

Os testes cobrem cenários como salário zero, faixas do INSS, teto previdenciário, faixas e redução do IRRF, entrada inválida e valores com centavos.

## Fontes oficiais

- [Tabela de contribuição mensal do INSS](https://www.gov.br/inss/pt-br/direitos-e-deveres/inscricao-e-contribuicao/tabela-de-contribuicao-mensal)
- [Tributação de 2026 — Receita Federal](https://www.gov.br/receitafederal/pt-br/assuntos/meu-imposto-de-renda/tabelas/2026)
- [Exemplos de aplicação da Lei 15.270/2025 — Receita Federal](https://www.gov.br/receitafederal/pt-br/assuntos/meu-imposto-de-renda/tabelas/exemplos-de-aplicacao-da-lei-15-270-2025)
