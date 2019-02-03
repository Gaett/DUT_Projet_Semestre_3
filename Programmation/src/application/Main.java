package application;
	

import java.io.File;
import java.util.Random;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
//	public static MediaPlayer mp;
//	public static Thread music;
	
	@Override
	public void start(Stage primaryStage) {
		try {
//Code pour mettre de la musique
//			 Main.music = new Thread( new Runnable() {
//				@Override
//				public void run() {
//					//Musique libre de droit sur https://www.bensound.com/royalty-free-music
//					String[] musics = new String[3];
//					musics[0] = getClass().getResource("/application/img/bensound-brazilsamba.mp3").toString();
//					musics[1] = getClass().getResource("/application/img/bensound-hipjazz.mp3").toString();
//					musics[2] = getClass().getResource("/application/img/bensound-funkyelement.mp3").toString();
//					Random r = new Random();
//					Media m = new Media(musics[r.nextInt(2)]);
//					Main.mp = new MediaPlayer(m);
//					Main.mp.play();
//				}
//			});
//			Main.music.start();
//Fin du code musique
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Interface.fxml"));
			Scene scene = new Scene(root,1000,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Simulation Smart City");
			primaryStage.getIcons().add(new Image("application/img/Smart_City.PNG"));
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
//Méthode appelée à la fermeture de l'application
	@Override
    public void stop() throws Exception {
       super.stop();
       InterfaceController.onStop();
//       if(music.isAlive())
//       {
//    	   music.interrupt();
//       }
       
    }

}
