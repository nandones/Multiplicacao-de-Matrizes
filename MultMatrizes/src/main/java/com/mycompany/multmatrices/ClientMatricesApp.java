package com.mycompany.multmatrices;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author NANDONES
 */
public class ClientMatricesApp {

    /**//**//**//**//**//**//**//**//**//**//**//**/
    /**/////////////////ATENÇÃO://///////////////**/
    /**///CONFIGURE ABAIXO OS PATHS DAS MATRIZES/**/
    /**//**//**//**//**//**//**//**//**//**//**//**/
    static final String MATRIZAPATH = "dummyA.txt";
    static final String MATRIZBPATH = "dummyB.txt";
    //Antes de instanciar uma matriz é necessário ter o número de elementos em cada linha e coluna,
    //sendo necessáriamente igauis (Matriz i j, onde i = j).
    static int rowsAndColumnsSize = 0;
    //Matrizes que serão preenchidas pelos dados desserializados.
    static double[][] matA;
    static double[][] matB;
    // Scanner para inserção de quaisquer dados.
    public static Scanner input = new Scanner(System.in);

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        System.out.println("digite o IP do server:");
        ClientCommunicationUtils.serverHost = input.next();
        System.out.println("digite a sigla correspondente a operação que deseja fazer:\n"
                + "LS: Local e SingleThread\n"
                + "LM: Local e MultiThread\n"
                + "RS: Remote e MultiThread\n"
                + "RM: Remote e MultiThread\n"
                + "EXIT: Encerra a execução");
        String menu = input.nextLine().toLowerCase();
        while (!menu.equalsIgnoreCase("LS") && !menu.equalsIgnoreCase("LM") && !menu.equalsIgnoreCase("RS") && !menu.equalsIgnoreCase("RM") && !menu.equalsIgnoreCase("EXIT")) {
            System.out.println("opção inválida! insira uma sigla disponível (LS,LM,RS,RM,EXIT):");
            menu = input.nextLine();
        }
        switch (menu) {
            case "ls" ->
                localSingleThreadMultiplication();
            case "lm" ->
                localMultipleThreadMultiplication();
            case "rs" ->
                remoteSingleThreadMultiplication();
            case "rm" ->
                remoteMultipleThreadMultiplication();
            case "exit" ->
                ClientCommunicationUtils.closeServer();
        }
        System.out.println("\n");
        while (!menu.equalsIgnoreCase("exit")) {
            System.out.println("digite a sigla correspondente a operação que deseja fazer:\n"
                    + "LS: Local e SingleThread\n"
                    + "LM: Local e MultiThread\n"
                    + "RS: Remote e MultiThread\n"
                    + "RM: Remote e MultiThread\n"
                    + "EXIT: Encerra a execução");
            menu = input.nextLine().toLowerCase();
            while (!menu.equalsIgnoreCase("LS") && !menu.equalsIgnoreCase("LM") && !menu.equalsIgnoreCase("RS") && !menu.equalsIgnoreCase("RM") && !menu.equalsIgnoreCase("EXIT")) {
                System.out.println("opção inválida! insira uma sigla disponível (LS,LM,RS,RM,EXIT):");
                menu = input.nextLine();
            }
            switch (menu) {
                case "ls" ->
                    localSingleThreadMultiplication();
                case "lm" ->
                    localMultipleThreadMultiplication();
                case "rs" ->
                    remoteSingleThreadMultiplication();
                case "rm" ->
                    remoteMultipleThreadMultiplication();
                case "exit" ->
                    ClientCommunicationUtils.closeServer();
            }
            System.out.println("\n");
        }

    }

    public static void test() throws IOException {
        double[][] matrixA = {{1, 2}, {3, 4}};
        double[][] matrixB = {{5, 6}, {7, 8}};

        ClientCommunicationUtils.sendRequestToServer(matrixA, matrixB, "singleThread");

        // Enviar comando para encerrar o servidor
        //ComunicacaoUtils.enviaOperacao("exit", matrixA, matrixB, "127.0.0.1");
    }

    public static void localSingleThreadMultiplication() throws IOException {
        rowsAndColumnsSize = ClientMatrixUtils.calculateSize(MATRIZAPATH);
        System.out.println("desserializando a matriz A...");
        matA = ClientMatrixUtils.desserializeMatrix(MATRIZAPATH, rowsAndColumnsSize);
        System.out.println("desserializando a matriz B...");
        matB = ClientMatrixUtils.desserializeMatrix(MATRIZBPATH, rowsAndColumnsSize);
        //MatrizUtils.printaMatriz(MatrizUtils.matA, "A");
        //System.out.println("");
        //MatrizUtils.printaMatriz(MatrizUtils.matB, "B");
        System.out.println("Calculando a multiplicação monothread...");
        double[][] result = ClientMatrixUtils.multiplyMatrices(matA, matB);
        //ClientMatrizUtils.printMatrix(result, "resultado");
        System.out.println("Escrevendo result.txt");
        ClientMatrixUtils.writeResultTXT(result, "result.txt");
    }

    public static void localMultipleThreadMultiplication() throws IOException, InterruptedException, ExecutionException {
        rowsAndColumnsSize = ClientMatrixUtils.calculateSize(MATRIZAPATH);
        System.out.println("desserializando a matriz A...");
        matA = ClientMatrixUtils.desserializeMatrix(MATRIZAPATH, rowsAndColumnsSize);
        System.out.println("desserializando a matriz B...");
        matB = ClientMatrixUtils.desserializeMatrix(MATRIZBPATH, rowsAndColumnsSize);

        System.out.println("Distribuindo matriz A...");
        double[][][] matricesSplited = ClientMatrixUtils.SplitMatrix(matA);
        double[][] matrixA1 = matricesSplited[0];
        double[][] matrixA2 = matricesSplited[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> task1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1...");
            return ClientMatrixUtils.multiplyMatrices(matrixA1, matB);
        };

        Callable<double[][]> task2 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA2...");
            return ClientMatrixUtils.multiplyMatrices(matrixA2, matB);
        };

        // Envia as tarefas para o ExecutorService
        Future<double[][]> futureResult1 = executor.submit(task1);
        Future<double[][]> futureResult2 = executor.submit(task2);

        // Aguarda o término das threads e obtém os resultados
        double[][] result1 = futureResult1.get();
        double[][] result2 = futureResult2.get();

        // Encerra o ExecutorService
        executor.shutdown();

        // Concatena os resultados
        System.out.println("Concatenando resultados...");
        double[][] result = ClientMatrixUtils.concatenateMatrices(result1, result2);
        //ClientMatrizUtils.printMatrix(result, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo result.txt...");
        ClientMatrixUtils.writeResultTXT(result, "result.txt");
    }

    public static void remoteSingleThreadMultiplication() throws IOException, InterruptedException, ExecutionException {
        rowsAndColumnsSize = ClientMatrixUtils.calculateSize(MATRIZAPATH);
        System.out.println("desserializando a matriz A...");
        matA = ClientMatrixUtils.desserializeMatrix(MATRIZAPATH, rowsAndColumnsSize);
        System.out.println("desserializando a matriz B...");
        matB = ClientMatrixUtils.desserializeMatrix(MATRIZBPATH, rowsAndColumnsSize);

        System.out.println("Distribuindo matriz A...");
        double[][][] matricesSplited = ClientMatrixUtils.SplitMatrix(matA);
        double[][] matrixA1 = matricesSplited[0];
        double[][] matrixA2 = matricesSplited[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> task1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1...");
            return ClientMatrixUtils.multiplyMatrices(matrixA1, matB);
        };

        Callable<double[][]> task2 = () -> {
            System.out.println("Enviando requisição para multiplicação da matrizA2...");
            return ClientCommunicationUtils.sendRequestToServer(matrixA2, matB, "SingleThread");
        };

        // Envia as tarefas para o ExecutorService
        Future<double[][]> futureResult1 = executor.submit(task1);
        Future<double[][]> futureResult2 = executor.submit(task2);

        // Aguarda o término das threads e obtém os resultados
        double[][] result1 = futureResult1.get();
        double[][] result2 = futureResult2.get();

        // Encerra o ExecutorService
        executor.shutdown();

        // Concatena os resultados
        System.out.println("Concatenando resultados...");
        double[][] result = ClientMatrixUtils.concatenateMatrices(result1, result2);
        //ClientMatrizUtils.printMatrix(result, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo result.txt...");
        ClientMatrixUtils.writeResultTXT(result, "result.txt");
    }

    /**
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void remoteMultipleThreadMultiplication() throws IOException, InterruptedException, ExecutionException {
        rowsAndColumnsSize = ClientMatrixUtils.calculateSize(MATRIZAPATH);
        System.out.println("desserializando a matriz A...");
        matA = ClientMatrixUtils.desserializeMatrix(MATRIZAPATH, rowsAndColumnsSize);
        System.out.println("desserializando a matriz B...");
        matB = ClientMatrixUtils.desserializeMatrix(MATRIZBPATH, rowsAndColumnsSize);

        System.out.println("Distribuindo matriz A...");
        double[][][] splitResult = ClientMatrixUtils.SplitMatrix(matA);
        double[][] matrixA1 = splitResult[0];
        double[][] matrixA2 = splitResult[1];

        System.out.println("Distribuindo matriz A1...");
        splitResult = ClientMatrixUtils.SplitMatrix(matrixA1);
        double[][] matrixA1_1 = splitResult[0];
        double[][] matrixA1_2 = splitResult[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> task1 = () -> {
            System.out.println("Executando thread para multiplicação com matrizA1_1...");
            return ClientMatrixUtils.multiplyMatrices(matrixA1_1, matB);
        };

        Callable<double[][]> task2 = () -> {
            System.out.println("Executando thread para multiplicação com matrizA1_2...");
            return ClientMatrixUtils.multiplyMatrices(matrixA1_2, matB);
        };

        Callable<double[][]> task3 = () -> {
            System.out.println("Enviando req ao server para multiplicação com matrizA2...");
            return ClientCommunicationUtils.sendRequestToServer(matrixA2, matB, "multiThread");
        };

        // Envia as tarefas para o ExecutorService
        Future<double[][]> futureResult1 = executor.submit(task1);
        Future<double[][]> futureResult2 = executor.submit(task2);
        Future<double[][]> futureResult3 = executor.submit(task3);

        // Aguarda o término das threads e obtém os resultados
        double[][] result1 = futureResult1.get();
        double[][] result2 = futureResult2.get();
        double[][] result3 = futureResult3.get();

        // Encerra o ExecutorService
        executor.shutdown();

        // Concatena os resultados
        System.out.println("Concatenando resultados...");
        double[][] result = ClientMatrixUtils.concatenateMatrices(result1, result2);
        result = ClientMatrixUtils.concatenateMatrices(result, result3);
        //ClientMatrizUtils.printaMatriz(resultado, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo result.txt...");
        ClientMatrixUtils.writeResultTXT(result, "result.txt");
    }
}
