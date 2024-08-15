/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 29/08/2023
* Ultima alteracao.: 07/09/2023
* Nome.............: Transmissor.java
* Funcao...........: Eh responsavel pela intermediacao entre as classes Transmissao e comunicacao, nela ocorre a passagem dos dados da classe
Transmssora para a receptora.  
*************************************************************** */

package modelo;

import controle.PrincipalController;

public class Comunicacao {
  private Receptor rec;
  private PrincipalController pc;

  public Comunicacao(PrincipalController pc) {
    this.pc = pc;
  }

  public void MeioDeComunicacao(int fluxoBrutoDeBits[]) { // metodo responsavel por transferir a mensagem de um aparelho
                                                          // para outro
    int tipoDeEnquadramento = pc.getTipoEnquadramento(); 
    int tCodificacao = pc.getTipoCodificacao();
    int[] fluxoBrutoDeBitsPontoA, fluxoBrutoDeBitsPontoB;
    fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
    fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBitsPontoA.length];
    int indexDoFluxoDeBits = 0;
    while (indexDoFluxoDeBits < fluxoBrutoDeBitsPontoA.length) {
      fluxoBrutoDeBitsPontoB[indexDoFluxoDeBits] += fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits];
      indexDoFluxoDeBits++;

    }
    rec = new Receptor(pc);
    rec.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB, tCodificacao, tipoDeEnquadramento);
  }

}
