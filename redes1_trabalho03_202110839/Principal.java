/* ***************************************************************
* Autor............: Yago Oliveira Silva
* Matricula........: 202110839
* Inicio...........: 29/08/2023
* Ultima alteracao.: 29/08/2023
* Nome.............: Principal.java
* Funcao...........: Eh Responsavel pela inicializacao do programa. O programa eh, em suma, a aplicacao 
basica da camada fisica de redes em java. 
*************************************************************** */

import controle.PrincipalController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Principal extends Application {
  public static void main(String[] args) throws Exception {
    launch(args);
    
  }

   private void showCaixaAlertaConstrucao() { // metodo da caixa de alerta texto vazio
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Ops! :(");
    alert.setHeaderText("Estamos em obras!");
    alert.setContentText("Devido a proporcao, nem todos os dados estao sendo exibidos " +
    "na tela, entretanto, estamos trabalhando para melhorar nosso servico. Por enquanto voce pode " + 
    "visualizar alguns de nossos dados no prompt de comando. obrigado pela compreensao! ");

    alert.showAndWait(); 
                        
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    showCaixaAlertaConstrucao();
    PrincipalController control = new PrincipalController();
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/visao/LayoutPrincipal.fxml")); // Carrega a tela
                                                                                                   // fxml
    Parent root = fxmlLoader.load(); // Carrega a cena com a tela fxml
    Scene tela = new Scene(root); // Carrega a tela com o root

    primaryStage.resizableProperty().setValue(Boolean.FALSE); // Nao permite que o usuario altere o tamanho da tela
     primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("visao/imagens/logo.png")));
    // //Adiciona um icone ao programa
    primaryStage.setTitle("Single Mensage Exchange"); // Nomeia o programa
    primaryStage.setScene(tela); // Carrega o Stage com a tela
    primaryStage.show(); // Exibe o programa
  }
}
