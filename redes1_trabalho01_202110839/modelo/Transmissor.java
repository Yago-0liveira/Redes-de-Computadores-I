/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 29/08/2023
* Ultima alteracao.: 09/09/2023
* Nome.............: Transmissor.java
* Funcao...........: Eh responsavel pela transformacao dos caracteres em codigo e pelo envio para a comunicacao.  
*************************************************************** */

package modelo;

import java.util.Arrays;

import controle.PrincipalController;
import javafx.scene.layout.HBox;

public class Transmissor {

  public PrincipalController pc;
  Comunicacao comunic;

  public Transmissor(Receptor receptor) { // construtor
    this.comunic = new Comunicacao(receptor);
  }

  public void CamadaDeAplicacaoTransmissora(String mensagem) { // O metodo define o texto ao escolher uma codificacao
    int tCodificacao = pc.getTipoCodificacao();

    StringBuilder asciiString = new StringBuilder(); // int[] mensagemAscii = converteAscii(mensagem);
    int[] valoresAscii = converteAscii(mensagem);// Converte a palavra em valores ASCI
    for (int asciii : valoresAscii) {
      asciiString.append(asciii).append(" ");

    }
    pc.ascii.setText(asciiString.toString());
    String mensagemBinaria = converteBinario(mensagem);

    // Preencha o array quadro com os valores binarios
    int[] quadro = new int[mensagemBinaria.length()];
    for (int i = 0; i < mensagemBinaria.length(); i++) {
      quadro[i] = Integer.parseInt(String.valueOf(mensagemBinaria.charAt(i)));
    }

    CamadaFisicaTransmissora(quadro, tCodificacao);
  }

  public void CamadaFisicaTransmissora(int quadro[], int tCodificacao) { // o metodo transmite a mensagem para a
                                                                         // codificacao selecionada
    int fluxoBrutoDeBits[] = quadro;
    switch (tCodificacao) {
      case 0: {
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoBinaria(quadro);
        break;
      }
      case 1: {
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchester(quadro);
        break;
      }
      case 2: {
        fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
        break;
      }
    }
    comunic.MeioDeComunicacao(fluxoBrutoDeBits);

  }

  public int[] CamadaFisicaTransmissoraCodificacaoBinaria(int quadro[]) { // o metodo Codifica o fluxo de bits em
                                                                          // binario
    HBox box = pc.getBoxGrafico();
    StringBuilder asciiString = new StringBuilder();
    for (int z = 0; z < quadro.length; z++) {
    }

    pc.show(quadro, box);
    return quadro;
  }

  public int[] CamadaFisicaTransmissoraCodificacaoManchester(int quadro[]) { // o metodo Codifica o fluxo de bits em
                                                                             // manchester
    HBox box = pc.getBoxGrafico();
    int[] manchester = new int[quadro.length * 2];

    int manchesterIndex = 0;
    int ultimoBit = 0; // Inicializa com 0 para nao ter transicao no primeiro bit

    for (int bit : quadro) {
      if (bit == 0) {
        // Transicao de 0 para 1 no codigo Manchester
        manchester[manchesterIndex++] = 0;
        manchester[manchesterIndex++] = 1;
      } else if (bit == 1) {
        // Transicao de 1 para 0 no codigo Manchester
        manchester[manchesterIndex++] = 1;
        manchester[manchesterIndex++] = 0;
      } else {
        // Caso o valor binario contenha valores diferentes de 0 ou 1
        System.err.println("Valor binario invalido: " + bit);
        return new int[0]; // Retorna um array vazio em caso de erro
      }

      ultimoBit = bit;
    }

    pc.show(manchester, box);
    return manchester;
  }

  public int[] CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int quadro[]) { // o metodo Codifica o fluxo de
                                                                                        // bits em manchester
                                                                                        // diferencial
    HBox box = pc.getBoxGrafico();

    int[] codificacaoManchesterDiferencial = new int[quadro.length * 2];
    for (int i = 0, j = 0; i < quadro.length; i++) {
      if (i == 0) {
        // Caso inicial, baixo-alto
        if (quadro[i] == 0) {
          codificacaoManchesterDiferencial[j] = 0;
          codificacaoManchesterDiferencial[j + 1] = 1;
        } else {
          codificacaoManchesterDiferencial[j] = 1;
          codificacaoManchesterDiferencial[j + 1] = 0;
        }
      } else {
        if (quadro[i] == quadro[i - 1]) {
          codificacaoManchesterDiferencial[j] = codificacaoManchesterDiferencial[j - 1];
          codificacaoManchesterDiferencial[j + 1] = codificacaoManchesterDiferencial[j - 2];
        } else {
          codificacaoManchesterDiferencial[j] = codificacaoManchesterDiferencial[j - 2];
          codificacaoManchesterDiferencial[j + 1] = codificacaoManchesterDiferencial[j - 1];
        }
      }
      j += 2;
    }

    pc.show(codificacaoManchesterDiferencial, box);
    return codificacaoManchesterDiferencial;
  }

  public static int[] converteAscii(String palavra) { // metodo responsavel pela conversao em ascii
    int[] ValoresAscii = new int[palavra.length()];
    for (int i = 0; i < palavra.length(); i++) {
      ValoresAscii[i] = (int) palavra.charAt(i);
    }
    return ValoresAscii;
  }

  public static String converteBinario(String palavra) { // metodo responsavel pela conversao ascii em binario
    int[] valoresAscii = new int[palavra.length()];

    for (int i = 0; i < palavra.length(); i++) {
      valoresAscii[i] = (int) palavra.charAt(i);
    }

    StringBuilder binarioStringBuilder = new StringBuilder();

    for (int i = 0; i < valoresAscii.length; i++) {
      int valorAscii = valoresAscii[i];

      for (int j = 7; j >= 0; j--) { // 8 bits em um valor ASCII
        int bit = (valorAscii >> j) & 1;
        binarioStringBuilder.append(bit);
      }
    }

    return binarioStringBuilder.toString();
  }

}
