
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

        public class ConversorDeMoeda {

            private static final String API_URL = "https://v6.exchangerate-api.com/v6/694a41fdc89640d886a97af8ddef4194/latest/BRL";

            public static void main(String[] args) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Digite o valor em Reais (BRL) que deseja converter: ");
                double valorReais = scanner.nextDouble();

                System.out.print("Digite o código da moeda para a qual deseja converter (ex: USD, EUR): ");
                String moedaDestino = scanner.next().toUpperCase();

                try {
                    double taxaCambio = obterTaxaDeCambio(moedaDestino);
                    if (taxaCambio != -1) {
                        double valorConvertido = valorReais * taxaCambio;
                        System.out.printf("R$ %.2f corresponde a %.2f %s.\n", valorReais, valorConvertido, moedaDestino);
                    } else {
                        System.out.println("Código da moeda inválido ou não disponível.");
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao obter a taxa de câmbio: " + e.getMessage());
                }

                scanner.close();
            }

            private static double obterTaxaDeCambio(String moeda) throws Exception {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() != 200) {
                    throw new RuntimeException("Erro: HTTP código: " + connection.getResponseCode());
                }

                StringBuilder resposta = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        resposta.append(linha);
                    }
                }

                String jsonResponse = resposta.toString();
                int index = jsonResponse.indexOf(moeda);
                if (index == -1) {
                    return -1; // Código da moeda não encontrado
                }

                String subString = jsonResponse.substring(index);
                String taxaString = subString.split(":")[1].split(",")[0].trim();
                return Double.parseDouble(taxaString);
            }
        }