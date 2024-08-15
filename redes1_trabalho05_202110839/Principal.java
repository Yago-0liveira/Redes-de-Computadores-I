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
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Principal extends Application {
  public static void main(String[] args) throws Exception {
    launch(args);
    
  }



  @Override
  public void start(Stage primaryStage) throws Exception {
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
