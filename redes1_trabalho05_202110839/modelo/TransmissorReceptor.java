/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 28/11/2023
* Ultima alteracao.: 06/12/2023
* Nome.............: Transmissor.java
* Funcao...........: Eh responsavel por toda a codificacao da transmissao e da recepcao dos quadros. Todos os metodos de envio/recepcao,
enquadramento/desenquadramento, controle de erro e controle de acesso ao meio
*************************************************************** */

package modelo;

import java.util.Arrays;

import controle.PrincipalController;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransmissorReceptor extends Thread {

  public PrincipalController pc;
  Comunicacao comunic;
  int id;
  ImageView transmitindo;
  ImageView colisao;
  boolean laco = false;
  boolean bateu = false;
  boolean bateu2 = true;
  int p;
  boolean testeMeio = true; 

  // Metodo para exibir frases automaticamente
  public static String exibirFrasesAutomaticamente() {
    String[] frases = {
        "oi",
        "tudo bem?",
        "ja fiz o almoco",
        "que horas voce vem?"
    };

    // Escolhe uma frase aleatoria
    String fraseEscolhida = escolherFraseAleatoria(frases);

    System.out.println("Frase Escolhida: " + fraseEscolhida);

    return fraseEscolhida;

  }

  private static int gerarTempoAleatorio() {
    Random random = new Random();
    int tempoMinimo = 0; // Tempo minimo em milissegundos
    int tempoMaximo = 5; // Tempo maximo em milissegundos

    // Gera um valor aleatorio entre tempoMinimo e tempoMaximo
    int tempoAleatorio = random.nextInt(tempoMaximo - tempoMinimo + 1) + tempoMinimo;

    return tempoAleatorio;
  }

  // Metodo para escolher uma frase aleatoria a partir de um array
  private static String escolherFraseAleatoria(String[] frases) {
    Random random = new Random();
    int indiceAleatorio = random.nextInt(frases.length);
    return frases[indiceAleatorio];
  }

  public void run() {
    CamadaDeAplicacaoTransmissora(exibirFrasesAutomaticamente());
  }

  public TransmissorReceptor(PrincipalController pc, int id, ImageView transmitindo, ImageView bateu) { // construtor
    this.pc = pc;
    this.id = id;
    this.transmitindo = transmitindo;
    this.colisao = bateu;
  }

  public void CamadaDeAplicacaoTransmissora(String mensagem) { // O metodo define o texto ao escolher uma codificacao
    int tCodificacao = pc.getTipoCodificacao();
    int tipoDeEnquadramento = pc.getTipoEnquadramento();
    int tipoDeControleDeErro = pc.getTipoDeControleDeErro();

    StringBuilder asciiString = new StringBuilder(); // int[] mensagemAscii = converteAscii(mensagem);
    int[] valoresAscii = converteAscii(mensagem);// Converte a palavra em valores ASCI
    for (int asciii : valoresAscii) {
      asciiString.append(asciii).append(" ");

    }
    String mensagemBinaria = converteBinario(mensagem);

    // Preencha o array quadro com os valores binarios
    int[] quadro = new int[mensagemBinaria.length()];
    for (int i = 0; i < mensagemBinaria.length(); i++) {
      quadro[i] = Integer.parseInt(String.valueOf(mensagemBinaria.charAt(i)));
    }

    CamadaEnlaceDadosTransmissora(quadro, tCodificacao, tipoDeEnquadramento, tipoDeControleDeErro);

  }

  public void CamadaFisicaTransmissora(int quadro[]) { // o metodo transmite a mensagem para a codificacao selecionada
    int fluxoBrutoDeBits[] = quadro;
    int tCodificacao = pc.getTipoCodificacao();
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
    comunic = new Comunicacao(pc, colisao);
     if(pc.getTipoDeMeio() == 5){
      comunic.MeioDeComunicacao2(fluxoBrutoDeBits, this);
    }
    else{
    comunic.MeioDeComunicacao(fluxoBrutoDeBits, this);
    }
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

  /************************************************************************************************* */
  public void CamadaEnlaceDadosTransmissora(int quadro[], int tipoDeDecodificacao, int tipoEnquadramento,
      int tDeControleDeErro) {
    int tipoDeEnquadramento = pc.getTipoEnquadramento();
    int tCodificacao = pc.getTipoCodificacao();
    int tipoDeControleDeErro = pc.getTipoDeControleDeErro();
    CamadaEnlaceDadosTransmissoraControleDeErro(quadro, tCodificacao, tipoDeEnquadramento);

    // chama proxima camada
    CamadaAcessoAoMeioTransmissora(quadro);

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
    StringBuilder quadroAtual = new StringBuilder("5"); // O cabecalho inicial indica 5 caracteres no quadro

    int numeroQuadro = 1; // Variavel para numerar os quadros

    for (int i = 0; i < quadro.length; i++) {
      // Adiciona o caractere atual ao quadro
      quadroAtual.append(quadro[i]);

      // Se o quadro atual atingiu o tamanho maximo (5 caracteres, incluindo o
      // cabecalho), adiciona-o a lista de quadrosLista e reinicia o quadro
      if (quadroAtual.length() >= 5) {
        quadrosLista.add(Integer.parseInt(quadroAtual.toString()));
        // Reinicia o quadro com o cabecalho para o proximo conjunto de caracteres
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
    // System.out.println("\n");
    // System.out.println("Enquadramento por Contagem de Caracteres");
    for (int quadros : quadroEnquadrado) {
      // System.out.println("Quadro " + numeroQuadro + ": " + quadros);
      numeroQuadro++; // Incrementa o numero do quadro
    }
    // System.out.println("\n");

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

      // Verifica sequencias de 8 numeros
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

    // System.out.println("Enquadramento por Insercao de Bytes");
    for (int bit : quadroEnquadradoFinal) {
      // System.out.print(Integer.toBinaryString(bit) + " ");
    }
    // System.out.println("\n");

    return quadroEnquadradoFinal;
  }

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(int quadro[]) {
    final int Flag = 0b01111110; // Flag: 0111 1100 (8 bits)
    final int MaxConsecUns = 5; // Numero maximo de uns consecutivos antes de inserir um bit 0

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
        // Se o bit nao for 1, zera o contador de uns consecutivos
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

    // System.out.println("Enquadramento por Insercao de Bits");
    for (int bit : quadroEnquadradoFinal) {
      // System.out.print(Integer.toBinaryString(bit));
    }
    // System.out.println("\n");

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
    // Conta o numero total de bits de valor '1' no array de entrada e adiciona o
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
      // Se o numero de bits '1' eh impar, adiciona '1' como bit de paridade
      int[] quadroPar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroPar, 0, quadro.length);
      quadroPar[quadro.length] = 1;
      // Imprime o array de entrada com o bit de paridade inserido
      // System.out.print("Array paridade par: ");
      for (int num : quadroPar) {
        // System.out.print(num + " ");
      }
      // System.out.println("\n");
      return quadroPar;
    } else {
      // Se o numero de bits '1' eh par, adiciona '0' como bit de paridade
      int[] quadroPar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroPar, 0, quadro.length);
      quadroPar[quadro.length] = 0;
      // Imprime o array de entrada com o bit de paridade inserido
      // System.out.print("Array paridade par: ");
      for (int num : quadroPar) {
        // System.out.print(num + " ");
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
      // Se o numero de bits '1' eh par, adiciona '1' como bit de paridade
      int[] quadroImpar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroImpar, 0, quadro.length);
      quadroImpar[quadro.length] = 1;

      // Imprime o array de entrada com o bit de paridade inserido
      // System.out.print("Array de entrada com bit de paridade impar: ");
      for (int num : quadroImpar) {
        // System.out.print(num + " ");
      }

      return quadroImpar;
    } else {
      // Se o numero de bits '1' eh impar, adiciona '0' como bit de paridade
      int[] quadroImpar = new int[quadro.length + 1];
      System.arraycopy(quadro, 0, quadroImpar, 0, quadro.length);
      quadroImpar[quadro.length] = 0;

      // Imprime o array de entrada com o bit de paridade inserido
      // System.out.print("Array de entrada com bit de paridade impar: ");
      for (int num : quadroImpar) {
        // System.out.print(num + " ");
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

    // System.out.print(" CRC: ");
    for (int bit : crcfinal) {
      // System.out.print(bit);
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
    // System.out.print("Dados codificados: ");
    for (int bit : codigoDeHamming) {
      // System.out.print(bit);
    }

    return codigoDeHamming;
  }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErroCodigoDehamming

  public void CamadaAcessoAoMeioTransmissora(int quadro[]) {
    int acessoAoMeio[] = null;
    int tipodeAcesso = pc.getTipoDeMeio();
    switch (tipodeAcesso) {
      case 0: // bit de paridade par
        acessoAoMeio = CamadaAcessoAoMeioTransmissoraAlohaPuro(quadro);
        break;
      case 1: // bit de paridade impar
        acessoAoMeio = CamadaAcessoAoMeioTransmissoraSlottedAloha(quadro);
        break;
      case 2: // CRC
        acessoAoMeio = CamadaAcessoAoMeioTransmissoraCsmaNaoPersistente(quadro);
        break;
      case 3: // codigo de Hamming
        acessoAoMeio = CamadaAcessoAoMeioTransmissoraCsmaPersistente(quadro);
        break;
      case 4: // codigo de Hamming
        acessoAoMeio = CamadaDeAcessoAoMeioTransmissoraCsmaPPersistente(quadro);
        break;
      case 5: // codigo de Hamming
        acessoAoMeio = CamadaDeAcessoAoMeioTransmissoraCsmaCD(quadro);
        break;
    }

  }// fim do metodo CamadaAcessoAoMeioTransmisora

  public int[] CamadaAcessoAoMeioTransmissoraAlohaPuro(int quadro[]) {

    Random radom = new Random();
    new Thread(() -> {
      System.out.println(" transmitindo o transmissor: " + getId());
      setTransmitindotrue();
      CamadaFisicaTransmissora(quadro);
      try {
        sleep(1000);

      } catch (InterruptedException e) {
      }
      System.out.println("passou sleep " + getId());
      while (!laco) { // Entrar aqui se n chegar
        System.out.println("entrou no laco " + id);
        try {
          int tempoDeEspera = 1500 + radom.nextInt(1501);
          System.out.println("tempo de espera aqui " + tempoDeEspera + "id " + getId());
          sleep(tempoDeEspera); // Espera um tempo aleatorio
        } catch (InterruptedException e) {
        }
        System.out.println("nao chegou, retransmissao: " + getId());
        CamadaFisicaTransmissora(quadro); // Envia o Quadro
        setTransmitindotrue(); // Seta imagem de Transmitindo True
        try {
          sleep(1000); // Aguarda Confirmacao
        } catch (InterruptedException e) {
        }
      }
      setTransmitindofalse();
      laco = false;
    }).start();

    return quadro;
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioTransmisoraAlohaPuro

  public int[] CamadaAcessoAoMeioTransmissoraSlottedAloha(int quadro[]) {
    Random random = new Random();

    new Thread(() -> {
      
      while ((System.currentTimeMillis() % 10000) % 1000 != 0) {
        
      }
      // System.out.println("slot " + slot + " id " + getId());
      CamadaFisicaTransmissora(quadro);
      setTransmitindotrue();
      try {
        sleep(1000); // sleep para ver se chegou a confirmacao

      } catch (InterruptedException e) {
      }
      // System.out.println("passou sleep " + getId());
      while (!laco) { // Entrar aqui se n chegar
       
        // System.out.println("entrou no laco " + id);
       try {
        sleep((System.currentTimeMillis() % 10000) % 100);
      } catch (InterruptedException e) {
        
      }
          System.out.println(" tentando " + getId());
        CamadaFisicaTransmissora(quadro);
        setTransmitindotrue();
        System.out.println("nao chegou, retransmissao: " + getId());
        try {
          sleep(1000); // Aguarda Confirmacao
        } catch (InterruptedException e) {
        }
      }
      setTransmitindofalse();
      laco = false;
    }).start();

    return quadro;
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioTransmisoraSlottedAloha

  public int[] CamadaAcessoAoMeioTransmissoraCsmaNaoPersistente(int quadro[]) {
    Random random = new Random();

    new Thread(() -> {
      if (pc.getOuvindoMeio() == 0) {
        pc.setOuvindoMeio(1);
        setTransmitindotrue();
        CamadaFisicaTransmissora(quadro);
        try {
          sleep(1000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        while (!laco) {
          try {
            sleep(1500 + random.nextInt(1501));
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          if (pc.getOuvindoMeio() == 0) {
            pc.setOuvindoMeio(1);
            setTransmitindotrue();
            CamadaFisicaTransmissora(quadro);
            try {
              sleep(1000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }

        }
      } else {
        while (!laco) {
          System.out.println("entrou aqui" + getId());
          try {
            sleep(1500 + random.nextInt(1501));
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          if (pc.getOuvindoMeio() == 0) {
            pc.setOuvindoMeio(1);
            setTransmitindotrue();
            CamadaFisicaTransmissora(quadro);
            try {
              sleep(1000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        }
      }

      laco = false;
      setTransmitindofalse();
    }).start();

    return quadro;
  }

  int[] CamadaAcessoAoMeioTransmissoraCsmaPersistente(int quadro[]) {
    Random random = new Random();

    new Thread(() -> {
      if (pc.getOuvindoMeio() == 0) { // verifica se o meio esta livre
        pc.setOuvindoMeio(1); // ocupa o meio
        setTransmitindotrue(); // seta a visibildade da transmissao
        CamadaFisicaTransmissora(quadro); // envia o quadro
        try {
          sleep(1000); // espera da transmissao
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        while (!laco) { // retransmissao caso tenha erro
          try {
            sleep(1500 + random.nextInt(1501));
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          while (pc.getOuvindoMeio() != 0) {
            try {
              sleep(10);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          pc.setOuvindoMeio(1);
          setTransmitindotrue();
          CamadaFisicaTransmissora(quadro);
          try {
            sleep(1000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      } else {
        while (pc.getOuvindoMeio() != 0) {
          try {
            sleep(1000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        pc.setOuvindoMeio(1);
        setTransmitindotrue();
        CamadaFisicaTransmissora(quadro);
        try {
          sleep(1000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        while (!laco) { // retransmissao caso tenha erro
          try {
            sleep(1500 + random.nextInt(1501));
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          while (pc.getOuvindoMeio() != 0) {
            try {
              sleep(1000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          pc.setOuvindoMeio(1);
          setTransmitindotrue();
          CamadaFisicaTransmissora(quadro);

        }
      }

      laco = false;
      setTransmitindofalse();
    }).start();

    return quadro;

    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioTransmisoraCsmaPersistente

  int[] CamadaDeAcessoAoMeioTransmissoraCsmaPPersistente(int quadro[]) {
    Random random = new Random();

    new Thread(() -> {
      long slot = ((System.currentTimeMillis() % 1000) % 100);
      if (pc.getOuvindoMeio() == 0 && slot == 0) {

        p = random.nextInt(2);
        if (p == 0) {
          System.out.println(" " + " id " + getId());
          pc.setOuvindoMeio(1); // ocupa o meio
          setTransmitindotrue(); // seta a visibildade da transmissao
          CamadaFisicaTransmissora(quadro); // envia o quadro
          try {
            sleep(1000); // espera da transmissao
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        while (!laco) { // retransmissao caso tenha erro
          try {
            sleep(1500 + random.nextInt(1501));
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          p = random.nextInt(2);
          slot = ((System.currentTimeMillis() % 1000) % 100);
          while (pc.getOuvindoMeio() != 0 || slot != 0 || p != 0) {
            p = random.nextInt(2);
            slot = ((System.currentTimeMillis() % 1000) % 100);
          }
          pc.setOuvindoMeio(1);
          setTransmitindotrue();
          CamadaFisicaTransmissora(quadro);
          try {
            sleep(1000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      } else {

        p = random.nextInt(2);
        slot = ((System.currentTimeMillis() % 1000) % 100);
        while (pc.getOuvindoMeio() != 0 || slot != 0 || p != 0) {
          slot = ((System.currentTimeMillis() % 1000) % 100);
          p = random.nextInt(2);
        }
        pc.setOuvindoMeio(1);
        CamadaFisicaTransmissora(quadro);
        setTransmitindotrue();
        System.out.println("nao chegou, retransmissao: " + getId());
        try {
          sleep(1000); // Aguarda Confirmacao
        } catch (InterruptedException e) {
        }
        while (!laco) { // retransmissao caso tenha erro
          try {
            sleep(1500 + random.nextInt(1501));
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          p = random.nextInt(2);
          slot = ((System.currentTimeMillis() % 1000) % 100);
          while (pc.getOuvindoMeio() != 0 || slot != 0 || p != 0) {
            p = random.nextInt(2);
            slot = ((System.currentTimeMillis() % 1000) % 100);
          }
          pc.setOuvindoMeio(1);
          setTransmitindotrue();
          CamadaFisicaTransmissora(quadro);
          try {
            sleep(1000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
      setTransmitindofalse();
      laco = false;
    }).start();

    return quadro;

    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioTransmisoraCsmaPPersistente

  int[] CamadaDeAcessoAoMeioTransmissoraCsmaCD(int quadro[]) {
    Random random = new Random();

    new Thread(() -> {
    while(testeMeio){
      if(pc.getOuvindoMeio() == 0){
        if(bateu2 == true){
          pc.setOuvindoMeio(1);
          setTransmitindotrue();
          CamadaFisicaTransmissora(quadro);
          while(!laco){
             try {
              sleep(1500 + random.nextInt(1501));
            } catch (InterruptedException e) {
            }
            while (pc.getOuvindoMeio() != 0) {
                try {
                  sleep(10);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              pc.setOuvindoMeio(1);
            setTransmitindotrue();
           CamadaFisicaTransmissora(quadro);
            try {
              sleep(1000);
            } catch (InterruptedException e) {
            }
          }
        }else{
          try {
              sleep(1500 + random.nextInt(1501));
            } catch (InterruptedException e) {
            }
            while (pc.getOuvindoMeio() != 0) {
                try {
                  sleep(10);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              pc.setOuvindoMeio(1);
            setTransmitindotrue();
           CamadaFisicaTransmissora(quadro);
            try {
              sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        laco = false;
        setTransmitindofalse();
        testeMeio = false;
      }else{
          try {
              sleep(1500 + random.nextInt(1501));
            } catch (InterruptedException e) {
            }
      }
    }
    }).start();
    return quadro;
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioTransmisoraCsmaCD

  /************************************************************************************************* */
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

    CamadaAcessoAoMeioReceptora(fluxoBrutoDeBits);

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
    // System.out.print("\nManchester: ");
    // System.out.println(manchesterString.toString());
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
    // System.out.print("\nManchester Diferencial: ");
    // System.out.println(manchesterDifString.toString());
    return resultadoBinario;
  }

  public void CamadaDeAplicacaoReceptora(int quadro[]) {// metodo responsavel por pegar o fluxoDeBits e transformar em
                                                        // String
    int[] transfDecimal = binarioParaDecimal(quadro);
    String decimal = decimaisParaAscii(transfDecimal);
    AplicacaoReceptora(decimal);
  }

  public void AplicacaoReceptora(String mensagem) { // metodo responsavel por exibir a mensagem descodificada na tela
    switch (this.id) {
      case 1:
        Platform.runLater(() -> {
          pc.receptorText2.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText3.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText4.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText5.setText("comp" + getId() + ": " + mensagem);
        });
        break;

      case 2:
        Platform.runLater(() -> {
          pc.receptorText1.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText3.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText4.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText5.setText("comp" + getId() + ": " + mensagem);
        });
        break;

      case 3:
        Platform.runLater(() -> {
          pc.receptorText1.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText2.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText4.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText5.setText("comp" + getId() + ": " + mensagem);
        });
        break;

      case 4:
        Platform.runLater(() -> {
          pc.receptorText1.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText2.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText3.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText5.setText("comp" + getId() + ": " + mensagem);
        });
        break;

      case 5:
        Platform.runLater(() -> {
          pc.receptorText1.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText2.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText3.setText("comp" + getId() + ": " + mensagem);
          pc.receptorText4.setText("comp" + getId() + ": " + mensagem);

        });
        break;

    }

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

  /************************************************************************************************* */
  void CamadaEnlaceDadosReceptora(int quadro[]) {
    int tipoDeEnquadramento = pc.getTipoEnquadramento();
    int tCodificacao = pc.getTipoCodificacao();
    int tipoDeControleDeErro = pc.getTipoDeControleDeErro();
    // chama proxima camada
    CamadaEnlaceDadosReceptoraEnquadramento(quadro, tCodificacao, tipoDeEnquadramento, tipoDeControleDeErro);

    CamadaDeAplicacaoReceptora(quadro);
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
    // System.out.print("Desenquadramento da Contagem de caracteres: ");
    // Imprime os quadros
    for (int quadross : quadrosDesenquadrados) {
      // System.out.print(quadross);
    }
    // System.out.println("\n");

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

    // System.out.println("\nDesenquadramento da insercao bytes:");
    for (int quadros : quadroDesenquadradoFinal) {
      // System.out.print(Integer.toBinaryString(quadros) + " ");
    }
    // System.out.println("\n");

    return quadroDesenquadradoFinal;
  }

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBits(int quadro[]) {
    final int FLAG = 0b01111110; // Flag: 0111 1110 (8 bits)
    final int MAX_CONSEC_UNS = 5; // Numero maximo de uns consecutivos antes de inserir um bit 0

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
    // System.out.println("\nQuadro desenquadrado pela insercao de bits:");
    for (int quadros : quadroDesenquadradoFinal) {
      // System.out.print(Integer.toBinaryString(quadros) + " ");
    }
    // System.out.println("\n");

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

      // Remove o bit de paridade (ultimo bit)
      quadro[quadro.length - 1] = quadro[quadro.length - 1] >> 1;
      // System.out.print("Array (era paridade par): ");
      for (int num : quadro) {
        // System.out.print(num + " ");
      }
      return quadro;
    } else {
      // System.out.print("erro: ");
      for (int num : quadro) {
        // System.out.print(num + " ");
      }
    }

    // Retorna o array original se houver erro de paridade
    return quadro;
  }

  public int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(int quadro[]) {
    int totalDeUns = 0;

    // Calcula o numero total de bits '1' no array de entrada (excluindo o bit de
    // paridade)
    for (int i = 0; i < quadro.length; i++) {
      totalDeUns += Integer.bitCount(quadro[i]);
    }

    // Verifica se a quantidade de bits '1' eh par (excluindo o bit de paridade)
    if (totalDeUns % 2 != 0) {

      // Remove o bit de paridade (ultimo bit)
      quadro[quadro.length - 1] = quadro[quadro.length - 1] >> 1;
      // System.out.print("Array (era paridade impar): ");
      for (int num : quadro) {
        // System.out.print(num + " ");
      }
      return quadro;
    } else {
      // System.out.print("erro: ");
      for (int num : quadro) {
        // System.out.print(num + " ");
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

      // Calculo dos bits de paridade recebidos
      int[] calculaParidade = new int[] {
          blocoRecebido[2], blocoRecebido[4], blocoRecebido[5], blocoRecebido[6] };

      // Verificacao de erros
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
    // System.out.print("Dados decodificados: ");
    for (int bit : quadroFinal) {
      // System.out.print(bit);
    }

    return quadroFinal;
  }// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming

  void CamadaAcessoAoMeioReceptora(int quadro[]) {
    int acessoAoMeio[] = null;
    int tipodeAcesso = pc.getTipoDeMeio();
    switch (tipodeAcesso) {
      case 0: // bit de paridade par
        acessoAoMeio = CamadaAcessoAoMeioReceptoraAlohaPuro(quadro);
        break;
      case 1: // bit de paridade impar
        acessoAoMeio = CamadaAcessoAoMeioReceptoraSlottedAloha(quadro);
        break;
      case 2: // CRC
        acessoAoMeio = CamadaAcessoAoMeioReceptoraCsmaNaoPersistente(quadro);
        break;
      case 3: // codigo de Hamming
        acessoAoMeio = CamadaAcessoAoMeioReceptoraCsmaPersistente(quadro);
        break;
      case 4: // codigo de Hamming
        acessoAoMeio = CamadaAcessoAoMeioReceptoraCsmaPPersistente(quadro);
        break;
      case 5: // codigo de Hamming
        acessoAoMeio = CamadaAcessoAoMeioReceptoraCsmaCD(quadro);
        break;

    }
    CamadaEnlaceDadosReceptora(quadro);
  }// fim do metodo CamadaAcessoAoMeioDadosReceptora

  int[] CamadaAcessoAoMeioReceptoraAlohaPuro(int quadro[]) {
    System.out.println("chegou aqui " + getId());

    if (bateu == true) {
      bateu = false;
      return null;

    } else {
      System.out.println("terminou " + getId());
      laco = true;
      bateu = false;
      return quadro;
    }
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioReceptoraAlohaPuro

  int[] CamadaAcessoAoMeioReceptoraSlottedAloha(int quadro[]) {
    System.out.println("chegou aqui " + getId());

    if (bateu == true) {
      bateu = false;
      return null;

    } else {
      System.out.println("terminou " + getId());
      laco = true;
      bateu = false;
      return quadro;
    }
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioReceptoraSlottedAloha

  int[] CamadaAcessoAoMeioReceptoraCsmaNaoPersistente(int quadro[]) {
    if (bateu == true) {
      pc.setOuvindoMeio(0);
      bateu = false;
      return null;

    } else {
      System.out.println("terminou " + getId());
      pc.setOuvindoMeio(0);
      laco = true;
      bateu = false;
      return quadro;
    }
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioReceptoraCsmaNaoPersistente

  int[] CamadaAcessoAoMeioReceptoraCsmaPersistente(int quadro[]) {
    if (bateu == true) {
      pc.setOuvindoMeio(0);
      bateu = false;
      return null;

    } else {
      System.out.println("terminou " + getId());
      pc.setOuvindoMeio(0);
      laco = true;
      bateu = false;
      return quadro;
    }
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioReceptoraCsmaPersistente

  int[] CamadaAcessoAoMeioReceptoraCsmaPPersistente(int quadro[]) {
    if (bateu == true) {
      pc.setOuvindoMeio(0);
      bateu = false;
      return null;

    } else {
      System.out.println("terminou " + getId());
      pc.setOuvindoMeio(0);
      laco = true;
      bateu = false;
      return quadro;
    }
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioReceptoraCsmaPPersistente

  int[] CamadaAcessoAoMeioReceptoraCsmaCD(int quadro[]) {
     if (bateu == true) {
      pc.setOuvindoMeio(0);
      bateu = false;
      bateu2 = true;
      return null;

    } else {
      System.out.println("terminou " + getId());
      pc.setOuvindoMeio(0);
      laco = true;
      bateu = false;
      bateu2 = true;
      return quadro; 
    }
    // algum codigo aqui
  }// fim do metodo CamadaAcessoAoMeioReceptoraCsmaCD

  public long getId() {
    return id;
  }

  public void setTransmitindotrue() {
    Platform.runLater(() -> {
      transmitindo.setVisible(true);
    });
  }

  public void setTransmitindofalse() {
    Platform.runLater(() -> {
      transmitindo.setVisible(false);
    });
  }

}
