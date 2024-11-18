package com.mycompany.servmatrizes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServComunicacaoUtils {

    private static final int SERVER_PORT = 12345;
    private static ServerSocket serverSocket;

    // Converte JSON para uma matriz double[][]
    public static double[][] jsonToMatrix(String json) throws JsonMappingException, JsonProcessingException {
        // Cria uma nova instância do ObjectMapper para a operação de deserialização
        ObjectMapper mapper = new ObjectMapper();

        // Usa o ObjectMapper para converter a String JSON de volta para uma matriz double[][] e retorna essa matriz
        return mapper.readValue(json, double[][].class);
    }

    // Converte uma matriz double[][] para JSON
    public static String matrizParaJson(double[][] matrix) throws JsonProcessingException {
        // Cria uma instância do ObjectMapper, que é a classe principal para manipulação JSON no Jackson
        ObjectMapper mapper = new ObjectMapper();

        // Usa o ObjectMapper para converter a matriz para uma String JSON e retorna essa String
        return mapper.writeValueAsString(matrix);
    }

    public static String matrizesParaJson(String operation, double[][] matrixA, double[][] matrixB) throws JsonProcessingException {
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

    public static void iniciaServer() throws IOException, InterruptedException, ExecutionException {
        serverSocket = new ServerSocket(SERVER_PORT);
        boolean serv = true;
        while (serv) {
            System.out.println("Servidor aguardando conexão na porta " + SERVER_PORT);
            serv = respondeReqDoCliente();
        }
    }

    public static boolean respondeReqDoCliente() throws IOException, InterruptedException, ExecutionException {
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

        //exibe a operação:
        System.out.println(operation);
        // Exibe a matriz recebida
        //MatrizUtils.printaMatriz(matrixA, "MatA");
        // Exibe a matriz recebida
        //MatrizUtils.printaMatriz(matrixB, "MatB");
        if ("singleThread".equalsIgnoreCase(operation)) {
            matrix = ServMatrizUtils.multiplicaMatrizes(matrixA, matrixB);
        }
        if ("multiThread".equalsIgnoreCase(operation)) {
            matrix = ServMatrizesApp.localMultipleThreadMultiplication(matrixA, matrixB);
        }
        if ("exit".equalsIgnoreCase(operation)) {
            return false;
        }

        // Converte a matriz para uma string JSON usando um método auxiliar
        if(matrix == null){
            System.out.println("ERRO, RESPOSTA É NULL");
        }
        json = matrizParaJson(matrix);
        // Envia o JSON para o servidor
        out.println(json);
        return true;
    }
}
