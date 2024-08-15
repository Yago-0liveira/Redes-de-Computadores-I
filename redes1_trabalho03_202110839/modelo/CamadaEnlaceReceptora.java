/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 03/11/2023
* Ultima alteracao.: 05/10/2023
* Nome.............: CamadEnlaceReceptora.java
* Funcao...........: Eh responsavel pelo enquadramento e pela deteccao de erros na recepcao
*************************************************************** */
package modelo;

import java.util.ArrayList;
import java.util.List;

import controle.PrincipalController;

public class CamadaEnlaceReceptora {
  Receptor receptor;
  public PrincipalController pc;

  public CamadaEnlaceReceptora(PrincipalController pc) {
    this.pc = pc;
  }

  void CamadaEnlaceDadosReceptora(int quadro[]) {
    int tipoDeEnquadramento = pc.getTipoEnquadramento();
    int tCodificacao = pc.getTipoCodificacao();
    int tipoDeControleDeErro = pc.getTipoDeControleDeErro();
    // chama proxima camada
    CamadaEnlaceDadosReceptoraEnquadramento(quadro, tCodificacao, tipoDeEnquadramento, tipoDeControleDeErro);

    receptor = new Receptor(pc);
    receptor.CamadaDeAplicacaoReceptora(quadro);
  }

  void CamadaEnlaceDadosReceptoraEnquadramento(int quadro[], int tcodificacao, int tipoDeEnquadramento,
      int tipoDeControleDeErro) {
    int quadroDesenquadrado[] = null;
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
        // System.out.println("ta aq");
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
        // System.out.println("ta aq2");
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
        // System.out.println("ta aq3");
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBits(quadro);
      case 3: // violacao da camada fisica
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        break;
    }// fim do switch/case
    CamadaEnlaceDadosReceptoraControleDeErro(quadroDesenquadrado);
  }// fim do metodo CamadaEnlaceDadosReceptoraEnquadramento

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres(int quadro[]) {
    StringBuilder quadrosEnquadrados = new StringBuilder();

    // Concatena os quadros em uma unica string
    for (int quadros : quadro) {
      quadrosEnquadrados.append(String.format("%05d", quadros));
    }

    // Desenquadra os quadros
    List<Integer> quadrosDesenquadrados = new ArrayList<>();
    for (int i = 0; i < quadrosEnquadrados.length(); i += 5) {
      if (i + 5 <= quadrosEnquadrados.length()) {
        int quadroAtual = Integer.parseInt(quadrosEnquadrados.substring(i + 1, i + 5));
        quadrosDesenquadrados.add(quadroAtual);
      }
    }

    // Converte a lista de quadros de volta para um array de
    // inteiros
    int[] quadroOriginal = new int[quadrosDesenquadrados.size()];
    for (int i = 0; i < quadrosDesenquadrados.size(); i++) {
      quadroOriginal[i] = quadrosDesenquadrados.get(i);
    }

    int[] quadrosDesen = quadroOriginal;
    System.out.print("Desenquadramento da Contagem de caracteres: ");
    // Imprime os quadros
    for (int quadross : quadrosDesenquadrados) {
      System.out.print(quadross);
    }
    System.out.println("\n");

    return quadroOriginal;
  }

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBytes(int quadro[]) {
    final int FLAG = 0b01111100; // Flag: 0111 1100 (8 bits)
    final int ESCAPE = 0b00111101; // Escape: 0011 1101 (8 bits)

    List<Integer> quadroDesenquadrado = new ArrayList<>();
    boolean escapeFlag = false;
    boolean flagEncontrada = false;

    for (int i = 0; i < quadro.length; i++) {
      int elemento = quadro[i];

      if (elemento == FLAG && !escapeFlag) {
        if (flagEncontrada) {
          // Encontrou a segunda flag, finaliza o desenquadramento
          break;
        }
        flagEncontrada = true;
      } else if (elemento == ESCAPE && !escapeFlag) {
        escapeFlag = true;
      } else {
        // Se nao for flag ou escape, adiciona ao quadro desenquadrado
        quadroDesenquadrado.add(elemento);
        escapeFlag = false;
      }
    }

    // Converte a lista de quadros desenquadrado de volta para um array de inteiros
    int[] quadroDesenquadradoFinal = new int[quadroDesenquadrado.size()];
    for (int i = 0; i < quadroDesenquadrado.size(); i++) {
      quadroDesenquadradoFinal[i] = quadroDesenquadrado.get(i);
    }

    System.out.println("\nDesenquadramento da insercao bytes:");
    for (int quadros : quadroDesenquadradoFinal) {
      System.out.print(Integer.toBinaryString(quadros) + " ");
    }
    System.out.println("\n");

    return quadroDesenquadradoFinal;
  }

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBits(int quadro[]) {
    final int FLAG = 0b01111110; // Flag: 0111 1110 (8 bits)
    final int MAX_CONSEC_UNS = 5; // Número maximo de uns consecutivos antes de inserir um bit 0

    List<Integer> quadroDesenquadrado = new ArrayList<>();
    int unsConsecutivos = 0;
    boolean flagEncontrada = false;

    for (int elemento : quadro) {
      if (elemento == FLAG && !flagEncontrada) {
        flagEncontrada = true;
        continue;
      }

      for (int j = 7; j >= 0; j--) {
        int bit = (elemento >> j) & 1;
        if (bit == 1) {
          unsConsecutivos++;
          quadroDesenquadrado.add(bit);
          if (unsConsecutivos == MAX_CONSEC_UNS) {
            // Ignora o proximo bit zero e continua a leitura
            j--;
            unsConsecutivos = 0;
          }
        } else {
          quadroDesenquadrado.add(bit);
          unsConsecutivos = 0;
        }
      }
    }

    // Converte a lista de bits desenquadrados de volta para um array de inteiros
    int[] quadroDesenquadradoFinal = new int[quadroDesenquadrado.size()];
    for (int i = 0; i < quadroDesenquadrado.size(); i++) {
      quadroDesenquadradoFinal[i] = quadroDesenquadrado.get(i);
    }

    // Impressao do quadro desenquadrado
    System.out.println("\nQuadro desenquadrado pela insercao de bits:");
    for (int quadros : quadroDesenquadradoFinal) {
      System.out.print(Integer.toBinaryString(quadros) + " ");
    }
    System.out.println("\n");

    return quadroDesenquadradoFinal;
  }

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoViolacaoDaCamadaFisica(int quadro[]) {
    // implementacao do algoritmo para DESENQUADRAR
    return quadro;
  }// fim do metodo CamadaEnlaceDadosReceptoraViolacaoDaCamadaFisica

  public void CamadaEnlaceDadosReceptoraControleDeErro(int quadro[]) {
    int controleDeErro[] = null;
    int tipoDeControleDeErro = pc.getTipoDeControleDeErro(); // alterar de acordo com o teste
    switch (tipoDeControleDeErro) {
      case 0: // bit de paridade par
        controleDeErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(quadro);
        break;
      case 1: // bit de paridade impar
        controleDeErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(quadro);
        break;
      case 2: // CRC
        break;
      case 3: // codigo de hamming
        controleDeErro = CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(quadro);
        break;
    }// fim do switch/case
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErro

  public int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(int quadro[]) {
    int totalDeUns = 0;

    // Calcula o numero total de bits '1' no array de entrada (excluindo o bit de
    // paridade)
    for (int i = 0; i < quadro.length; i++) {
      totalDeUns += Integer.bitCount(quadro[i]);
    }

    // Verifica se a quantidade de bits '1' eh par (excluindo o bit de paridade)

    if (totalDeUns % 2 == 0) {

      // Remove o bit de paridade (último bit)
      quadro[quadro.length - 1] = quadro[quadro.length - 1] >> 1;
      System.out.print("Array (era paridade par): ");
      for (int num : quadro) {
        System.out.print(num + " ");
      }
      return quadro;
    } else {
      System.out.print("erro: ");
      for (int num : quadro) {
        System.out.print(num + " ");
      }
    }

    // Retorna o array original se houver erro de paridade
    return quadro;
  }

  public int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(int quadro[]) {
    int totalDeUns = 0;

    // Calcula o número total de bits '1' no array de entrada (excluindo o bit de
    // paridade)
    for (int i = 0; i < quadro.length; i++) {
      totalDeUns += Integer.bitCount(quadro[i]);
    }

    // Verifica se a quantidade de bits '1' eh par (excluindo o bit de paridade)
    if (totalDeUns % 2 != 0) {

      // Remove o bit de paridade (último bit)
      quadro[quadro.length - 1] = quadro[quadro.length - 1] >> 1;
      System.out.print("Array (era paridade impar): ");
      for (int num : quadro) {
        System.out.print(num + " ");
      }
      return quadro;
    } else {
      System.out.print("erro: ");
      for (int num : quadro) {
        System.out.print(num + " ");
      }
    }

    // Retorna o array original se houver erro de paridade
    return quadro;

  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar

  public void CamadaEnlaceDadosReceptoraControleDeErroCRC(int quadro[]) {
    // implementacao do algoritmo para VERIFICAR SE HOUVE ERRO
    // usar polinomio CRC-32(IEEE 802)
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroCRC

  public int[] CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(int quadro[]) {
    int numBlocos = quadro.length / 7;
    int[] quadroFinal = new int[numBlocos * 4];

    for (int i = 0; i < numBlocos; i++) {
      int[] blocoRecebido = new int[7];
      System.arraycopy(quadro, i * 7, blocoRecebido, 0, 7);

      // Cálculo dos bits de paridade recebidos
      int[] calculaParidade = new int[] {
          blocoRecebido[2], blocoRecebido[4], blocoRecebido[5], blocoRecebido[6] };

      // Verificação de erros
      boolean deteccaoDeErro = false;
      for (int j = 0; j < 3; j++) {
        if (calculaParidade[j] != blocoRecebido[j]) {
          deteccaoDeErro = true;
          break;
        }
      }

      // Correcao de erro (se possivel) e armazenamento dos dados decodificados
      if (deteccaoDeErro) {
        // Correcao de erro: inverte o bit errado
        blocoRecebido[3] = 1 - blocoRecebido[3];
      }

      // Armazenamento dos dados decodificados
      quadroFinal[i * 4] = blocoRecebido[2];
      quadroFinal[i * 4 + 1] = blocoRecebido[4];
      quadroFinal[i * 4 + 2] = blocoRecebido[5];
      quadroFinal[i * 4 + 3] = blocoRecebido[6];
    }

    // Exibir os dados decodificados
    System.out.print("Dados decodificados: ");
    for (int bit : quadroFinal) {
      System.out.print(bit);
    }

    return quadroFinal;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming

}
