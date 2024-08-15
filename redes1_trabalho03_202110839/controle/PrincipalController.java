/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 29/08/2023
* Ultima alteracao.: 11/09/2023
* Nome.............: PrincipalController.java
* Funcao...........: Eh responsavel por lidar com eventos gerados pelos componentes da GUI LayoutPrincipal.fxml.
Eh tambem responsavel por controlar eventos ocorridos nas classes pertencenetes ao pacote modelo, assim como por
atualizar a GUI com dados do programa e o programa com dados advindos da GUI.  
*************************************************************** */

package controle;

import java.net.URL;
import java.util.ResourceBundle;
import modelo.*;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

public class PrincipalController implements Initializable {

  @FXML
  public TextArea ascii;

  @FXML
  private HBox boxGrafico;

  @FXML
  public TextArea binario;

  @FXML
  private ToggleGroup grupo;

  @FXML
  private ToggleGroup grupo2;

  @FXML
  private ToggleGroup grupo3;

  @FXML
  private Button idEnviar;

  @FXML
  private RadioButton radioCrc;

  @FXML
  private RadioButton radioHamming;

  @FXML
  private RadioButton radioImpar;

  @FXML
  private RadioButton radioPar;

  @FXML
  private RadioButton radioBits;

  @FXML
  private RadioButton radioBytes;

  @FXML
  private RadioButton radioCamadaFisica;

  @FXML
  private RadioButton radioCaracteres;

  @FXML
  private RadioButton radioBinario;

  @FXML
  private RadioButton radioManche;

  @FXML
  private RadioButton radioMancheDif;

  @FXML
  public TextArea receptorText;

  @FXML
  private TextArea receptorText2;

  @FXML
  private TextArea transmissorText;

  @FXML
  private ImageView imageMarco;

  @FXML
  private ImageView enviarPhoto;

  @FXML
  private ImageView enviarPhoto1;

    @FXML
    private Slider sliderErro;


  private int tipoCodificacao;
  private int tipoDeEnquadramento;
  private int tipoDeControleDeErro;

  Transmissor transmissor;
  Comunicacao comunicacao;
  Receptor receptor;
  RadioButton radio;
  RadioButton radio2;
  RadioButton radio3;
  CamadaEnlaceTransmissora enlaceTransmissora;
  CamadaEnlaceReceptora enlaceReceptora;
  int nivelErro = 1;

 


  private void showCaixaAlerta() { // metodo da caixa de alerta texto vazio
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Aviso");
    alert.setHeaderText("Digite alguma coisa!");
    alert.setContentText("Para enviar uma mensagem voce deve digitar algo bacana primeiro :)   ");

    alert.showAndWait();
  }

  

  private void showCaixaAlertaEspecial() { // metodo da caixa de alerta texto vazio
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Aviso triste");
    alert.setHeaderText("tipo de enquadramento invalido");
    alert.setContentText("Nao consegui implementar o enquadramento por violacao da camada fisica :( ");

    alert.showAndWait();
  }

  @FXML
  void mudaCor(KeyEvent event) { // metodo para alterar a cor do botao de enviar mensagem
    enviarPhoto.setVisible(false);
    enviarPhoto1.setVisible(true);
  }

  @FXML
  void AplicacaoTransmissora(MouseEvent event) { // metodo referente as acoes tomadas apos clicar no botao enviar
                                                 // mensagem
    String nome = transmissorText.getText();
    if (nome.isEmpty()) { // verifica se o campo transmissorText esta vazio e realiza operacoes caso seja
                          // verdade
      enviarPhoto.setVisible(true); // 111 e 112 alteram a cor do botao de enviar mensagem
      enviarPhoto1.setVisible(false);
      showCaixaAlerta(); // chama a caixa de texto
    }

    else {
      enviarPhoto.setVisible(true); // 118 e 119 alteram a cor do botao de enviar mensagem
      enviarPhoto1.setVisible(false);
      tamanhoTexto(); // chamada do metodo de alteracoes em textAreas
      imageMarco.setVisible(true); // ativando foto do marco
      receptorText2.setText("voce: " + nome); // preenchendo o textarea receptor2
      radio = (RadioButton) grupo.getSelectedToggle();// 122 a 143 referentes as opcoes dos radios
      radio2 = (RadioButton) grupo2.getSelectedToggle();
      radio3 = (RadioButton) grupo3.getSelectedToggle();
      setTipoCodificacao(-1);
      setTipoEnquadramento(-1);
      setTipoDeControleDeErro(-1);

      if (radio.getText().equals(radioBinario.getText())) {
        setTipoCodificacao(0);
        limpaGrafico();

      }

      if (radio.getText().equals(radioManche.getText())) {
        setTipoCodificacao(1);
        limpaGrafico();

      }

      if (radio.getText().equals(radioMancheDif.getText())) {
        setTipoCodificacao(2);
        limpaGrafico();

      }

      if (radio2.getText().equals(radioCaracteres.getText())) {
        setTipoEnquadramento(0);
        System.out.println("0");
      }

      if (radio2.getText().equals(radioBytes.getText())) {
        setTipoEnquadramento(1);
        System.out.println("1");
      }

      if (radio2.getText().equals(radioBits.getText())) {
        setTipoEnquadramento(2);
        System.out.println("2");
      }

      if (radio2.getText().equals(radioCamadaFisica.getText())) {
        showCaixaAlertaEspecial();
        setTipoEnquadramento(3);
        System.out.println("3");

      }
      if (radio3.getText().equals(radioPar.getText())) {
        setTipoDeControleDeErro(0);
      }
      if (radio3.getText().equals(radioImpar.getText())) {
        setTipoDeControleDeErro(1);
      }
      if (radio3.getText().equals(radioCrc.getText())) {
        setTipoDeControleDeErro(2);
      }
      if (radio3.getText().equals(radioHamming.getText())) {
        setTipoDeControleDeErro(3);
      }
      transmissor.CamadaDeAplicacaoTransmissora(nome); // passa para a proxima camada do codigo

    }
    transmissorText.clear(); // limpa o textarea apos o envio da mensagem

  }

  public void tamanhoTexto() { // metodo de modificacao dos textarea
    receptorText.setStyle("-fx-control-inner-background: lightblue;"); // define a cor do textArea
    // Adicione um ouvinte para o evento de mudanca de texto
    receptorText.textProperty().addListener((observable, oldValue, newValue) -> {
      // Calcule o numero de linhas no TextArea
      int numLinhas = receptorText.getText().split("").length;

      if (numLinhas > 48) { // bloco para definir o tamanho do texto area
        receptorText.setPrefHeight(275);
      } else {
        // Ajuste a altura do TextArea com base no numero de linhas
        receptorText.setPrefHeight(numLinhas * 5);
      }
    });
    receptorText2.setStyle("-fx-control-inner-background: lightblue;"); // define a cor do textArea
    // Adicione um ouvinte para o evento de mudanca de texto
    receptorText2.textProperty().addListener((observable, oldValue, newValue) -> {
      // Calcule o numero de linhas no TextArea
      int numLinhas = receptorText2.getText().split("").length;

      // Ajuste a altura do TextArea com base no numero de linhas
      if (numLinhas > 48) { // bloco para definir o tamanho do texto area
        receptorText2.setPrefHeight(218);
      } else {
        receptorText2.setPrefHeight(numLinhas * 3);
      }
    });

  }

  public void limpaGrafico() { // metodo para limpar o grafico
    boxGrafico.getChildren().clear();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) { // metodo para definir requisitos na inicializacao do
                                                                   // programa

    this.transmissor = new Transmissor(this); // Passa a instaancia de receptor para transmissor

    imageMarco.setVisible(false); // define a imagem do marco como invisivel

    transmissorText.setWrapText(true); // Permite que o texto quebre automaticamente nas linhas

    // Define um ouvinte de eventos para o evento
    transmissorText.setOnKeyTyped(event -> {

      String texto = transmissorText.getText();
      int caretPosition = transmissorText.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    receptorText.setWrapText(true); // Permite que o texto quebre automaticamente nas linhas
    receptorText.setEditable(false); // Torna o TextArea somente leitura

    // Define um ouvinte de eventos para o evento
    receptorText.setOnKeyTyped(event -> {

      String texto = receptorText.getText();
      int caretPosition = receptorText.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    receptorText2.setWrapText(true); // Permite que o texto quebre automaticamente nas linhas
    receptorText2.setEditable(false); // Torna o TextArea somente leitura

    // Define um ouvinte de eventos para o evento
    receptorText2.setOnKeyTyped(event -> {

      String texto = receptorText2.getText();
      int caretPosition = receptorText2.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    ascii.setWrapText(true); // Permite que o texto quebre automaticamente nas linhas
    ascii.setEditable(false); // Torna o TextArea somente leitura

    // Define um ouvinte de eventos para o evento
    ascii.setOnKeyTyped(event -> {

      String texto = ascii.getText();
      int caretPosition = ascii.getCaretPosition();
      int linhaStart = texto.lastIndexOf('\n', caretPosition) + 1;
      int cumprimentoDaLinhaAtual = caretPosition - linhaStart;
    });

    binario.setWrapText(true); // Permite que o texto quebre automaticamente nas linhas
    binario.setEditable(false); // Torna o TextArea somente leitura

    // Define um ouvinte de eventos para o evento
    binario.setOnKeyTyped(event -> {

      String texto = binario.getText();
      int caretPosition = binario.getCaretPosition();
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
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
        //passando o novo valor para o atributo de velocidade do consumidor
        nivelErro = newValue.intValue();
      }
    });


  }

  private static void sinal(HBox display, int current, int prev) { // metodo responsavel pela criacao dos sinais da onda
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


}
