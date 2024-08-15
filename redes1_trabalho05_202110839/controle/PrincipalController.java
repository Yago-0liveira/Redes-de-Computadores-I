/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 28/''/2023
* Ultima alteracao.: 06/12/2023
* Nome.............: PrincipalController.java
* Funcao...........: Eh responsavel por lidar com eventos gerados pelos componentes da GUI LayoutPrincipal.fxml.
Eh tambem responsavel por controlar eventos ocorridos nas classes pertencenetes ao pacote modelo, assim como por
atualizar a GUI com dados do programa e o programa com dados advindos da GUI.  
*************************************************************** */

package controle;

import java.net.URL;
import java.util.ResourceBundle;
import modelo.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class PrincipalController implements Initializable {

  @FXML
  private HBox boxGrafico;

  @FXML
  private Button idEnviar;

  @FXML
  private RadioMenuItem menuAloha;

  @FXML
  private RadioMenuItem menuBits;

  @FXML
  private RadioMenuItem menuBytes;

  @FXML
  private RadioMenuItem menuCamadaFisica;

  @FXML
  private RadioMenuItem menuCd;

  @FXML
  private RadioMenuItem menuCrc;

  @FXML
  private RadioMenuItem menuDiferencial;

  @FXML
  private RadioMenuItem menuHamming;

  @FXML
  private RadioMenuItem menuImpar;

  @FXML
  private RadioMenuItem menuManchester;

  @FXML
  private RadioMenuItem menuNaoPersit;

  @FXML
  private RadioMenuItem menuPar;

  @FXML
  private RadioMenuItem menuPersist;

  @FXML
  private RadioMenuItem menuPpersist;

  @FXML
  private RadioMenuItem menuSlotted;

  @FXML
  private RadioMenuItem menubinario;

  @FXML
  private RadioMenuItem menuContagem;

  @FXML
  public TextArea receptorText1;

  @FXML
  public TextArea receptorText2;

  @FXML
  public TextArea receptorText3;

  @FXML
  public TextArea receptorText4;

  @FXML
  public TextArea receptorText5;
 
  @FXML
  private ImageView colisao1;

  @FXML
  private ImageView colisao2;

  @FXML
  private ImageView colisao3;

  @FXML
  private ImageView colisao4;

  @FXML
  private ImageView colisao5;

  @FXML
  private ImageView transmitindo1;

  @FXML
  private ImageView transmitindo2;

  @FXML
  private ImageView transmitindo3;

  @FXML
  private ImageView transmitindo4;

  @FXML
  private ImageView transmitindo5;

  @FXML
  private Slider sliderErro;

  private int tipoCodificacao;
  private int tipoDeEnquadramento;
  private int tipoDeControleDeErro;
  private int tipoDeMeio;
  private int ouvindoMeio = 0;

  TransmissorReceptor transmissor;
  Comunicacao comunicacao;
  int nivelErro = 1;
  int entrouMeio = 0;

  private void showCaixaAlertaEspecial() { // metodo da caixa de alerta texto vazio
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Aviso triste");
    alert.setHeaderText("tipo de enquadramento invalido");
    alert.setContentText("Nao consegui implementar o enquadramento por violacao da camada fisica :( ");

    alert.showAndWait();
  }

  @FXML
  void AplicacaoTransmissora(MouseEvent event) { // metodo referente as acoes tomadas apos clicar no botao enviar
    // mensagem
    setTipoCodificacao(-1);
    setTipoEnquadramento(-1);
    setTipoDeControleDeErro(-1);
    setTipoDeMeio(-1);

    if (menubinario.isSelected()) {
      setTipoCodificacao(0);
      limpaGrafico();
    }

    if (menuManchester.isSelected()) {
      setTipoCodificacao(1);
      limpaGrafico();
    }

    if (menuDiferencial.isSelected()) {
      setTipoCodificacao(2);
      limpaGrafico();
    }

    if (menuContagem.isSelected()) {
      setTipoEnquadramento(0);

    }

    if (menuBytes.isSelected()) {
      setTipoEnquadramento(1);

    }

    if (menuBits.isSelected()) {
      setTipoEnquadramento(2);
    }

    if (menuCamadaFisica.isSelected()) {
      showCaixaAlertaEspecial();
      setTipoEnquadramento(3);
    }

    if (menuPar.isSelected()) {
      setTipoDeControleDeErro(0);
    }

    if (menuImpar.isSelected()) {
      setTipoDeControleDeErro(1);
    }
    if (menuCrc.isSelected()) {
      setTipoDeControleDeErro(2);
    }
    if (menuHamming.isSelected()) {
      setTipoDeControleDeErro(3);
    }

    if (menuAloha.isSelected()) {
      setTipoDeMeio(0);
    }
    if (menuSlotted.isSelected()) {
      setTipoDeMeio(1);
    }
    if (menuNaoPersit.isSelected()) {
      setTipoDeMeio(2);
    }
    if (menuPersist.isSelected()) {
      setTipoDeMeio(3);
    }
    if (menuPpersist.isSelected()) {
      setTipoDeMeio(4);
    }
    if (menuCd.isSelected()) {
      setTipoDeMeio(5);
    }
    TransmissorReceptor transrec1 = new TransmissorReceptor(this, 1, transmitindo1, colisao1);
    TransmissorReceptor transrec2 = new TransmissorReceptor(this, 2, transmitindo2, colisao2);
    TransmissorReceptor transrec3 = new TransmissorReceptor(this, 3, transmitindo3, colisao3);
    TransmissorReceptor transrec4 = new TransmissorReceptor(this, 4, transmitindo4, colisao4);
    TransmissorReceptor transrec5 = new TransmissorReceptor(this, 5, transmitindo5, colisao5);

    transrec1.start();

    transrec2.start();

    transrec3.start();

    transrec4.start();

    transrec5.start();

    tamanhoTexto(receptorText1); // chamada do metodo de alteracoes em textAreas
    tamanhoTexto(receptorText2);
    tamanhoTexto(receptorText3);
    tamanhoTexto(receptorText4);
    tamanhoTexto(receptorText5);
   
  }

  public void tamanhoTexto(TextArea text) { // metodo de modificacao dos textarea
    text.setStyle("-fx-control-inner-background: lightblue;"); // define a cor do textArea
    // Adicione um ouvinte para o evento de mudanca de texto
    text.textProperty().addListener((observable, oldValue, newValue) -> {
      // Calcule o numero de linhas no TextArea
      int numLinhas = text.getText().split("").length;

      if (numLinhas > 48) { // bloco para definir o tamanho do texto area
        text.setPrefHeight(275);
      } else {
        // Ajuste a altura do TextArea com base no numero de linhas
        text.setPrefHeight(numLinhas * 5);
      }
    });

  }

  public void limpaGrafico() { // metodo para limpar o grafico
    boxGrafico.getChildren().clear();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) { // metodo para definir requisitos na inicializacao do
                                                                   // programa

    receptorText1.setWrapText(true);
    receptorText1.setEditable(false);
    receptorText2.setWrapText(true);
    receptorText2.setEditable(false);
    receptorText3.setWrapText(true); // Permite que o texto quebre automaticamente nas linhas
    receptorText3.setEditable(false); // Torna o TextArea somente leitura
    receptorText4.setWrapText(true);
    receptorText4.setEditable(false);
    receptorText5.setWrapText(true);
    receptorText5.setEditable(false);

    colisao1.setVisible(false);
    colisao2.setVisible(false);
    colisao3.setVisible(false);
    colisao4.setVisible(false);
    colisao5.setVisible(false);

    transmitindo1.setVisible(false);
    transmitindo2.setVisible(false);
    transmitindo3.setVisible(false);
    transmitindo4.setVisible(false);
    transmitindo5.setVisible(false);

    receptorText1.setOnKeyTyped(event -> {
      String texto = receptorText1.getText();
      int caretPosition = receptorText1.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    receptorText2.setOnKeyTyped(event -> {

      String texto = receptorText2.getText();
      int caretPosition = receptorText2.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    // Define um ouvinte de eventos para o evento
    receptorText3.setOnKeyTyped(event -> {

      String texto = receptorText3.getText();
      int caretPosition = receptorText3.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    receptorText4.setOnKeyTyped(event -> {

      String texto = receptorText4.getText();
      int caretPosition = receptorText4.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    receptorText5.setOnKeyTyped(event -> {

      String texto = receptorText5.getText();
      int caretPosition = receptorText5.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    sliderErro.setMin(1);
    sliderErro.setMax(5);
    sliderErro.setValue(2);
    sliderErro.setMajorTickUnit(1);
    sliderErro.setMinorTickCount(0);
    sliderErro.setSnapToTicks(true);

    sliderErro.valueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        // passando o novo valor para o atributo de velocidade do consumidor
        nivelErro = newValue.intValue();
      }
    });

  }

  private static void sinal(HBox display, int current, int prev) { // metodo responsavel pela criacao dos sinais da onda
    Platform.runLater(() -> {
      ObservableList led = display.getChildren();
      VBox vBox = new VBox();
      if (current == 0) {
        vBox.setAlignment(Pos.BOTTOM_LEFT);
      } else {
        vBox.setAlignment(Pos.TOP_LEFT);
      }
      if (current != prev && !led.isEmpty()) {
        led.add(0, new Line(0, 0, 0,
            display.getHeight() - display.getPadding().getTop() - display.getPadding().getBottom() - 1));
      }
      vBox.getChildren().add(new Line(0, 0, 50, 0));
      led.add(0, vBox);
    });
  }

  public static void show(int[] bits, HBox display) { // metodo para a construcao e apresentacao dos sinas criados no
                                                      // metodo acima
    for (int i = bits.length - 1; i >= 0; i--) {
      if (i == bits.length - 1) {
        sinal(display, bits[i], 'n');
      } else {
        sinal(display, bits[i], bits[i + 1]);
      }
    }

  }

  public void setTipoCodificacao(int codificacao) { // metodo de definicao do tipo de codificao
    this.tipoCodificacao = codificacao;
  }

  public void setTipoEnquadramento(int tipoEnquadramento) {
    this.tipoDeEnquadramento = tipoEnquadramento;
  }

  public int getTipoEnquadramento() {
    return tipoDeEnquadramento;
  }

  public int getTipoCodificacao() { // metodo de recebimento do tipo de codificacao
    return tipoCodificacao;
  }

  public HBox getBoxGrafico() { // metodo de recebimento do box
    return boxGrafico;
  }

  public int getTipoDeControleDeErro() {
    return tipoDeControleDeErro;
  }

  public void setTipoDeControleDeErro(int tipoDeControleDeErro) {
    this.tipoDeControleDeErro = tipoDeControleDeErro;
  }

  public int getNivelErro() {
    return nivelErro;
  }

  public void setNivelErro(int nivelErro) {
    this.nivelErro = nivelErro;
  }

  public int getTipoDeMeio() {
    return tipoDeMeio;
  }

  public void setTipoDeMeio(int tipoDeMeio) {
    this.tipoDeMeio = tipoDeMeio;
  }

  public int getVariavelGlobal() {
    return entrouMeio;
  }

  public void setVariavelGlobal(int global) {
    this.entrouMeio = global;
  }

  public int getOuvindoMeio() {
    return ouvindoMeio;
  }

  public void setOuvindoMeio(int ouvindoMeio) {
    this.ouvindoMeio = ouvindoMeio;
  }

}
