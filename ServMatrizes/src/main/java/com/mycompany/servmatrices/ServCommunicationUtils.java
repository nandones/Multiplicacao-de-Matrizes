package com.mycompany.servmatrices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ServCommunicationUtils {

    private static final int SERVER_PORT = 12345;
    private static ServerSocket serverSocket;

    /**
     * Converte JSON para uma matriz double[][]
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
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException 
     */
    public static void startServer() throws IOException, InterruptedException, ExecutionException {
        System.out.println("Servidor rodando em: "+InetAddress.getLocalHost());
        serverSocket = new ServerSocket(SERVER_PORT);
        boolean serv = true;
        while (serv) {
            System.out.println("Servidor aguardando conexão na porta " + SERVER_PORT);
            serv = respondRequestFromClient();
        }
    }

    /**
     * 
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException 
     */
    public static boolean respondRequestFromClient() throws IOException, InterruptedException, ExecutionException {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Cliente conectado!");
        double[][] matrix = null;
        // Cria um BufferedReader para ler a mensagem JSON do cliente
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // Cria um fluxo de saída para enviar dados ao servidor
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        ObjectMapper mapper = new ObjectMapper();

        String json = in.readLine(); // Lê a linha contendo o JSON

        // Converte o JSON para um Map
        Map<String, Object> map = mapper.readValue(json, Map.class);

        // Extrai os valores do Map
        String operation = (String) map.get("operation");
        double[][] matrixA = mapper.convertValue(map.get("matrixA"), double[][].class);
        double[][] matrixB = mapper.convertValue(map.get("matrixB"), double[][].class);


        System.out.println("->"+operation+":");
        if ("singleThread".equalsIgnoreCase(operation)) {
            matrix = ServMatrixUtils.multiplyMatrices(matrixA, matrixB);
        }
        if ("multiThread".equalsIgnoreCase(operation)) {
            matrix = ServMatrixApp.localMultipleThreadMultiplication(matrixA, matrixB);
        }
        if ("exit".equalsIgnoreCase(operation)) {
            return false;
        }

        // Converte a matriz para uma string JSON usando um método auxiliar
        if(matrix == null){
            System.out.println("ERRO, RESPOSTA É NULL");
        }
        json = matrixToJson(matrix);
        // Envia o JSON para o servidor
        out.println(json);
        return true;
    }
}
