package application;

import java.util.Random;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.Carte;
import model.CarteDefault;
import model.Case;
import model.Commerce;
import model.Habitation;
import model.Robot;
import model.RobotEquipe;
import model.Route;
import model.Station;
import model.Tache;
import model.TaskBoard;
import model.TaskManager;
import model.Time;

public class InterfaceController {
	//Ici se trouvent les objets FXML
	@FXML
	private Canvas mapDisplay;
	@FXML
	private Label pos;
	@FXML
	private Button startButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Button resetButton;
	@FXML
	private Button creerButton;
	@FXML
	private Button suppressButton;
	@FXML
	private Button chargeButton;
	@FXML
	private Button robotButton;
	@FXML
	private Button routeButton;
	@FXML
	private Button commerceButton;
	@FXML
	private Button habitationButton;
	@FXML
	private AnchorPane canvasScrollAnchorPane;
	@FXML
	private TextField size;
	@FXML
	private TextArea listRobots;
	@FXML
	private Label compteurTaches;
	
//Boolean permettant l'arrêt de toutes les fonctionnalités
	private static boolean stop = false;
	
//Threads pour le rendu graphique et les animations
	private static Thread renderThread;
	private static Thread animationThread;
	
//TaskManager pour gérer la notification des tâches
	private static TaskManager coach;
	
	
//Ici nous trouverons les attributs stockant les objets courrants de la carte
	private static boolean isLaunched = false;
	public static Carte map;
	public static Case[] shortMap;
	public static boolean jour;
	private static Time time;
	
//On limite le nombre de robots
	private static int robotLimit = 10;
	public static Robot[] robots;
	private static int robotCount = 0;
	
//Attribut pour la gestion des taches
	private static TaskBoard taskBoard;
	
//Cet attribut permet de definir quel type de case sera pose par la methode onMousePressed
	private static int selectedTileType = -1;
	
//Prennent la valeur de la case selectionnee la plus recente
	private static int currentX;
	private static int currentY;

	
//Score des équipes de robot
	private static RobotEquipe equipe1;
	private static RobotEquipe equipe2; 
	
//Methode affichant la position de la souris lorsqu'elle passe sur le canvas mapDisplay
	@FXML
    public void onMouseMoved(MouseEvent event) {
			posText("(" + (int)(event.getX()/64) + ", " + (int)(event.getY()/64) + ")");
    }

//Permet de poser des cases sur le canvas mapDisplay
	@FXML
    public void onMousePressedMap(MouseEvent event) {
		currentX = (int)(event.getX()/64);
		currentY = (int)(event.getY()/64);
        if(event.isPrimaryButtonDown()){
	//Switch case permettant de définir l'action du clic de la souris
        	switch (selectedTileType) {
        		case 0 : map.setCase(new Case(true), currentX, currentY);
        		break;
        		case 1 : map.setCase(new Station(10), currentX, currentY);
				break;
        		case 2 : map.setCase(new Route(), currentX, currentY);
        		break;
        		case 3 : map.setCase(new Commerce(), currentX, currentY);
        		break;
        		case 4 : map.setCase(new Habitation(), currentX, currentY);
        		break;
        		case 5 :
        			GraphicsContext gcr = mapDisplay.getGraphicsContext2D();
        			if(robotCount < robotLimit && map.getCase(currentX, currentY).isTraversable())
        			{
            			Robot.pool.execute(robots[robotCount] = new Robot((int)(event.getX()/64)*64,(int)(event.getY()/64)*64, map));
            			gcr.drawImage(robots[robotCount].getImg(), currentX*64, currentY*64);
            			coach.addObserver(robots[robotCount]);
            			if(robotCount % 2 == 0)
            			{
            				equipe1.ajouterRobot(robots[robotCount]);
            			}
            			else
            			{
            				equipe2.ajouterRobot(robots[robotCount]);
            			}
            			robotCount++;
            			majListRobots();
            		}
        			else if(robotCount < robotLimit)
        			{
        				Alert erreur = new Alert(Alert.AlertType.ERROR, "Un robot ne peut être placé que sur la route.");
        				erreur.setResizable(true);
        				erreur.getDialogPane().setMinHeight(175);
        		        erreur.showAndWait();
        			}
        			else
        			{
        				Alert erreur = new Alert(Alert.AlertType.ERROR, "Limite du nombre de robot atteinte.");
        				erreur.setResizable(true);
        		        erreur.showAndWait();
        			}
        			
				break;
        	}
        }
    }

//Permet de supprimer le contenu de la case
	@FXML
	public void supprimerContenu(MouseEvent envent)
	{
		selectedTileType = 0;
	}
	
	
//Permet de dessiner des stations
		@FXML
	    public void poserStation(MouseEvent event) {
			selectedTileType = 1;
	    }
		
//Permet de dessiner des routes
		@FXML
	    public void poserRoute(MouseEvent event) {
			selectedTileType = 2;
	    }
		
//Permet de dessiner des routes
		@FXML
	    public void poserCommerce(MouseEvent event) {
			selectedTileType = 3;
	    }
		
//Permet de dessiner des routes
		@FXML
	    public void poserHabitation(MouseEvent event) {
			selectedTileType = 4;
	    }
		
//methode permettant de deplacer le robot
		@FXML
		public void poserRobot(MouseEvent event){
			selectedTileType = 5;
		}
		
//Méthode d'initialisation du contenu de la map
	@FXML
	public void loadMap(){
		GraphicsContext gc = mapDisplay.getGraphicsContext2D();
		gc.clearRect(0, 0, Carte.colN*64, Carte.rowN*64);
		for(int i = 0; i < Carte.rowN; ++i) {
			for(int j = 0; j < Carte.colN; ++j) {
				map.getCase(i,j).setPositionsCase(i*Case.imgSize, j*Case.imgSize);
				gc.drawImage(map.getCase(i, j).getImage(), Case.imgSize*i , Case.imgSize*j);
			}
		}
	}
	
//Methode appelée pour charger le contenu du canvas
	@FXML
	public void loadTask()
	{
		Image img = Tache.getImg();
		GraphicsContext gc = mapDisplay.getGraphicsContext2D();
		gc.clearRect(0, 0, Carte.colN*64, Carte.rowN*64);
		for(Case c : shortMap)
		{
			gc.drawImage(c.getImage(), c.getPositionCase()[0] , c.getPositionCase()[1]);
		}
		
		
		for(Tache c : taskBoard.getActiveTask())
		{
			gc.drawImage(img, Case.imgSize*c.row , Case.imgSize*c.col);
		}
		
		for(int i = 0; i < robotCount; ++i) {
				gc.drawImage(robots[i].getImg(), robots[i].getPosition()[0]  , robots[i].getPosition()[1]);
		}
		
		Platform.runLater(() ->{
			this.setCompteurTaches(this.getNombreTaches());
			this.majListRobots();
		});
	}
	
//Methode permettant de changer l'affichage du label de position de la souris
    public void posText(String curseur){
    	pos.setText(curseur);
    }
	
//Methode permettant d'attribut une taille e un canvas passe en param
	public void setCanvasSize(Canvas c)
	{
		c.setHeight(Carte.colN*64);
		c.setWidth(Carte.rowN*64);
	}
	
//Methode permettant de retourner la valeur rentree dans le champ de saisie de taille de l'utilisateur
	@FXML
	public int getRequestedSize()
	{
		return Integer.parseInt(size.getText());
	}
	
//Methode permettant l'initialisation de tous les composants necessaire au fonctionnement de l'application
	@FXML
    public void lancerSimulation(MouseEvent event) {
		if(isLaunched == false && this.getRequestedSize() > 0) {
			isLaunched = true;			
			robotButton.setDisable(false);
			
			suppressButton.setDisable(true);
			routeButton.setDisable(true);
			commerceButton.setDisable(true);
			habitationButton.setDisable(true);
			chargeButton.setDisable(true);
						
			robots = new Robot[robotLimit];

			startButton.setDisable(true);

			startButton.setText("Démarrer ▶");
			
			size.setVisible(false);
			taskBoard = new TaskBoard(map);
			Thread t = new Thread(taskBoard); 
			t.start();
			
			coach = new TaskManager();
			taskBoard.addTaskObserver(coach);
			
			animationRefresh();
			animationThread.start();
		}
    }
	
//Methode permettant de générer la carte
	@FXML
	public void creerCarte(MouseEvent event){
		if(this.getRequestedSize() > 0) {
			CarteDefault cd = new CarteDefault(getRequestedSize());
			map = cd.map;
			time = new Time();
			
	//Si la map n'est pas null
			if(map != null){
				setCanvasSize(mapDisplay);
				loadMap();
				Alert erreur = new Alert(Alert.AlertType.INFORMATION, "Vous êtes en mode construction de l'environnement, vous pouvez éditer la carte, pour passer à l'étape suivant cliquez sur Start");
				erreur.setResizable(true);
				erreur.getDialogPane().setMinHeight(200);
				erreur.showAndWait();

				robotButton.setDisable(true);
				
				suppressButton.setDisable(false);
				routeButton.setDisable(false);
				commerceButton.setDisable(false);
				habitationButton.setDisable(false);
				chargeButton.setDisable(false);
				
				creerButton.setText("Recréer");
				
				robots = new Robot[robotLimit];
				
				equipe1 = new RobotEquipe();
				equipe2 = new RobotEquipe();
				
				startButton.setDisable(false);
				
				size.setVisible(false);
				
				if(getRequestedSize() <= 19)
				{
					refreshSmall();
					renderThread.start();
				}
				else
				{
					refreshTall();
					renderThread.start();
				}
			}
		}
	}
	
	
//Méthode permettant de creer la carte en appuyant sur le bouton entrée
	@FXML
	public void keyEnter(KeyEvent keyEvent){
		if(keyEvent.getCode() == KeyCode.ENTER){
			this.creerCarte(null);
		}
	}
	
//Méthode permettant de mettre en pause l'application
	/** Ne fonctionne pas entièrement */
	@FXML
    public void pauserSimulation(MouseEvent event) {
		taskBoard.stop();
		
		robotButton.setDisable(true);
		
		suppressButton.setDisable(false);
		routeButton.setDisable(false);
		commerceButton.setDisable(false);
		habitationButton.setDisable(false);
		chargeButton.setDisable(false);
		
		isLaunched = false;
		startButton.setDisable(false);
		
		startButton.setText("Reprendre");
		
		size.setVisible(true);
    }
	
//Méthode permettant de réinitialiser l'application
		/** Peut causer des erreurs */
	@FXML
    public void resetSimulation(MouseEvent event) {
		//Permet de pouvoir changer la taille de l'image
		taskBoard.stop();
		
		this.pauserSimulation(event);
		startButton.setText("Démarrer ▶");
		startButton.setDisable(true);
		creerButton.setText("Créer");
		compteurTaches.setText("0");
		listRobots.setText("");
		Robot.idRobot = 0;
		
		equipe1 = new RobotEquipe();
		equipe2 = new RobotEquipe();

		map = null;
		
		GraphicsContext gc = mapDisplay.getGraphicsContext2D();
		gc.clearRect(0, 0, Carte.colN*64, Carte.rowN*64);

		//Reinitialise le nombre de robot
		robotCount = 0;
		robots = new Robot[robotLimit];
    }
	
//Méthode mettant à jour la liste des robots et le score des équipe sur la droite de l'écran
	public void majListRobots(){
		String scoreEquipe1 = "Equipe 1 : Score : " + equipe1.getScore();
		for(Robot r : equipe1.getTeam())
		{
			scoreEquipe1 += " \n  \n  " + r;
		}
		String scoreEquipe2 = "\n \nEquipe 2 : Score : " + equipe2.getScore();
		for(Robot r : equipe2.getTeam())
		{
			scoreEquipe2 += " \n  \n  " + r;
		}
		this.listRobots.setText(scoreEquipe1 + scoreEquipe2);		
	}
	
//Méthode calculant le nombre de tâche inactives, non occupées
	public int getNombreTaches(){
		int res = 0;
		for(int i = 0; i < Carte.rowN; i++){
			for(int j = 0; j < Carte.colN; j++){
				if(taskBoard.getTache(i,j).isActive()){
					res++;
				}	
				for(int n = 0; n < robotCount; n++) {
					if(taskBoard.getTache(i, j).isActive() && robots[n].getPosition()[0]/64 == i && robots[n].getPosition()[1]/64 == j){
						res--;
					}
				}
			}
		}
		return res;
	}
	
//Méthode mettant à jour le nombre de tâche inactive, non occupée en bas à droite de l'écran
	public void setCompteurTaches(int nombreDeTaches){
		this.compteurTaches.setText("" + nombreDeTaches);
	}
	
//Methode permettant aux robots de traverser les commerces le moins possible
	public void resetCommerces()
	{
		for(int[] i: map.getCommerce())
		{
			map.getCase(i[0], i[1]).setTraversable(false);
		}
		for(Station s : map.getStation())
		{
			s.setTraversable(false);
		}
	}
	
//Méthode gérant le rafraichissement de l'affiche pour les cartes de taile <= 19
	@FXML
	public void refreshSmall()
	{
		renderThread = new Thread(new Runnable() {
		@Override
		public void run() {
			double transparency = 1.0;
			while(!stop) {
				try {
					Thread.sleep(40);
					resetCommerces();
					if(isLaunched)
					{	
						time.updateTime();
						GraphicsContext gc = mapDisplay.getGraphicsContext2D();
	
				        gc.setGlobalAlpha(1);
						loadTask();
//Début du block gestion des jours / des nuits	
						gc.setFill(javafx.scene.paint.Color.BLACK);
						if(!time.jour)
						{
							jour = false;
							if((time.hour > 4) && (time.hour < 9))
							{
								transparency = transparency - 0.01;
								if(transparency < 0)
								{
									transparency = 0;
								}
								gc.setGlobalAlpha(transparency);
								gc.fillRect(0, 0, map.rowN*64, map.colN*64);
							}
							else if((time.hour > -1) && (time.hour < 5)) 
							{
								transparency = transparency + 0.01;
								if(transparency > 0.15)
								{
									transparency = 0.15;
								}
								gc.setGlobalAlpha(transparency);
								gc.fillRect(0, 0, map.rowN*64, map.colN*64);
							}
						}
						else 
						{
							jour = true;
							transparency = 0;
						}
						if(time.hour == 23)
						{
							for(Case c : shortMap)
							{
								if(c instanceof Habitation)
								{
									c.setImage("application/img/HouseV2Night.png");
								}
								if(c instanceof Commerce)
								{
									c.setImage("application/img/ShopV2Night.png");
								}
							}
						}
						if(time.hour == 8)
						{
							
							for(Case c : shortMap)
							{
								if(c instanceof Habitation)
								{
									c.setImage("application/img/HouseV2.png");
								}
								if(c instanceof Commerce)
								{
									c.setImage("application/img/ShopV2.png");
								}
							}
						}
//Fin du block gestion des jours / des nuits						
					}
					else
					{
						loadMap();
						shortMap = map.convertMap();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		});
	}
	
//Méthode gérant le rafraichissement de l'affiche pour les cartes de taile == 20
	@FXML
	public void refreshTall()
	{
		renderThread = new Thread(new Runnable() {
		@Override
		public void run() {
			double transparency = 1.0;
			while(!stop) {
				try {
					Thread.sleep(150);
					resetCommerces();
					if(isLaunched)
					{	
						time.updateTime();
						GraphicsContext gc = mapDisplay.getGraphicsContext2D();
	
				        gc.setGlobalAlpha(1);
						loadTask();
						//Début du block gestion des jours / des nuits	
						gc.setFill(javafx.scene.paint.Color.BLACK);
						if(!time.jour)
						{
							jour = false;
							if((time.hour > 4) && (time.hour < 9))
							{
								transparency = transparency - 0.01;
								if(transparency < 0)
								{
									transparency = 0;
								}
								gc.setGlobalAlpha(transparency);
								gc.fillRect(0, 0, map.rowN*64, map.colN*64);
							}
							else if((time.hour > -1) && (time.hour < 5)) 
							{
								transparency = transparency + 0.01;
								if(transparency > 0.15)
								{
									transparency = 0.15;
								}
								gc.setGlobalAlpha(transparency);
								gc.fillRect(0, 0, map.rowN*64, map.colN*64);
							}
						}
						else 
						{
							jour = true;
							transparency = 0;
						}
						if(time.hour == 23)
						{
							for(Case c : shortMap)
							{
								if(c instanceof Habitation)
								{
									c.setImage("application/img/HouseV2Night.png");
								}
								if(c instanceof Commerce)
								{
									c.setImage("application/img/ShopV2Night.png");
								}
							}
						}
						if(time.hour == 8)
						{
							
							for(Case c : shortMap)
							{
								if(c instanceof Habitation)
								{
									c.setImage("application/img/HouseV2.png");
								}
								if(c instanceof Commerce)
								{
									c.setImage("application/img/ShopV2.png");
								}
							}
						}
//Fin du block gestion des jours / des nuits
					}
					else
					{
						loadMap();
						shortMap = map.convertMap();
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		});
	}
	
//Méthode permettant de changer les images des stations pour donner un effet de fonctionnement
	public void animation()
	{
		Random r = new Random();
		for(Station s : map.getStation())
		{
			int a = r.nextInt(2)+1;
			s.setImage("application/img/PowerPlant_0" + a +".png");
		}
	}
	
//Méthode permettant de changer les images des robots pour donner un effet de mouvements
	public void robotAnim()
	{
		Random r = new Random();
		for(Robot robot : robots)
		{
			if(robot != null)
			{
				robot.setImg(r.nextInt(2)+1);
			}
		}
	}
	
//Méthode appelant en continu les animations pour qu'elles s'affichent
	@FXML
	public void animationRefresh()
	{
		animationThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while(!stop) {
				try {
					Thread.sleep(500);
					animation();
					robotAnim();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		});
	}
	
//Méthode arrêtant toutes activités de l'application
	public static void onStop()
	{
		if(isLaunched)
		{
			if(robots != null)
			{
				for(Robot r : robots)
				{
					try
					{
						r.stop();
					}catch(Exception e)
					{
						
					}
				}
				Robot.pool.shutdown();
			}
			taskBoard.stop();
		}
		stop = !stop;
	}
	
}