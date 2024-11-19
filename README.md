# Multiplicação de Matrizes com Comunicação Cliente-Servidor
Projeto feito e compatível na IDE Netbeans;
Este projeto consiste em um conjunto de duas aplicações Java (cliente e servidor) para a multiplicação de matrizes quadradas. Seu enfoque é o paralelizar tal tarefa, multiplicando A por B, mas podendo fazer de forma sequencial, com 2 threads locais, quebrando a matriz A em 2, 2 threads, uma local e outra remota, também quebrando A em 2, ou com 4 threads, logicamente quebrando a matriz A em 4 Threads, executando 2 localmente e 2 remotamente. Caso opte por uma operação remota, o cliente envia dois arquivos serializados de matrizes e uma String descrevendo a operação a ser executada (1 ou 2 threads do lado do servidor), todos como json e enviados como ``.ser``, para o servidor e solicita a multiplicação. O servidor processa a solicitação e retorna o resultado, que é concatenado com o processamento local e salvo em um arquivo `.txt`.

## Funcionalidades

O sistema suporta os seguintes modos de operação para multiplicação de matrizes:

1. **Execução Local Monothread**: O cliente executa a multiplicação de matrizes de forma sequencial.
2. **Execução Local Multithread**: A multiplicação é dividida em duas threads e processada em paralelo localmente.
3. **Execução Remota Monothread**: O cliente e o servidor executam a multiplicação de matrizes sequencialmente, com comunicação entre ambos.
4. **Execução Remota Multithread**: A multiplicação é dividida em 4 threads, sendo 2 processadas pelo cliente e 2 pelo servidor, utilizando da paralelização da tarefa para melhorar o desempenho.

## Estrutura do Projeto

O projeto é composto por duas aplicações:

- **Servidor** (`ServMatrixApp`): Recebe requisições de multiplicação de matrizes do cliente, executa a operação solicitada (monothread ou multithread) e envia o resultado de volta para o cliente.
- **Cliente** (`ServMatrixApp`): Faz multiplicações localmente, envia duas matrizes para o servidor, solicita a operação de multiplicação e recebe o resultado a ser concatenado com suas operações locais.

## Requisitos

- Java 8 ou superior.
- Dependências: `com.fasterxml.jackson.core` (para manipulação de JSON), mais detalhes[aqui](https://github.com/FasterXML/jackson).
  
## Como Usar

### 1. Preparação dos Arquivos de Matrizes

O cliente deve ter dois arquivos de matrizes serializados, dentro da pasta ``MultMatrizes`` no formato `.txt`, ambas quadradas.
ex: cada dígito deve ser separado por espaço e só, linhas separadas pela própria quebra de linha e nada mais.
```txt
1.0 1.0 2.0 5.2
0 1.3 0.5 0.3456
0 1 0 1
1.2 3.4 5.6 7.8
````

### 2. Execução do Servidor

O servidor espera por conexões na porta `12345` e pode ser iniciado com o seguinte comando:

```bash
java com.mycompany.servmatrices.ServMatrixApp
```

ou ao utilizar uma IDE, selecione ``"run file"`` ao seleciona-lo na aba ``projects``

### 3. Execução do Cliente
O cliente deve enviar as matrizes ao servidor e aguardar o resultado. Altere os arquivos ``matA`` e ``matB`` para ler os dados que você desejar, seguindo as regras explicitadas no exemplo. Caso executando na IDE de sua preferência, pode-se adicionar matrizes ao diretório e alterar as variáveis ``MATRIZAPATH`` e ``MATRIZBPATH`` Dependendo da operação selecionada (local ou remota, monothread ou multithread), o cliente pode ser executado com:

```bash
java com.mycompany.servmatrices.ServMatrixApp
```
ou ao utilizar uma IDE, selecione ``"run file"`` ao seleciona-lo na aba ``projects``<br>
a aplicação server irá exibir assim que for iniciada seu endereço IPV4, copie o endereço e o copie para a aplicação cliehnt, quando solicitado.

### Modos de Operação:
1. **Execução Local Monothread:**
No modo monothread local, o cliente executa a multiplicação de matrizes de maneira sequencial.

2. **Execução Local Multithread:**
No modo multithread local, a multiplicação é dividida em duas partes, cada uma sendo processada em uma thread separada no cliente.

3. **Execução Remota Monothread:**
O cliente envia metade da matriz A e a matriz B para o servidor e o servidor realiza a multiplicação sequencialmente, retornando o resultado para o cliente, sendo concatenado com  o resultado da multiplicação realizada pelo cliente entre a outra metade de A e B.

4. **Execução Remota Multithread:**
No modo remoto multithread, a multiplicação das matrizes é dividida em 4 partes. O cliente processa duas partes da multiplicação e o servidor processa as outras duas, utilizando múltiplas threads para melhorar o desempenho.

## Detalhes Técnicos
### Conversão de Matrizes para JSON
- ``jsonToMatrix(String json):`` Converte uma string JSON em uma matriz double[][].
- ``matrixToJson(double[][] matrix):`` Converte uma matriz double[][] em uma string JSON.
- ``matricesToJson(double[][]matrixA, double[][]matrixB,  operation):`` payload padrão da requisição do client, contendo duas matrizes e string com a operação solicitada.
- ``closeServer():`` envia string "exit" para o servidor, que finaliza sua execução.
## Comunicação Cliente-Servidor
A comunicação entre o cliente e o servidor é feita via JSON, utilizando os seguintes formatos:

Formato de Requisição:
```json
{
  "operation": "singleThread",
  "matrixA": [[...]],
  "matrixB": [[...]]
}
```
Formato de Resposta:

```json
[[...]] //não é uma estrutura json completa, mas é enviado assim, apenas uma matriz.
```
## Métodos de Multiplicação
- **Multiplicação Monothread:** A multiplicação é realizada de maneira sequencial.
- **Multiplicação Multithread Local:** A matriz A é dividida em duas partes e processada em duas threads no cliente.
- **Multiplicação Remota Monothread:** O cliente envia as matrizes para o servidor, que executa a multiplicação sequencialmente.
- **Multiplicação Remota Multithread:** A multiplicação é dividida em 4 partes, sendo 2 processadas no servidor e 2 no cliente.

## Concorrência
Para o modo multithread, o sistema utiliza duas threads no cliente e/ou no servidor para melhorar a performance ao multiplicar matrizes grandes.

## Arquivo de Resultado
Após a multiplicação, o resultado final é concatenado e escrito em um arquivo ``.txt`` tanto no cliente quanto no servidor.

##Exemplo de Execução
1. **Servidor:** Inicie o servidor:
```bash
java com.mycompany.servmatrices.ServMatrixApp
```
copie seu ip;
2. **Cliente:** Envie as matrizes e solicite a operação:
```bash
java com.mycompany.servmatrices.ServMatrixApp
```
siga as instruções ao executar o programa.
## Conclusão
Este projeto permite a execução de operações de multiplicação de matrizes tanto de forma local (com ou sem threads) quanto remotamente (com ou sem threads), oferecendo flexibilidade e performance para lidar com matrizes grandes.

---
Grato pela atenção!!!
