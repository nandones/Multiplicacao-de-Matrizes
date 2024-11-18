package com.mycompany.servmatrices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServMatrixUtils {

    /**
     * Divide uma matriz[][] em duas, método para distribuir o processamento.<br>
     * A matriz é quebrada com metade das linhas para cada, caso não seja par, A fica<br>
     * com a linha extra.
     * @param matrix[][]
     * @return new double[][][]{matrizA, matrizB}// retorna 2 matrizes de duas dimensões<br>
     * como uma matriz de 3 dimensões.
     */
    public static double[][][] splitMatrix(double[][] matrix) {
    int rows = matrix.length;
    int columns = matrix[0].length;

    // Calcula o número de linhas para cada matriz
    int rowsA = (rows + 1) / 2; // A matriz A fica com a linha extra, se existir
    int rowsB = rows / 2;

    // Inicializa as matrizes A e B
    double[][] matrixA = new double[rowsA][columns];
    double[][] matrixB = new double[rowsB][columns];

    // Preenche a matriz A com a primeira metade (incluindo a linha extra, se existir)
    for (int i = 0; i < rowsA; i++) {
        System.arraycopy(matrix[i], 0, matrixA[i], 0, columns);
    }

    // Preenche a matriz B com a segunda metade
    for (int i = 0; i < rowsB; i++) {
        System.arraycopy(matrix[i + rowsA], 0, matrixB[i], 0, columns);
    }

    return new double[][][]{matrixA, matrixB};
}
    /**
     * método para após o processamento distribuído.<br>
     * retorna uma matriz que concatena da primeira linha de matrizA até a última de matrizB.
     * @param matrixA
     * @param matrixB
     * @return matrizConcatenada
     */
    public static double[][] concatenateMatrices(double[][] matrixA, double[][] matrixB) {
    int rowsA = matrixA.length;
    int rowsB = matrixB.length;
    int columns = matrixA[0].length;

    // A matriz resultante terá um número de linhas igual à soma das linhas de A e B
    double[][] concatenatedMatrix = new double[rowsA + rowsB][columns];

    // Copia as linhas de matrizA para matrizConcatenada
    for (int i = 0; i < rowsA; i++) {
        System.arraycopy(matrixA[i], 0, concatenatedMatrix[i], 0, columns);
    }

    // Copia as linhas de matrizB para matrizConcatenada, após as linhas de matrizA
    for (int i = 0; i < rowsB; i++) {
        System.arraycopy(matrixB[i], 0, concatenatedMatrix[i + rowsA], 0, columns);
    }

    return concatenatedMatrix;
}

    /**
     * Método elegante que garante numero fixo de 2 casas decimais e padding padrão para exibir os valores dfa matriz
     * @param matrix
     * @param matrixName 
     */
    public static void printMatriz(double[][] matrix, String matrixName) {
        // Definir o padding para um alinhamento mais uniforme
        int padding = 8; // Número de espaços reservados para cada número

        System.out.println("[Matriz " + matrixName + "]:");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("|");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%" + padding + ".2f", matrix[i][j]); // Formato fixo com 2 casas decimais
            }
            System.out.println("   |"); // Fecha cada linha da matriz
        }
    }



    /**
     * Multiplica duas matrizes quadradas., desde que o numero de colunas da
     * primeira<br>
     * matriz seja igual ao número de limhas da segunda, sendo [linha][coluna]
     *
     * @param matA
     * @param matB
     * @return A matriz resultante da multiplicação.
     */
    public static double[][] multiplyMatrices(double[][] matA, double[][] matB) {
        int rowsA = matA.length;
        int columnsA = matA[0].length;
        int rowsB = matB.length;
        int columnsB = matB[0].length;

        // Verificação de compatibilidade para multiplicação
        if (columnsA != rowsB) {
            throw new IllegalArgumentException("Número de colunas da primeira matriz deve ser igual ao número de linhas da segunda matriz.");
        }

        // Criação da matriz resultante
        double[][] result = new double[rowsA][columnsB];

        // Multiplicação das matrizes
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < columnsB; j++) {
                for (int k = 0; k < columnsA; k++) {
                    result[i][j] += matA[i][k] * matB[k][j];
                }
            }
        }

        return result;
    }
}
