/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 28/11/2023
* Ultima alteracao.: 06/12/2023
* Nome.............: Transmissor.java
* Funcao...........: Eh responsavel pela intermediacao entre as classes Transmissao e comunicacao, nela ocorre a passagem dos dados da classe
Transmssora para a receptora.  
*************************************************************** */

package modelo;

import java.util.Random;
import java.util.concurrent.Semaphore;
import controle.PrincipalController;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class Comunicacao {
  private TransmissorReceptor transrRec;
  private PrincipalController pc;
  ImageView bateu;
  Semaphore sem = new Semaphore(1);
  static int numeroTransmissores = 0;
  

  public Comunicacao(PrincipalController pc, ImageView bateu) {
    this.pc = pc;
    this.bateu = bateu;
  }

  public void MeioDeComunicacao(int fluxoBrutoDeBits[], TransmissorReceptor t) { // metodo responsavel por transferir a
                                                                                 // mensagem de um aparelho
    new Thread(() -> {
      try {
        sem.acquire();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      numeroTransmissores += 1;

      if (numeroTransmissores > 1) {
        t.bateu = true;
        Platform.runLater(() -> {
          bateu.setVisible(true);
        });
        System.out.println("bateu " + t.getId());
      }
      sem.release();
   
      int tipoDeEnquadramento = pc.getTipoEnquadramento();
      int tCodificacao = pc.getTipoCodificacao();
      int tipoDeControleDeErro = pc.getTipoDeControleDeErro();
      int erro, porcentagemDeErros;
      int[] fluxoBrutoDeBitsPontoA, fluxoBrutoDeBitsPontoB;
      Random aleatorio = new Random();
      int posiBit = aleatorio.nextInt(7);
      int numAleatorio = aleatorio.nextInt(pc.getNivelErro());
      porcentagemDeErros = 0;
      fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
      fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBitsPontoA.length];
      int indexDoFluxoDeBits = 0;
      while (indexDoFluxoDeBits < fluxoBrutoDeBitsPontoA.length) {
        if (numAleatorio == 0) {
          fluxoBrutoDeBitsPontoB[indexDoFluxoDeBits] += fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits];
        } // fazer a probabilidade do erro
        else {
          fluxoBrutoDeBitsPontoB[indexDoFluxoDeBits] += (fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits] == 0)
              ? ++fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits]
              : --fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits];
        }
        indexDoFluxoDeBits++;
        try {
          Thread.sleep(8);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
      try {
        sem.acquire();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      numeroTransmissores--;
      Platform.runLater(() -> {
        bateu.setVisible(false);
      });
      sem.release();
      t.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB, tCodificacao, tipoDeEnquadramento, tipoDeControleDeErro);
    }).start();
  }


  public void MeioDeComunicacao2(int fluxoBrutoDeBits[], TransmissorReceptor t) { // metodo responsavel por transferir a
                                                                                 // mensagem de um aparelho
    new Thread(() -> {
      try {
        sem.acquire();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      numeroTransmissores += 1;

      if (numeroTransmissores > 1) {
        t.bateu = true;
        t.bateu2 = false;
        Platform.runLater(() -> {
          bateu.setVisible(true);
        });
        System.out.println("bateu " + t.getId());
      }
      sem.release();
      if(t.bateu == true && pc.getTipoDeMeio() == 5){

       
      }else{
      int tipoDeEnquadramento = pc.getTipoEnquadramento();
      int tCodificacao = pc.getTipoCodificacao();
      int tipoDeControleDeErro = pc.getTipoDeControleDeErro();
      int erro, porcentagemDeErros;
      int[] fluxoBrutoDeBitsPontoA, fluxoBrutoDeBitsPontoB;
      Random aleatorio = new Random();
      int posiBit = aleatorio.nextInt(7);
      int numAleatorio = aleatorio.nextInt(pc.getNivelErro());
      porcentagemDeErros = 0;
      fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
      fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBitsPontoA.length];
      int indexDoFluxoDeBits = 0;
      while (indexDoFluxoDeBits < fluxoBrutoDeBitsPontoA.length) {
        if (numAleatorio == 0) {
          fluxoBrutoDeBitsPontoB[indexDoFluxoDeBits] += fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits];
        } // fazer a probabilidade do erro
        else {
          fluxoBrutoDeBitsPontoB[indexDoFluxoDeBits] += (fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits] == 0)
              ? ++fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits]
              : --fluxoBrutoDeBitsPontoA[indexDoFluxoDeBits];
        }
        indexDoFluxoDeBits++;
        try {
          Thread.sleep(8);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
      try {
        sem.acquire();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      numeroTransmissores--;
      Platform.runLater(() -> {
        bateu.setVisible(false);
      });
      sem.release();
      t.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB, tCodificacao, tipoDeEnquadramento, tipoDeControleDeErro);
   }
    }).start();
  }
}


