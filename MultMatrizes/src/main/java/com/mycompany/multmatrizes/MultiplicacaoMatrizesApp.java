package com.mycompany.multmatrizes;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author NANDONES
 */
public class MultiplicacaoMatrizesApp {
    
    /**//**//**//**//**//**//**//**//**//**//**//**/
    /**/////////////////ATENÇÃO://///////////////**/
    /**///CONFIGURE ABAIXO OS PATHS DAS MATRIZES/**/
    /**//**//**//**//**//**//**//**//**//**//**//**/
    static final String MATRIZAPATH= "matA.txt";
    static final String MATRIZBPATH= "matB.txt";

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        localSingleThreadMultiplication();
        //localMultipleThreadMultiplication();
        //teste();
    }

    public static void teste() {
        /*MatrizUtils.dimensions = 4;
        double[][] matA ={
        {2.0, 1.0, 1.0, 1.0},
        {1.0, 1.0, 1.0, 1.0},
        {1.0, 1.0, 1.0, 1.0},};
        MatrizUtils.printaMatriz(matA, "A");
        
        double[][] matB ={
        {1.0, 1.0, 1.0},
        {1.0, 1.0, 1.0},
        {1.0, 1.0, 1.0},
        {1.0, 1.0, 1.0}};
        MatrizUtils.printaMatriz(matB, "B");
        
        double [][] matC = MatrizUtils.multiplicaMatrizes(matB, matA);
        MatrizUtils.printaMatriz(matC, "resultado");*/

        double[][] matrizOriginalMultiplicando = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9},
            {10, 11, 12},
            {13, 14, 15}
        };
        
        double[][] matrizMultiplicador = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9},
        };

        double[][][] resultado = MatrizUtils.dividirMatrizEmDuas(matrizOriginalMultiplicando);
        double[][] matrizA = resultado[0];
        double[][] matrizB = resultado[1];

        MatrizUtils.printaMatriz(matrizA, "A");
        MatrizUtils.printaMatriz(matrizB, "B");
        
        double[][] matrizC = MatrizUtils.multiplicaMatrizes(matrizA, matrizMultiplicador);
        MatrizUtils.printaMatriz(matrizC, "C");
        
        double[][] matrizD = MatrizUtils.multiplicaMatrizes(matrizB, matrizMultiplicador);
        MatrizUtils.printaMatriz(matrizD, "D");
        
        double[][] concatenada = MatrizUtils.concatenar2Matrizes(matrizC, matrizD);
        MatrizUtils.printaMatriz(concatenada, "Resultado:");
        
    }

    public static void localSingleThreadMultiplication() throws IOException {
        MatrizUtils.atribuiDimensoes(MATRIZAPATH);
        System.out.println("Instanciando a matriz A...");
        MatrizUtils.matA = MatrizUtils.instanciarMatriz(MATRIZAPATH);
        System.out.println("Instanciando a matriz B...");
        MatrizUtils.matB = MatrizUtils.instanciarMatriz(MATRIZBPATH);
        //MatrizUtils.printaMatriz(MatrizUtils.matA, "A");
        //System.out.println("");
        //MatrizUtils.printaMatriz(MatrizUtils.matB, "B");
        System.out.println("Calculando a multiplicação monothread...");
        double[][] resultado = MatrizUtils.multiplicaMatrizes(MatrizUtils.matA, MatrizUtils.matB);
        System.out.println("");
        //MatrizUtils.printaMatriz(resultado, "resultado");
        System.out.println("Escrevendo resultado.txt");
        MatrizUtils.escreveResultadoTXT(resultado, "resultado.txt");
    }
    
    public static void localMultipleThreadMultiplication() throws IOException, InterruptedException, ExecutionException {
        MatrizUtils.atribuiDimensoes(MATRIZAPATH);
        System.out.println("Instanciando a matriz A...");
        MatrizUtils.matA = MatrizUtils.instanciarMatriz(MATRIZAPATH);
        System.out.println("Instanciando a matriz B...");
        MatrizUtils.matB = MatrizUtils.instanciarMatriz(MATRIZBPATH);

        System.out.println("Distribuindo matriz A...");
        double[][][] resultadoDistribuicao = MatrizUtils.dividirMatrizEmDuas(MatrizUtils.matA);
        double[][] matrizA1 = resultadoDistribuicao[0];
        double[][] matrizA2 = resultadoDistribuicao[1];

        // Cria o ExecutorService com duas threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define as tarefas de multiplicação para cada metade da matriz
        Callable<double[][]> tarefa1 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA1...");
            return MatrizUtils.multiplicaMatrizes(matrizA1, MatrizUtils.matB);
        };

        Callable<double[][]> tarefa2 = () -> {
            System.out.println("Executando thread para multiplicação da matrizA2...");
            return MatrizUtils.multiplicaMatrizes(matrizA2, MatrizUtils.matB);
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
        double[][] resultado = MatrizUtils.concatenar2Matrizes(resultado1, resultado2);
        //MatrizUtils.printaMatriz(resultado, "resultado");

        // Escreve o resultado em um arquivo
        System.out.println("Escrevendo resultado.txt...");
        MatrizUtils.escreveResultadoTXT(resultado, "resultado.txt");
    }

}
