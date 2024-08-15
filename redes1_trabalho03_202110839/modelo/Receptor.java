/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 29/08/2023
* Ultima alteracao.: 10/09/2023
* Nome.............: Transmissor.java
* Funcao...........: Eh responsavel por receber os dados advindos da transmissao e decodifica-los para que a mensagem possa
ser vista pelo usuario
*************************************************************** */
package modelo;

import controle.PrincipalController;

public class Receptor {

  public PrincipalController pc;
  Transmissor transmissor;
  CamadaEnlaceReceptora enlaceReceptora;

  public Receptor(PrincipalController pc) {
    this.pc = pc;
  }

  public void CamadaFisicaReceptora(int quadro[], int tCodificacao, int tEnquadramento, int tipoDeControleDeErro) { // Chamar
                                                                                                                    // //
                                                                                                                    // especifica
    int tipoDeDecodificacao = pc.getTipoCodificacao();
    int fluxoBrutoDeBits[];
    switch (tipoDeDecodificacao) {
      case 0: {// Decodificao binaria
        fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoBinaria(quadro);
        break;
      }
      case 1: {// Decodificacao manchester
        fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchester(quadro);
        break;
      }
      case 2: {// Decodificacao manchester diferencial
        fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro);
        break;
      }
      default: {
        fluxoBrutoDeBits = null;
      }

    }
    enlaceReceptora = new CamadaEnlaceReceptora(pc);
    enlaceReceptora.CamadaEnlaceDadosReceptora(fluxoBrutoDeBits);
    // CamadaDeAplicacaoReceptora(fluxoBrutoDeBits);
  }

  public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int quadro[]) {// metodo responsavel por decodificar o
                                                                        // fluxoDeBits em binario
    StringBuilder binarioString = new StringBuilder();
    for (int i = 0; i < quadro.length; i++) {
      binarioString.append(quadro[i]);
      if ((i + 1) % 8 == 0) { // Adiciona uma nova linha apos cada byte (cada 8 bits)
        binarioString.append(" ");
      }
    }
    System.out.print("\nBinario: ");
    pc.binario.setText(binarioString.toString());
    System.out.println(binarioString.toString());
    return quadro;
  }

  public int[] CamadaFisicaReceptoraDecodificacaoManchester(int quadro[]) { // metodo responsavel por decodificar o
                                                                            // fluxoDeBits manchester em binario
    int[] resultadoBinario = new int[quadro.length / 2];
    for (int i = 0; i < quadro.length; i += 2) {
      if (quadro[i] == 0 && quadro[i + 1] == 1) {
        resultadoBinario[i / 2] = 0;
      } else if (quadro[i] == 1 && quadro[i + 1] == 0) {
        resultadoBinario[i / 2] = 1;
      } else {
        throw new IllegalArgumentException("Sinal Manchester invalido.");
      }
    }
    // Converte os valores de manchester em uma string
    StringBuilder manchesterString = new StringBuilder();
    for (int valor : quadro) {
      manchesterString.append(valor);
    }
    System.out.print("\nManchester: ");
    pc.binario.setText(manchesterString.toString());
    System.out.println(manchesterString.toString());
    return resultadoBinario;
  }

  public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int quadro[]) { // metodo responsavel por
                                                                                       // decodificar o fluxoDeBits
                                                                                       // manchester dif em binario
    int[] resultadoBinario = new int[quadro.length / 2];
    for (int i = 0, j = 0; i < quadro.length; i += 2) {
      if (i == 0) {
        if (quadro[i] == 0 && quadro[i + 1] == 1) {
          resultadoBinario[0] = 0;
        }
        if (quadro[i] == 1 && quadro[i + 1] == 0) {
          resultadoBinario[0] = 1;
        }
      } else {
        if (quadro[i] == quadro[i - 1]) {
          resultadoBinario[j] = resultadoBinario[j - 1];
        } else {
          if (resultadoBinario[j - 1] == 1) {
            resultadoBinario[j] = 0;
          } else {
            resultadoBinario[j] = 1;
          }
        }
      }
      j++;
    }

    // Criar uma string formatada com os valores do array
    StringBuilder manchesterDifString = new StringBuilder();
    for (int valor : quadro) {
      manchesterDifString.append(valor);
    }
    System.out.print("\nManchester Diferencial: ");
    pc.binario.setText(manchesterDifString.toString());
    System.out.println(manchesterDifString.toString());
    return resultadoBinario;
  }

  public void CamadaDeAplicacaoReceptora(int quadro[]) {// metodo responsavel por pegar o fluxoDeBits e transformar em
                                                        // String
    int[] transfDecimal = binarioParaDecimal(quadro);
    String decimal = decimaisParaAscii(transfDecimal);
    AplicacaoReceptora(decimal);
  }

  public void AplicacaoReceptora(String mensagem) { // metodo responsavel por exibir a mensagem descodificada na tela
    pc.receptorText.setText(mensagem);
  }

  public static int[] binarioParaDecimal(int[] nBinario) { // metodo responsavel por transformar o numero binario em
    // decimal
    int tamanhoSegmento = 8; // Tamanho de cada segmento em bits
    int numeroDeSegmentos = nBinario.length / tamanhoSegmento;

    int[] numerosDecimais = new int[numeroDeSegmentos];

    for (int i = 0; i < numeroDeSegmentos; i++) {
      StringBuilder segmentoBinario = new StringBuilder();

      for (int j = 0; j < tamanhoSegmento; j++) {
        int indice = i * tamanhoSegmento + j;
        segmentoBinario.append(nBinario[indice]);
      }

      // Converte o segmento binario em um numero decimal e armazena no array
      numerosDecimais[i] = Integer.parseInt(segmentoBinario.toString(), 2);
    }

    return numerosDecimais;
  }

  public static String decimaisParaAscii(int[] decimais) { // metodo responsavel por transformar numeros decimais em
                                                           // caracteres correspondentes na tabela ascii
    StringBuilder resultado = new StringBuilder();

    for (int decimal : decimais) {
      if (decimal >= 0 && decimal <= 255) {
        resultado.append((char) decimal);
      } else {
        // Tratar valores fora do intervalo ASCII (0-255) como caracteres especiais
        resultado.append((char) (decimal & 0xFF));
      }
    }

    return resultado.toString();
  }

}
