package com.mycompany.multmatrices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientCommunicationUtils {
    private static final int SERVER_PORT = 12345;
    public static String serverHost = "";


    /**
     * Converte JSON para uma matriz double[][]
     * 
     * @param json
     * @return
     * @throws JsonMappingException
     * @throws JsonProcessingException 
     */
    public static double[][] jsonToMatrix(String json) throws JsonMappingException, JsonProcessingException {
        // Cria uma nova instância do ObjectMapper para a operação de deserialização
        ObjectMapper mapper = new ObjectMapper();
        // Usa o ObjectMapper para converter a String JSON de volta para uma matriz double[][] e retorna essa matriz
        return mapper.readValue(json, double[][].class);
    }
    
    /**
     * Converte uma matriz double[][] para JSON
     * @param matrix
     * @return
     * @throws JsonProcessingException 
     */
    public static String matrixToJson(double[][] matrix) throws JsonProcessingException {
        // Cria uma instância do ObjectMapper, que é a classe principal para manipulação JSON no Jackson
        ObjectMapper mapper = new ObjectMapper();

        // Usa o ObjectMapper para converter a matriz para uma String JSON e retorna essa String
        return mapper.writeValueAsString(matrix);
    }
    
    /**
     * 
     * @param operation
     * @param matrixA
     * @param matrixB
     * @return
     * @throws JsonProcessingException 
     */
    public static String matricesToJson(String operation, double[][] matrixA, double[][] matrixB) throws JsonProcessingException {
        // Cria uma instância do ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        // Cria um mapa para armazenar os dados
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("operation", operation);
        jsonMap.put("matrixA", matrixA);
        jsonMap.put("matrixB", matrixB);

        // Converte o mapa para uma String JSON
        return mapper.writeValueAsString(jsonMap);
    }

    /**
     * 
     * @param matA
     * @param matB
     * @param operation
     * @return 
     */
    public static double[][] sendRequestToServer(double[][] matA, double matB[][], String operation) {
        double[][] matProcessed = null;

        try {
            // Cria um socket para conectar ao servidor no host e na porta especificados
            Socket socket = new Socket(serverHost, SERVER_PORT);
            System.out.println("Conectado ao servidor!");

            // Cria um fluxo de saída para enviar dados ao servidor
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Cria um fluxo de entrada para receber a resposta do servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Converte a matriz para uma string JSON usando um método auxiliar
            String json = matricesToJson(operation, matA, matB);

            System.out.println("Enviando payload ao servidor como JSON...");
            out.println(json);

            // Lê a resposta do servidor (presumindo que seja uma string JSON em uma única linha)
            String responseJson = in.readLine();
            System.out.println("Resposta recebida do servidor");

            // Converte o JSON de volta para uma matriz (caso necessário)
            matProcessed = jsonToMatrix(responseJson);
            //MatrizUtils.printaMatriz(matProcessed, "resultado"); // Método auxiliar para exibir a matriz

            // Fecha os fluxos e o socket
            out.close();
            in.close();
            socket.close();

        } catch (JsonProcessingException e) {
            // Captura e exibe erros relacionados ao processamento de JSON
            e.printStackTrace();
        } catch (IOException e) {
            // Captura e exibe erros relacionados a operações de entrada/saída
            e.printStackTrace();
        }

        return matProcessed;
    }

    public static void closeServer() throws IOException {
        Socket socket = new Socket(serverHost, SERVER_PORT);
        System.out.println("Conectado ao servidor!");

        // Cria um fluxo de saída para enviar dados ao servidor
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        ObjectMapper mapper = new ObjectMapper();

 
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("operation", "exit");
        String json = mapper.writeValueAsString(jsonMap);
        
        System.out.println("Enviando payload ao servidor como JSON...");
        out.println(json);
    }

}
