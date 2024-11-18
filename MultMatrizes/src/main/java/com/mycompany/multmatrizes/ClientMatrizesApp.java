package com.mycompany.multmatrizes;

import java.io.IOException;
import java.util.Arrays;
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
public class ClientMatrizesApp {

    /**//**//**//**//**//**//**//**//**//**//**//**/
 /**/////////////////ATENÇÃO://///////////////**/
    /**///CONFIGURE ABAIXO OS PATHS DAS MATRIZES/**/
    /**//**//**//**//**//**//**//**//**//**//**//**/
    static final String MATRIZAPATH = "matA.txt";
    static final String MATRIZBPATH = "matB.txt";

    static int dimensions = 0;
    static double[][] matA;
    static double[][] matB;

    public static Scanner input = new Scanner(System.in);

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.ExecutionException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        localSingleThreadMultiplication();
        //localMultipleThreadMultiplication();
        //teste();
        //remoteSingleThreadMultiplication();
        //remoteMultipleThreadMultiplication2();
    }

    public static void teste() throws IOException {
        double[][] matrixA = {{1, 2}, {3, 4}};
        double[][] matrixB = {{5, 6}, {7, 8}};

        ClientComunicacaoUtils.enviaRequisicaoAoServ(matrixA, matrixB, "singleThread");

        // Enviar comando para encerrar o servidor
        //ComunicacaoUtils.enviaOperacao("exit", matrixA, matrixB, "127.0.0.1");
    }

    public static void localSingleThreadMultiplication() throws IOException {
        dimensions = ClientMatrizUtils.atribuiDimensoes(MATRIZAPATH);
        System.out.println("Instanciando a matriz A...");
        matA = ClientMatrizUtils.instanciarMatriz(MATRIZAPATH, dimensions);
        System.out.println("Instanciando a matriz B...");
        matB = ClientMatrizUtils.instanciarMatriz(MATRIZBPATH, dimensions);
        //MatrizUtils.printaMatriz(MatrizUtils.matA, "A");
        //System.out.println("");
        //MatrizUtils.printaMatriz(MatrizUtils.matB, "B");
        System.out.println("Calculando a multiplicação monothread...");
        double[][] resultado = ClientMatrizUtils.multiplicaMatrizes(matA, matB);
        System.out.println("");
        ClientMatrizUtils.printaMatriz(resultado, "resultado");
        System.out.println("Escrevendo resultado.txt");
        ClientMatrizUtils.escreveResultadoTXT(resultado, "resultado.txt");
    }

    public static void localMultipleThreadMultiplication() throws IOException, InterruptedException, ExecutionException {
        dimensions = ClientMatrizUtils.atribuiDimensoes(MATRIZAPATH);
        System.out.println("Instanciando a matriz A...");
        matA = ClientMatrizUtils.instanciarMatriz(MATRIZAPATH, dimensions);
        System.out.println("Instanciando a matriz B...");
        matB = ClientMatrizUtils.instanciarMatriz(MATRIZBPATH, dimensions);

        System.out.println("Distribuindo matriz A...");
        double[][][] resultadoDistribuicao = ClientMatrizUtils.dividirMatrizEmDuas(matA);
        double[][] matrizA1 = resultadoDistribuicao[0];
        double[][] matrizA2 = resultadoDistribuicao[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> tarefa1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1...");
            return ClientMatrizUtils.multiplicaMatrizes(matrizA1, matB);
        };

        Callable<double[][]> tarefa2 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA2...");
            return ClientMatrizUtils.multiplicaMatrizes(matrizA2, matB);
        };

        // Envia as tarefas para o ExecutorService
        Future<double[][]> futureResultado1 = executor.submit(tarefa1);
        Future<double[][]> futureResultado2 = executor.submit(tarefa2);

        // Aguarda o término das threads e obtém os resultados
        double[][] resultado1 = futureResultado1.get();
        double[][] resultado2 = futureResultado2.get();

        // Encerra o ExecutorService
        executor.shutdown();

        // Concatena os resultados
        System.out.println("Concatenando resultados...");
        double[][] resultado = ClientMatrizUtils.concatenar2Matrizes(resultado1, resultado2);
        ClientMatrizUtils.printaMatriz(resultado, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo resultado.txt...");
        ClientMatrizUtils.escreveResultadoTXT(resultado, "resultado.txt");
    }

    public static void remoteSingleThreadMultiplication() throws IOException, InterruptedException, ExecutionException {
        dimensions = ClientMatrizUtils.atribuiDimensoes(MATRIZAPATH);
        System.out.println("Instanciando a matriz A...");
        matA = ClientMatrizUtils.instanciarMatriz(MATRIZAPATH, dimensions);
        System.out.println("Instanciando a matriz B...");
        matB = ClientMatrizUtils.instanciarMatriz(MATRIZBPATH, dimensions);

        System.out.println("Distribuindo matriz A...");
        double[][][] resultadoDistribuicao = ClientMatrizUtils.dividirMatrizEmDuas(matA);
        double[][] matrizA1 = resultadoDistribuicao[0];
        double[][] matrizA2 = resultadoDistribuicao[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> tarefa1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1...");
            return ClientMatrizUtils.multiplicaMatrizes(matrizA1, matB);
        };

        Callable<double[][]> tarefa2 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA2...");
            return ClientComunicacaoUtils.enviaRequisicaoAoServ(matrizA2, matB, "SingleThread");
        };

        // Envia as tarefas para o ExecutorService
        Future<double[][]> futureResultado1 = executor.submit(tarefa1);
        Future<double[][]> futureResultado2 = executor.submit(tarefa2);

        // Aguarda o término das threads e obtém os resultados
        double[][] resultado1 = futureResultado1.get();
        double[][] resultado2 = futureResultado2.get();

        // Encerra o ExecutorService
        executor.shutdown();

        // Concatena os resultados
        System.out.println("Concatenando resultados...");
        double[][] resultado = ClientMatrizUtils.concatenar2Matrizes(resultado1, resultado2);
        ClientMatrizUtils.printaMatriz(resultado, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo resultado.txt...");
        ClientMatrizUtils.escreveResultadoTXT(resultado, "resultado.txt");
    }

    public static void remoteMultipleThreadMultiplication() throws IOException, InterruptedException, ExecutionException {
        dimensions = ClientMatrizUtils.atribuiDimensoes(MATRIZAPATH);
        System.out.println("Instanciando a matriz A...");
        matA = ClientMatrizUtils.instanciarMatriz(MATRIZAPATH, dimensions);
        System.out.println("Instanciando a matriz B...");
        matB = ClientMatrizUtils.instanciarMatriz(MATRIZBPATH, dimensions);

        System.out.println("Distribuindo matriz A...");
        double[][][] resultadoDistribuicao = ClientMatrizUtils.dividirMatrizEmDuas(matA);
        double[][] matrizA1 = resultadoDistribuicao[0];
        double[][] matrizA2 = resultadoDistribuicao[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> tarefa1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1...");
            System.out.println("Distribuindo matriz A1...");
            double[][][] resultadoDistribuicaoMatrizA1 = ClientMatrizUtils.dividirMatrizEmDuas(matrizA1);
            double[][] matrizA1_1 = resultadoDistribuicaoMatrizA1[0];
            double[][] matrizA1_2 = resultadoDistribuicaoMatrizA1[1];

            // Cria o ExecutorService com duas threads
            ExecutorService executor_2 = Executors.newFixedThreadPool(2);

            // Define as tarefas de multiplicação para cada metade da matriz
            Callable<double[][]> tarefa1_1 = () -> {
                System.out.println("Executando thread para multiplicação da matrizA1...");

                return ClientMatrizUtils.multiplicaMatrizes(matrizA1_1, matB);
            };

            Callable<double[][]> tarefa1_2 = () -> {
                System.out.println("Executando thread para multiplicação da matrizA2...");
                return ClientMatrizUtils.multiplicaMatrizes(matrizA1_2, matB);
            };

            // Envia as tarefas para o ExecutorService
            Future<double[][]> futureResultado1 = executor_2.submit(tarefa1_1);
            Future<double[][]> futureResultado2 = executor_2.submit(tarefa1_2);

            // Aguarda o término das threads e obtém os resultados
            double[][] resultado1 = futureResultado1.get();
            double[][] resultado2 = futureResultado2.get();

            // Concatena os resultados
            System.out.println("Concatenando resultados de A1...");
            double[][] resultado = ClientMatrizUtils.concatenar2Matrizes(resultado1, resultado2);
            ClientMatrizUtils.printaMatriz(resultado, "resultado");
            executor_2.shutdown();
            return ClientMatrizUtils.multiplicaMatrizes(matrizA1, matB);
            
        };///////////////////////////

        Callable<double[][]> tarefa2 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA2...");
            return ClientComunicacaoUtils.enviaRequisicaoAoServ(matrizA2, matB, "MultiThread");
        };

        // Envia as tarefas para o ExecutorService
        Future<double[][]> futureResultado1 = executor.submit(tarefa1);
        Future<double[][]> futureResultado2 = executor.submit(tarefa2);

        // Aguarda o término das threads e obtém os resultados
        double[][] resultado1 = futureResultado1.get();
        double[][] resultado2 = futureResultado2.get();

        // Encerra o ExecutorService
        executor.shutdown();

        // Concatena os resultados
        System.out.println("Concatenando resultados...");
        double[][] resultado = ClientMatrizUtils.concatenar2Matrizes(resultado1, resultado2);
        ClientMatrizUtils.printaMatriz(resultado, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo resultado.txt...");
        ClientMatrizUtils.escreveResultadoTXT(resultado, "resultado.txt");
        System.out.println("gg easy");
    }
    
    public static void remoteMultipleThreadMultiplication2() throws IOException, InterruptedException, ExecutionException {
        dimensions = ClientMatrizUtils.atribuiDimensoes(MATRIZAPATH);
        System.out.println("Instanciando a matriz A...");
        matA = ClientMatrizUtils.instanciarMatriz(MATRIZAPATH, dimensions);
        System.out.println("Instanciando a matriz B...");
        matB = ClientMatrizUtils.instanciarMatriz(MATRIZBPATH, dimensions);

        System.out.println("Distribuindo matriz A...");
        double[][][] resultadoDistribuicao = ClientMatrizUtils.dividirMatrizEmDuas(matA);
        double[][] matrizA1 = resultadoDistribuicao[0];
        double[][] matrizA2 = resultadoDistribuicao[1];
        
        System.out.println("Distribuindo matriz A1...");
        resultadoDistribuicao = ClientMatrizUtils.dividirMatrizEmDuas(matrizA1);
        double[][] matrizA1_1 = resultadoDistribuicao[0];
        double[][] matrizA1_2 = resultadoDistribuicao[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> tarefa1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1_1...");
            return ClientMatrizUtils.multiplicaMatrizes(matrizA1_1, matB);
        };

        Callable<double[][]> tarefa2 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1_2...");
            return ClientMatrizUtils.multiplicaMatrizes(matrizA1_2, matB);
        };
        
        Callable<double[][]> tarefa3 = () -> {
            System.out.println("Enviando req ao server para multiplicação da matrizA2...");
            return ClientComunicacaoUtils.enviaRequisicaoAoServ(matrizA2, matB, "multiThread");
        };

        // Envia as tarefas para o ExecutorService
        Future<double[][]> futureResultado1 = executor.submit(tarefa1);
        Future<double[][]> futureResultado2 = executor.submit(tarefa2);
        Future<double[][]> futureResultado3 = executor.submit(tarefa3);
        

        // Aguarda o término das threads e obtém os resultados
        double[][] resultado1 = futureResultado1.get();
        double[][] resultado2 = futureResultado2.get();
        double[][] resultado3 = futureResultado3.get();

        // Encerra o ExecutorService
        executor.shutdown();

        // Concatena os resultados
        System.out.println("Concatenando resultados...");
        double[][] resultado = ClientMatrizUtils.concatenar2Matrizes(resultado1, resultado2);
        resultado = ClientMatrizUtils.concatenar2Matrizes(resultado, resultado3);
        //ClientMatrizUtils.printaMatriz(resultado, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo resultado.txt...");
        ClientMatrizUtils.escreveResultadoTXT(resultado, "resultado.txt");
    }
}