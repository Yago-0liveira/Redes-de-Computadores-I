/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 11/10/2023
* Ultima alteracao.: 15/10/2023
* Nome.............: CamadEnlaceReceptora.java
* Funcao...........: Eh responsavel pelo desenquadramento correto dos sinais
*************************************************************** */
package modelo;

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
    // chama proxima camada
    CamadaEnlaceDadosReceptoraEnquadramento(quadro,tCodificacao, tipoDeEnquadramento);
    receptor = new Receptor(pc);
    receptor.CamadaDeAplicacaoReceptora(quadro);
  }

  void CamadaEnlaceDadosReceptoraEnquadramento(int quadro[], int tcodificacao, int tipoDeEnquadramento) {
    int quadroDesenquadrado[];
    switch (tipoDeEnquadramento) {
      case 0: // contagem de caracteres
      //System.out.println("ta aq");
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres(quadro);
        break;
      case 1: // insercao de bytes
      //System.out.println("ta aq2");
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBytes(quadro);
        break;
      case 2: // insercao de bits
     // System.out.println("ta aq3");
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBits(quadro);
      case 3: // violacao da camada fisica
        quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoViolacaoDaCamadaFisica(quadro);
        break;
    }// fim do switch/case
  }// fim do metodo CamadaEnlaceDadosReceptoraEnquadramento

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres(int quadro[]) {
   //para transformar em bytes
		System.out.println("CAMADA DE ENLACE - Caracteres");
		int quadro2[] = new int[(quadro.length) / 8];
		String str = "";
		int j = 0;
		int k = 0;
		for (int i = 0; i < quadro.length;) {
			str = "";
			j = 0;
			while (j < 8 && i < quadro.length) {
				str += quadro[i];
				j++;
				i++;
			}
			int valor = Integer.parseInt(str, 2);
			quadro2[k] = valor;
			k++;
		}

		int quadroDesenquadrado[] = new int[(quadro2.length) - 1];
		for (int i = 0, count = 1; i < quadro2.length - 1; i++, count++) {
			quadroDesenquadrado[i] = quadro2[i + 1];
			System.out.println(quadroDesenquadrado[i]);
		}

  return quadro;
  }

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBytes(int quadro[]) {
    	//tranforma em bytes
	  System.out.println(" ");
		int quadro2[] = new int[(quadro.length) / 8];
		String str = "";
		int j = 0;
		int k = 0;
		for (int i = 0; i < quadro.length;) {
			str = "";
			j = 0;
			while (j < 8 && i < quadro.length) {
				str += quadro[i];
				j++;
				i++;
			}
			int valor = Integer.parseInt(str, 2);
			quadro2[k] = valor;
			k++;
		} 

		//DESENQUADRAMENTO
		int quadroDesenquadrado[] = new int[2];
		quadroDesenquadrado[0] = 0;
		quadroDesenquadrado[1] = 0;
		char flag = '$';
		byte bytes2 = (byte) flag;
		char esc = '|';
		byte escape = (byte) esc;
		int count = 0;
		if (quadro2[3] == flag) {//se a ultima posicao for flag de fim 
			for (int i = 1; i < 3; i++) { //so percorre os dois 
				System.out.println(quadro2[i]);
					if (quadro2[i] == escape) {
						if (i == 1) {
							quadroDesenquadrado[count] = quadro2[i + 1];
							
						}
						else {
    	   			
						}
					}
					else {
						quadroDesenquadrado[count] = quadro2[i];
						count++;
						if (i == 2) {
						}
					}
			}
		}
		else {//só percorre uma posicao
			if (quadro2[1] == 0) {
				
			}
			else {
				quadroDesenquadrado[0] = quadro2[1];
			}
		}

    return quadro;
  }

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoInsercaoDeBits(int quadro[]) {
    int mask;
    System.out.println("bits");
    for (int i = 0; i < quadro.length; i++) {
      mask = 0;
      for (int a = 31, s = 1; a >= 0; a--, s++) {
        mask = 1 << a;
        System.out.print((((quadro[i] & mask) != 0) ? 1 : 0));
        System.out.print(((s % 8 == 0) ? " " : ""));
      } 
      System.out.println("");
    } 

    int flag = 126; // flag 01111110
    int mascaraDeslocamento = 31; // auxilia no deslocamento
    int mascara = 1 << mascaraDeslocamento;
    boolean parar = true;
    boolean sairWhile;
    int flagTemp;
    for (int j = 0; j < quadro.length; j++) {
      for (int i = 0; i < quadro.length; i++) {
        flagTemp = flag << 24;
        flagTemp = flagTemp ^ flag;
        quadro[j] = flagTemp ^ quadro[j];
      } 
      int cont = 0;
      while (parar != false) {
        if (cont > 32) {
          parar = false;
        } 
        if ((quadro[j] & mascara) != 0) { // quadr[i]==1
          int contBits = 0;
          sairWhile = false;
          int contDeslocamento = mascaraDeslocamento;
          int contMascara = 1 << (contDeslocamento);
          while ((contBits < 5) && (sairWhile != true)) {
            if ((quadro[j] & contMascara) != 0) {
              contBits++;
            } 
            else {
              sairWhile = true;
            } 
            contDeslocamento -= 1;
            contMascara = 1 << contDeslocamento;
          } 
          int mascaraDiv = 1;
          int maskDivTemp = 1;
          if (contBits == 5) {
            mascaraDiv <<= 16;
            maskDivTemp <<= 15;
            mascaraDiv = mascaraDiv | maskDivTemp;
          } 

          int mascaraTemp = mascaraDiv & quadro[j];
          quadro[j] = mascaraTemp ^ quadro[j];
          mascaraTemp <<= 1;
          quadro[j] = mascaraTemp | quadro[j];

        }
        mascaraDeslocamento -= 1;
        mascara >>= 1;
        cont++;
      } 
      quadro[j] >>= 16;

    } 
    return quadro;
  }

  public int[] CamadaEnlaceDadosReceptoraEnquadramentoViolacaoDaCamadaFisica(int quadro[]) {
    // implementacao do algoritmo para DESENQUADRAR
    return quadro;
  }// fim do metodo CamadaEnlaceDadosReceptoraViolacaoDaCamadaFisica

}
