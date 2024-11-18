package com.mycompany.multmatrizes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientMatrizUtils {


    /**
     * Lê o arquivo da matriz (txt) e retorna uma nova matriz double[][]<br>
     * é nexcessario que a variável estatica dimensions já tenha sido
     * atribuída;<br>
     * confira o método "atribuiDimensoes"
     *
     * @param dimensions
     * @see atribuiDimensoes
     * @param filePath
     * @return
     */
    public static double[][] instanciarMatriz(String filePath, int dimensions) {
        double[][] newMat = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            StringBuilder content = new StringBuilder();
            String line;

            //Levando em consideração que é uma matriz quadrada
            while ((line = reader.readLine()) != null) {
                content.append(line).append(' ');
            }
            reader.close();

            newMat = new double[dimensions][dimensions];
            String matContent = content.toString();
            String[] matArray = matContent.split(" ");

            int k = 0; //Contador p/ iterar sobre matArray[]
            for (int i = 0; i < dimensions; i++) {
                for (int j = 0; j < dimensions; j++) {
                    newMat[i][j] = Double.parseDouble(matArray[k]);
                    k++;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClientMatrizUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientMatrizUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return newMat;
    }
    /**
     * Divide uma matriz[][] em duas, método para distribuir o processamento.<br>
     * A matriz é quebrada com metade das linhas para cada, caso não seja par, A fica<br>
     * com a linha extra.
     * @param matriz[][]
     * @return new double[][][]{matrizA, matrizB}// retorna 2 matrizes de duas dimensões<br>
     * como uma matriz de 3 dimensões.
     */
    public static double[][][] dividirMatrizEmDuas(double[][] matriz) {
    int linhas = matriz.length;
    int colunas = matriz[0].length;

    // Calcula o número de linhas para cada matriz
    int linhasA = (linhas + 1) / 2; // A matriz A fica com a linha extra, se existir
    int linhasB = linhas / 2;

    // Inicializa as matrizes A e B
    double[][] matrizA = new double[linhasA][colunas];
    double[][] matrizB = new double[linhasB][colunas];

    // Preenche a matriz A com a primeira metade (incluindo a linha extra, se existir)
    for (int i = 0; i < linhasA; i++) {
        System.arraycopy(matriz[i], 0, matrizA[i], 0, colunas);
    }

    // Preenche a matriz B com a segunda metade
    for (int i = 0; i < linhasB; i++) {
        System.arraycopy(matriz[i + linhasA], 0, matrizB[i], 0, colunas);
    }

    return new double[][][]{matrizA, matrizB};
}
    /**
     * método para após o processamento distribuído.<br>
     * retorna uma matriz que concatena da primeira linha de matrizA até a última de matrizB.
     * @param matrizA
     * @param matrizB
     * @return matrizConcatenada
     */
    public static double[][] concatenar2Matrizes(double[][] matrizA, double[][] matrizB) {
    int linhasA = matrizA.length;
    int linhasB = matrizB.length;
    int colunas = matrizA[0].length;

    // A matriz resultante terá um número de linhas igual à soma das linhas de A e B
    double[][] matrizConcatenada = new double[linhasA + linhasB][colunas];

    // Copia as linhas de matrizA para matrizConcatenada
    for (int i = 0; i < linhasA; i++) {
        System.arraycopy(matrizA[i], 0, matrizConcatenada[i], 0, colunas);
    }

    // Copia as linhas de matrizB para matrizConcatenada, após as linhas de matrizA
    for (int i = 0; i < linhasB; i++) {
        System.arraycopy(matrizB[i], 0, matrizConcatenada[i + linhasA], 0, colunas);
    }

    return matrizConcatenada;
}

    /**
     * Método elegante que garante numero fixo de 2 casas decimais e padding padrão para exibir os valores dfa matriz
     * @param matriz
     * @param nomeMatriz 
     */
    public static void printaMatriz(double[][] matriz, String nomeMatriz) {
        // Definir o padding para um alinhamento mais uniforme
        int padding = 8; // Número de espaços reservados para cada número

        System.out.println("[Matriz " + nomeMatriz + "]:");
        for (int i = 0; i < matriz.length; i++) {
            System.out.print("|");
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.printf("%" + padding + ".2f", matriz[i][j]); // Formato fixo com 2 casas decimais
            }
            System.out.println("   |"); // Fecha cada linha da matriz
        }
    }

    /**
     * Identifica as dimensões da matriz (linha e coluna, trabalha apenas com
     * matrizes quadradas) Atribui valor à variavel estática dimensions.
     *
     * @param filePath
     * @return 
     */
    public static int atribuiDimensoes(String filePath) {
        BufferedReader reader;
        int dimensions = 0;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            StringBuilder content = new StringBuilder();
            String line;

            line = reader.readLine();
            content.append(line);
            String result = content.toString();
            String[] arrayResult = result.split(" ");
            dimensions = arrayResult.length;
            reader.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClientMatrizUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientMatrizUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return dimensions;

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
    public static double[][] multiplicaMatrizes(double[][] matA, double[][] matB) {
        int linhasA = matA.length;
        int colunasA = matA[0].length;
        int linhasB = matB.length;
        int colunasB = matB[0].length;

        // Verificação de compatibilidade para multiplicação
        if (colunasA != linhasB) {
            throw new IllegalArgumentException("Número de colunas da primeira matriz deve ser igual ao número de linhas da segunda matriz.");
        }

        // Criação da matriz resultante
        double[][] resultado = new double[linhasA][colunasB];

        // Multiplicação das matrizes
        for (int i = 0; i < linhasA; i++) {
            for (int j = 0; j < colunasB; j++) {
                for (int k = 0; k < colunasA; k++) {
                    resultado[i][j] += matA[i][k] * matB[k][j];
                }
            }
        }

        return resultado;
    }

    public static void escreveResultadoTXT(double[][] matriz, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                // Formatar cada valor com quatro casas decimais e necessário para garantir que seja 
                //impresso com valores inteiros separados dor decimais por ponto e não vírgula.
                resultado.append(String.format(Locale.US, "%.4f", matriz[i][j]));
                // Adicionar espaço entre valores na mesma linha, exceto no último
                if (j < matriz[i].length - 1) {
                    resultado.append(" ");
                }
            }
            // Adicionar caracteres Unicode para a nova linha (carriage return + line feed)
            //é necessário para obter o hash identico ao gabarito.
            resultado.append('\r').append('\n');
        }
        writer.write(resultado.toString());
        writer.close();
    }
}
