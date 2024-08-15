/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 11/10/2023
* Ultima alteracao.: 15/10/2023
* Nome.............: CamadEnlaceTransmissora.java
* Funcao...........: Eh responsavel pelo enquadramento correto dos sinais
*************************************************************** */

package modelo;

import controle.PrincipalController;

public class CamadaEnlaceTransmissora {
  Transmissor transmissor;
  public PrincipalController pc;

  public CamadaEnlaceTransmissora(PrincipalController pc) {
    this.pc = pc;
  }

  public void CamadaEnlaceDadosTransmissora(int quadro[]) {
    int tipoDeEnquadramento = pc.getTipoEnquadramento(); 
    int tCodificacao = pc.getTipoCodificacao();
    CamadaEnlaceDadosTransmissoraEnquadramento(quadro, tCodificacao, tipoDeEnquadramento);
    // chama proxima camada
    transmissor = new Transmissor(pc);
    transmissor.CamadaFisicaTransmissora(quadro, tCodificacao, tipoDeEnquadramento);
  }// fim do metodo CamadaEnlaceDadosTransmissora

  public void CamadaEnlaceDadosTransmissoraEnquadramento(int quadro[], int tcodificacao, int tipoDeEnquadramento) {
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
    int tamanho = 4;
		int vezes;
		int quadroEnquadrado[] = new int[tamanho]; //quadro enquadrado terá no maximo 4 bytes

				//verifica quantos quadros serao criados de acordo com o tamanho do quadro
		if (quadro.length <= 3) {
			vezes = 1;
		}
		else {
			if (quadro.length % 3 == 0) {
				vezes = quadro.length / 3;
			}
			else {
				vezes = (quadro.length / 3) + 1;
			}
		}
		System.out.println("vezes: " + vezes);

		//variaveis auxiliares para percorrer o quadro aos poucos
		int tam = 0;
		int p = quadro.length;

		if (p - 3 >= 0) {
			tam += 3;
			p -= 3;
		}//fim do if
		else {
			tam += p;
		}//fim do else

				//transforma o tamanho do quadro em string para pegar o valor em byte dele
		String contagem = "";
		byte[] cont;
		int bytes;
		int count = 0;
		int aux = 0;
		//criacao dos quadros
		int i = 0;
		for (int j = 0; j < vezes; j++) { //para prencher os n quadros
			for (int k = 1; i < tam && k < 4;) {
				quadroEnquadrado[k] = quadro[i];
				i++;
				k++;
			}
			contagem = "";
			count = 0;
			for (int c = 1; c < 4; c++) {
				if (quadroEnquadrado[c] != 0) {
					count++;
  			}
			}
			aux = 1 + count;
			contagem += aux;
			cont = contagem.getBytes();
			bytes = cont[0];
			quadroEnquadrado[0] = bytes;
	  	//for para imprimir
			for (int q = 0; q < quadroEnquadrado.length; q++) {
				System.out.println(quadroEnquadrado[q]);
			}
			System.out.println("*************");
			//atualiza as variaveis auxiliares para percorrer o quadro
			if (p - 3 >= 0) {
				tam += 3;
      	p -= 3;
			}
			else {
				tam += p;
			}
			//for para zerar todas as posicoes do quadroEnquadrado
			for (int k = 0; k < quadroEnquadrado.length; k++) {
				quadroEnquadrado[k] = 0;
			}
		}
    return quadroEnquadrado;
  }

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(int quadro[]) {
    // inicia todas as posições do vetor com 0
    int quadroEnquadrado[] = new int[4];
    for (int k = 0; k < quadroEnquadrado.length; k++) {
      quadroEnquadrado[k] = 0;
    }
    System.out.println("CAMADA DE ENLACE BYTES");
    char flag = '$';
    byte bytes2 = (byte) flag;
    char esc = '|';
    byte escape = (byte) esc;
    int vezes;
    // verifica quantos quadros serao criados
    if (quadro.length <= 2) {
      vezes = 1;
    }
    else {
      if (quadro.length % 2 == 0) {
        vezes = quadro.length / 2;
      } 
      else {
        vezes = (quadro.length / 2) + 1;
      } 
    } 
    System.out.println("vezes: " + vezes);
    int tam = 0;
    int p = quadro.length;

    if (p - 2 >= 0) {
      tam += 2;
      p -= 2;
    } 
    else {
      tam += p;
    } 

    int i = 0;
    int ok = 0;
    for (int j = 0; j < vezes; j++) { // para prencher os n quadros
      quadroEnquadrado[0] = bytes2;// coloca a flag na primeira posicao do quadro
      for (int k = 1; i < tam && k < 3;) {
        if (quadro[i] == flag) {// se o byte for igual a flag
          if (k == 1) { // cabe os dois bytes
            if (ok == 0) {// se for colocar o escape e o byte no mesmo quadro
              quadroEnquadrado[k] = escape;
              quadroEnquadrado[k + 1] = quadro[i];
              if (quadro.length % 2 == 0) {
                vezes++;
              }
              i++;
              k += 2;
            } 
            else { // se o escape ficou em outro quadro
              quadroEnquadrado[k] = quadro[i];
              ok = 0;
              i++;
              k++;
            } 
          } 
          else { // se o escape ficar em nesse quadro o byte em outro
            quadroEnquadrado[k] = escape;
            ok = 1;
            vezes++;
            k++;
          } 
        } 
        else { // se o byte nao e igual a flag
          quadroEnquadrado[k] = quadro[i];
          i++;
          k++;
        } 
      } 
      if (quadroEnquadrado[2] != 0) {
        quadroEnquadrado[3] = flag;
      } 
      else {
        quadroEnquadrado[2] = flag;
      } 
      
      for (int q = 0; q < quadroEnquadrado.length; q++) {
        System.out.println(quadroEnquadrado[q]);
      } 
      System.out.println("*************");
      if (p - 2 >= 0) {
        tam += 2;
        p -= 2;
      } 
      else {
        tam += p;
        p = 0;
      } 
      for (int k = 0; k < quadroEnquadrado.length; k++) {
        quadroEnquadrado[k] = 0;
      } 
    } 
    return quadroEnquadrado;
  }

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(int quadro[]) {
    int flagbit = 126; // flag
    int mascaraDeslocamento = 31;
    int mascara = 1 << mascaraDeslocamento;
    int count = 0;
    boolean parar = true;
    int contBits = 0;
    boolean sairWhile = false;
    int contMascara;
    for (int i = 0; i < quadro.length; i++) {
      count = 0;
      parar = false;
      quadro[i] <<= 16;
      while (!parar) {
        if (count > 32) {
          parar = true;
        } 
        if ((quadro[i] & mascara) != 0) { // quadro[i]==1
          contBits = 0;
          sairWhile = false;
          int contDeslocamento = mascaraDeslocamento;
          contMascara = 1 << (contDeslocamento);

          while ((contBits < 5) && (!sairWhile)) {
            if ((quadro[i] & contMascara) != 0) {
              contBits++;
            } 
            else {
              sairWhile = true;
            } 
            contDeslocamento -= 1;
            contMascara = 1 << contDeslocamento;
          } 

          int mascaraDiv = 1;
          if (contBits == 5) {
            while ((mascaraDiv & contMascara) == 0) {
              mascaraDiv <<= 1;
              mascaraDiv |= 1;
            } 
          } 

          int mascaraTemp = mascaraDiv & quadro[i];
          quadro[i] = mascaraTemp ^ quadro[i];
          mascaraTemp >>= 1;
          quadro[i] = mascaraTemp | quadro[i];

        } 
        mascaraDeslocamento -= 1;
        mascara >>= 1;
        count++;
      } 

      for (int j = 0; j < quadro.length; j++) {
        int flagTemp = flagbit << 24;
        flagTemp = flagTemp | flagbit;
        quadro[j] = flagTemp | quadro[j];
      } 
    } 

    return quadro;
  }

  public int[] CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(int quadro[]) {
    // Nao consegui fazer :(
    return quadro;
  }// fim do metodo CamadaEnlaceDadosTransmissoraViolacaoDaCamadaFisica

}
