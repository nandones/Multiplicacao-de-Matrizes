package com.mycompany.servmatrices;

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
public class ServMatrixApp {

    public static Scanner input = new Scanner(System.in);

    public static double[][] localMultipleThreadMultiplication(double[][] matrixA, double[][] matrixB) throws InterruptedException, ExecutionException {
        System.out.println("Distribuindo matriz A...");
        double[][][] resultadoDistribuicao = ServMatrixUtils.splitMatrix(matrixA);
        double[][] matrizA1 = resultadoDistribuicao[0];
        double[][] matrizA2 = resultadoDistribuicao[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> tarefa1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1...");
            return ServMatrixUtils.multiplyMatrices(matrizA1, matrixB);
        };

        Callable<double[][]> tarefa2 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA2...");
            return ServMatrixUtils.multiplyMatrices(matrizA2, matrixB);
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
        return ServMatrixUtils.concatenateMatrices(resultado1, resultado2);
    }

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        ServCommunicationUtils.startServer();
    }
}
