/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 03/10/2023
* Ultima alteracao.: 05/10/2023
* Nome.............: CamadEnlaceTransmissora.java
* Funcao...........: Eh responsavel pelo enquadramento e pela deteccao de erros na transmissao
*************************************************************** */

package modelo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import controle.PrincipalController;

public class CamadaEnlaceTransmissora {
  Transmissor transmissor;
  public PrincipalController pc;

  public CamadaEnlaceTransmissora(PrincipalController pc) {
    this.pc = pc;
  }

  public void CamadaEnlaceDadosTransmissora(int quadro[], int tipoDeDecodificacao, int tipoEnquadramento,
      int tDeControleDeErro) {
    int tipoDeEnquadramento = pc.getTipoEnquadramento();
    int tCodificacao = pc.getTipoCodificacao();
    int tipoDeControleDeErro = pc.getTipoDeControleDeErro();
    CamadaEnlaceDadosTransmissoraControleDeErro(quadro, tCodificacao, tipoDeEnquadramento);

    // chama proxima camada
    transmissor = new Transmissor(pc);
    transmissor.CamadaFisicaTransmissora(quadro, tCodificacao, tipoDeEnquadramento, tipoDeControleDeErro);
  }

  // fim do metodo CamadaEnlaceDadosTransmissora

  public void CamadaEnlaceDadosTransmissoraEnquadramento(int quadro[]) {
    int tipoDeEnquadramento = pc.getTipoEnquadramento();
    int quadroEnquadrado[];
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
        quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(quadro);
        break;
      case 3: // violacao da camada fisica
        quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        break;
    }// fim do switch/case
  }// fim do metodo CamadaEnlaceTransmissoraEnquadramento

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(int quadro[]) {
    List<Integer> quadrosLista = new ArrayList<>();
    StringBuilder quadroAtual = new StringBuilder("5"); // O cabeçalho inicial indica 5 caracteres no quadro

    int numeroQuadro = 1; // Variavel para numerar os quadros

    for (int i = 0; i < quadro.length; i++) {
      // Adiciona o caractere atual ao quadro
      quadroAtual.append(quadro[i]);

      // Se o quadro atual atingiu o tamanho máximo (5 caracteres, incluindo o
      // cabeçalho), adiciona-o a lista de quadrosLista e reinicia o quadro
      if (quadroAtual.length() >= 5) {
        quadrosLista.add(Integer.parseInt(quadroAtual.toString()));
        // Reinicia o quadro com o cabeçalho para o proximo conjunto de caracteres
        quadroAtual = new StringBuilder("5");
      }
    }

    // Se houver caracteres restantes no ultimo quadro, adiciona-o a lista de
    // quadrosLista
    if (quadroAtual.length() > 1) {
      quadrosLista.add(Integer.parseInt(quadroAtual.toString()));
    }

    // Converte a lista de quadrosLista de volta para um array de inteiros
    int[] quadroEnquadrado = new int[quadrosLista.size()];
    for (int i = 0; i < quadrosLista.size(); i++) {
      quadroEnquadrado[i] = quadrosLista.get(i);
    }

    // Imprime os quadros com numeros
    System.out.println("\n");
    System.out.println("Enquadramento por Contagem de Caracteres");
    for (int quadros : quadroEnquadrado) {
      System.out.println("Quadro " + numeroQuadro + ": " + quadros);
      numeroQuadro++; // Incrementa o numero do quadro
    }
    System.out.println("\n");

    return quadroEnquadrado;
  }

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(int quadro[]) {
    final int flag = 0b01111100; // Flag: 0111 1100 (8 bits)
    final int Escape = 0b00111101; // Escape: 0011 1101 (8 bits)

    List<Integer> quadroEnquadrado = new ArrayList<>();

    // Adiciona a flag no inicio do quadro
    quadroEnquadrado.add(flag);

    for (int i = 0; i < quadro.length; i++) {
      // Adiciona a flag a cada 24 bits (3 bytes) do quadro
      if (i % 24 == 0 && i != 0) {
        quadroEnquadrado.add(flag);
      }

      // Verifica sequencias de 8 números
      if (i + 7 < quadro.length) {
        int sequencia = 0;
        for (int j = 0; j < 8; j++) {
          sequencia = (sequencia << 1) + quadro[i + j];
        }
        // Se a sequencia for igual a flag, adiciona o escape antes dela
        if (sequencia == flag) {
          quadroEnquadrado.add(Escape);
        }
      }
      // Adiciona o bit ao quadro encaixado
      quadroEnquadrado.add(quadro[i]);
    }

    // Adiciona a flag no final do quadro
    quadroEnquadrado.add(flag);

    // Converte a lista de quadros encaixados de volta para um array de inteiros
    int[] quadroEnquadradoFinal = new int[quadroEnquadrado.size()];
    for (int i = 0; i < quadroEnquadrado.size(); i++) {
      quadroEnquadradoFinal[i] = quadroEnquadrado.get(i);
    }

    System.out.println("Enquadramento por Insercao de Bytes");
    for (int bit : quadroEnquadradoFinal) {
      System.out.print(Integer.toBinaryString(bit) + " ");
    }
    System.out.println("\n");

    return quadroEnquadradoFinal;
  }

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(int quadro[]) {
    final int Flag = 0b01111110; // Flag: 0111 1100 (8 bits)
    final int MaxConsecUns = 5; // Número maximo de uns consecutivos antes de inserir um bit 0

    List<Integer> quadroEnquadrado = new ArrayList<>();
    int unsConsecutivos = 0;
    int contador = 0;

    // Adiciona a flag no inicio do quadro
    quadroEnquadrado.add(Flag);

    for (int bit : quadro) {
      contador++;
      if (bit == 1) {
        unsConsecutivos++;
        // Adiciona o bit 1 ao quadro
        quadroEnquadrado.add(bit);
        if (unsConsecutivos == MaxConsecUns) {
          // Adiciona um bit 0 apos 5 uns consecutivos
          quadroEnquadrado.add(0);
          unsConsecutivos = 0;
        }
      } else {
        // Se o bit não for 1, zera o contador de uns consecutivos
        unsConsecutivos = 0;
        // Adiciona o bit 0 ao quadro encaixado
        quadroEnquadrado.add(bit);
      }

      // Realiza o enquadramento a cada 5 bits
      if (contador % 24 == 0) {
        // Adiciona a flag no final do grupo de 5 bits
        quadroEnquadrado.add(Flag);
      }
    }

    // Adiciona a flag no final do quadro
    quadroEnquadrado.add(Flag);

    // Converte a lista de quadros encaixados de volta para um array de inteiros
    int[] quadroEnquadradoFinal = new int[quadroEnquadrado.size()];
    for (int i = 0; i < quadroEnquadrado.size(); i++) {
      quadroEnquadradoFinal[i] = quadroEnquadrado.get(i);
    }

    System.out.println("Enquadramento por Insercao de Bits");
    for (int bit : quadroEnquadradoFinal) {
      System.out.print(Integer.toBinaryString(bit));
    }
    System.out.println("\n");

    return quadroEnquadradoFinal;
  }

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(int quadro[]) {
    // Nao consegui fazer :(
    return quadro;
  }// fim do metodo CamadaEnlaceDadosTransmissoraViolacaoDaCamadaFisica

  public void CamadaEnlaceDadosTransmissoraControleDeErro(int quadro[], int tcodificacao, int tipoDeEnquadramento) {
    int controleDeErro[] = null;
    int tipoDeControleDeErro = pc.getTipoDeControleDeErro(); // alterar de acordo com o teste
    switch (tipoDeControleDeErro) {
      case 0: // bit de paridade par
        controleDeErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(quadro);
        break;
      case 1: // bit de paridade impar
        controleDeErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(quadro);
        break;
      case 2: // CRC
        controleDeErro = CamadaEnlaceDadosTransmissoraControleDeErroCRC(quadro);
        break;
      case 3: // codigo de Hamming
        controleDeErro = CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(quadro);
        break;
    }// fim do switch/case
    CamadaEnlaceDadosTransmissoraEnquadramento(controleDeErro);

  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErro

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(int quadro[]) {
    int totalDeUns = 0;
    // Conta o número total de bits de valor '1' no array de entrada e adiciona o
    // bit de paridade
    for (int i = 0; i < quadro.length; i++) {
      int num = quadro[i];
      while (num > 0) {
        totalDeUns += num % 2;
        num = num / 2;
      }
    }
    // Adiciona o bit de paridade para garantir que o total de bits '1' seja par
    if (totalDeUns % 2 != 0) {
      // Se o número de bits '1' é impar, adiciona '1' como bit de paridade
      int[] quadroPar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroPar, 0, quadro.length);
      quadroPar[quadro.length] = 1;
      // Imprime o array de entrada com o bit de paridade inserido
      System.out.print("Array paridade par: ");
      for (int num : quadroPar) {
        System.out.print(num + " ");
      }
      System.out.println("\n");
      return quadroPar;
    } else {
      // Se o numero de bits '1' é par, adiciona '0' como bit de paridade
      int[] quadroPar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroPar, 0, quadro.length);
      quadroPar[quadro.length] = 0;
      // Imprime o array de entrada com o bit de paridade inserido
      System.out.print("Array paridade par: ");
      for (int num : quadroPar) {
        System.out.print(num + " ");
      }
      return quadroPar;
    }

  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadePar

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(int quadro[]) {
    int totalDeUns = 0;

    // Conta o numero total de bits de valor '1' no array de entrada
    for (int i = 0; i < quadro.length; i++) {
      int num = quadro[i];
      while (num > 0) {
        totalDeUns += num % 2;
        num = num / 2;
      }
    }

    // Adiciona o bit de paridade para garantir que o total de bits '1' seja impar
    if (totalDeUns % 2 == 0) {
      // Se o número de bits '1' é par, adiciona '1' como bit de paridade
      int[] quadroImpar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroImpar, 0, quadro.length);
      quadroImpar[quadro.length] = 1;

      // Imprime o array de entrada com o bit de paridade inserido
      System.out.print("Array de entrada com bit de paridade ímpar: ");
      for (int num : quadroImpar) {
        System.out.print(num + " ");
      }

      return quadroImpar;
    } else {
      // Se o numero de bits '1' eh impar, adiciona '0' como bit de paridade
      int[] quadroImpar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroImpar, 0, quadro.length);
      quadroImpar[quadro.length] = 0;

      // Imprime o array de entrada com o bit de paridade inserido
      System.out.print("Array de entrada com bit de paridade ímpar: ");
      for (int num : quadroImpar) {
        System.out.print(num + " ");
      }

      return quadroImpar;
    }
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadeImpar

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroCRC(int quadro[]) {
    String polinomio = "10000010011000001000111011011011";
    StringBuilder stringBinaria = new StringBuilder();
    for (int bit : quadro) {
      stringBinaria.append(bit);
    }

    StringBuilder entradaComZeros = new StringBuilder(stringBinaria);
    while (entradaComZeros.length() < polinomio.length() - 1) {
      entradaComZeros.insert(0, '0');
    }

    BigInteger Tx = new BigInteger(entradaComZeros.toString(), 2);
    BigInteger Cx = new BigInteger(polinomio, 2);

    Tx = Tx.shiftLeft(polinomio.length() - 1);
    Cx = Cx.shiftLeft(entradaComZeros.length() - polinomio.length());

    int shiftBit;
    while (Tx.toString(2).length() > polinomio.length() - 1) {
      Tx = Tx.xor(Cx);
      shiftBit = Cx.toString(2).length() - Tx.toString(2).length();
      Cx = Cx.shiftRight(shiftBit);
    }

    String crcBinario = Tx.toString(2);
    int[] crcArray = new int[polinomio.length() - 1];
    for (int i = 0; i < crcBinario.length(); i++) {
      crcArray[i] = Character.getNumericValue(crcBinario.charAt(i));
    }

    int[] crcfinal = new int[quadro.length + crcArray.length];
    System.arraycopy(quadro, 0, crcfinal, 0, quadro.length);
    System.arraycopy(crcArray, 0, crcfinal, quadro.length, crcArray.length);

    System.out.print(" CRC: ");
    for (int bit : crcfinal) {
      System.out.print(bit);
    }

    return crcfinal;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroCRC

  public int[] CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(int quadro[]) {
    int entradaLength = quadro.length;
    int numBlocos = entradaLength / 4;
    int outputLength = numBlocos * 7;
    int[] codigoDeHamming = new int[outputLength];

    for (int i = 0; i < numBlocos; i++) {
      int[] bloco = new int[4];
      System.arraycopy(quadro, i * 4, bloco, 0, 4);
      codigoDeHamming[i * 7 + 2] = bloco[0];
      codigoDeHamming[i * 7 + 4] = bloco[1];
      codigoDeHamming[i * 7 + 5] = bloco[2];
      codigoDeHamming[i * 7 + 6] = bloco[3];

      // Calculo dos bits de paridade
      codigoDeHamming[i * 7] = codigoDeHamming[i * 7 + 2] ^ codigoDeHamming[i * 7 + 4] ^ codigoDeHamming[i * 7 + 6];
      codigoDeHamming[i * 7 + 1] = codigoDeHamming[i * 7 + 2] ^ codigoDeHamming[i * 7 + 5] ^ codigoDeHamming[i * 7 + 6];
      codigoDeHamming[i * 7 + 3] = codigoDeHamming[i * 7 + 4] ^ codigoDeHamming[i * 7 + 5] ^ codigoDeHamming[i * 7 + 6];
    }

    // Exibir os dados codificados
    System.out.print("Dados codificados: ");
    for (int bit : codigoDeHamming) {
      System.out.print(bit);
    }

    return codigoDeHamming;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErroCodigoDehamming

}
