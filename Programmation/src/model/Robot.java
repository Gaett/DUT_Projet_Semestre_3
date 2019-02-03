package model;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.scene.image.Image;

public class Robot extends Observable implements Runnable, Observer { 
	private Image img;
	
	private int posX;
	private int posY;
	
	private Carte environnement;
	
	public static ExecutorService pool = Executors.newFixedThreadPool(20);
	
	/** Identifiant de robot */
	public static int idRobot = 0;
	
	/** booleen permettant de savoir si le robot a un but */
	public boolean goal = false;
	private boolean stop = false;
	
	/** L'identifiant du premier robot cree dans l'application sera 0 */
	private int id = 0;
	
	private boolean isMoving = false;
	private boolean notified = false;
	private boolean working = false;
	private boolean wantToCharge = false;
	
	private boolean paid = false;
	
	private int score;
	
	private Station station;
	
	private Tache currentTask;
	
	private RobotEquipe equipe;
	
	/** Attribut qui seront modifiables par les classes filles :
	 * speed
	 * battery
	 * cargo
	 * */
	
	/** La vitesse du robot modifie simplement la frequence d'appel de la methode seDeplacer */
	protected int speed;
	private int batteryCapacity;
	protected int battery;
	protected int cargo;
	
	private RobotEquipe team;
	
	/**
	 *  Constructeur de robot test
	 */
	public Robot( int x, int y, Carte envir)
	{
		this.environnement = envir;
		this.score = 0;
		this.speed = 500;
		this.battery = 2560;
		this.batteryCapacity = this.battery;
		this.posX = x;
		this.posY = y;
		this.id = idRobot;
		
		this.img = new Image("application/img/robot01-"+ (this.id+1) +".png");
		++idRobot;
	}
	
	public int getId() {
		return id;
	}
	
	public void addToTeam(RobotEquipe re)
	{
		this.team =re;
	}
	
	public RobotEquipe getTeam()
	{
		return this.team;
	}
	
	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	/**
	 *  renvoit la valeur de l'attribut image 
	 */ 
	public Image getImg() {
		return this.img;
	}
	
	public boolean hasGoal()
	{
		return goal;
	}
	
//Accesseurs et modificateurs des différents attributs.
	public void setScore(int ajout)
	{
		this.score += ajout;
	}
	
	public int getScore()
	{
		return this.score;
	}
	
	public void setImg(int toggle)
	{
		this.img = new Image("application/img/robot0" + toggle + "-"+ (this.id+1) +".png");
	}

//Renvoi la position du robot sous forme de tableau, avec pour indice les valeurs suivante:  0: X , 1: Y
	public int[] getPosition()
	{
		int[] res = {this.posX,this.posY};
		return res;
	}

//Renvoi le pourcentage de batterie du robot
	public int getCharge() {
		return (100*this.battery)/this.batteryCapacity;
	}
	
	/** Methode de base permettant une translation */
	public boolean seDeplacer(int x, int y)
	{
		this.posX = this.posX + (x);
		this.posY = this.posY + (y);
		this.battery --;
		setChanged();
		notifyObservers();
		return true;
	}
	
	/**
	 * Methode de base permettant d'orienter les déplacements
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean seDeplacerVers(int x, int y)
	{
		if(x < this.posX)
		{
			if(y < this.posY)
			{
				seDeplacer(-1,-1);
			}
			else if(y > this.posY)
			{
				seDeplacer(-1,1);
			}
			else
			{
				seDeplacer(-1,0);
			}
		}
		else if(x > this.posX)
		{
			if(y < this.posY)
			{
				seDeplacer(1,-1);
			}
			else if(y > this.posY)
			{
				seDeplacer(1,1);
			}
			else
			{
				seDeplacer(1,0);
			}
		}
		else
		{
			if(y < this.posY)
			{
				seDeplacer(0,-1);
			}
			else if(y > this.posY)
			{
				seDeplacer(0,1);
			}
			else
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Methode envoyant le robot à la case c
	 * @param Case c
	 */
	public synchronized void goCase(Case c)
	{
		if(!isMoving)
		{
			A_Star a = new A_Star(this.environnement);
			Case start = this.environnement.getCase(posX/64, posY/64);
			a.path(start, c);
			isMoving = true;
			try {
				for(int i = a.getPath().length-1; i >= 0; i--)
				{
					boolean current = false;
					while(!current)
					{
						Object obj = new Object();
						synchronized(obj)
						{
							current = seDeplacerVers(a.getPath()[i].getPositionCase()[0],a.getPath()[i].getPositionCase()[1]);
							obj.wait(25);
						}
					}
				}				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally
			{
				Robot.this.isMoving = false;
			}
		}
	}
	
	/**
	 * Cherche la station la plus proche à vol d'oiseau
	 * @return Case
	 */
	public Case optiCharge()
	{
		Case st = this.environnement.getStation()[0];
		int optiX = Math.abs(st.getPositionCase()[0] - this.posX);
		int optiY = Math.abs(st.getPositionCase()[1] - this.posY);
		int moyenne = (optiX+optiY)/2;
		
		for(int i = 0; i < this.environnement.getStation().length; i++)
		{
			Case a = this.environnement.getStation()[i];
			if(moyenne > (Math.abs(a.getPositionCase()[0] - this.posX)+ Math.abs(a.getPositionCase()[1] - this.posY))/2)
			{
				moyenne = (Math.abs(a.getPositionCase()[0] - this.posX)+ Math.abs(a.getPositionCase()[1] - this.posY))/2;
				st = this.environnement.getStation()[i];
			}
		}
		wantToCharge = true;
		return st;
	}
	
	/**
	 * redéfinition de la méthode update
	 */
	@Override
	public void update(Observable o, Object arg){
		Tache t = (Tache)arg;
		int path = (Math.abs(t.getRow() - posX/64) + Math.abs(t.getCol() - posY/64));
		
		if(path < 8 && !t.isAccepted() && !working)
		{
			t.setAccepted(true);
			notified = true;
			working = true;
			currentTask = t;
		}
	}
	
	/**
	 * Vérifie si le robot a besoin de se recharger
	 * @return boolean
	 */
	public boolean needCharge()
	{
		return ((100*this.battery)/this.batteryCapacity) < 40;
	}
	
	/**
	 * recharge le robot.
	 */
	public void recharger()
	{
		this.battery = this.batteryCapacity;
		this.wantToCharge = false;
	}
	
	@Override
	public String toString()
	{
		String res = "";
		if(this.battery*100/this.batteryCapacity > 2)
		{
			res = "Id Robot : " + (this.id+1) + " Score : " + this.score + "\n" + " Batterie : " + this.battery*100/this.batteryCapacity + "%";
		}
		else
		{
			res = "Id Robot : " + (this.id+1) + " Score : " + this.score + "\n" + " Batterie Critique";
		}
		return res;
	}
	
	public void stop()
	{
		this.stop = true;
		goal = false;
		isMoving = false;
		notified = false;
		working = false;
		wantToCharge = false;
	}

	@Override
	public void run() {
		Random r = new Random();
		boolean moving = false;
		while(!stop)
		{
			if((this.environnement.getCase(posX/64, posY/64) instanceof Commerce) || (this.environnement.getCase(posX/64, posY/64) instanceof Station))
			{
				this.environnement.getCase(posX/64, posY/64).setTraversable(true);
			}
			if(!needCharge())
			{
				try 
				{
					Thread.sleep(1000);
					moving = false;
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				
				if(!notified)
				{
						paid = false;
						Case[] voisinsTraversables = this.environnement.voisinsTraversables(this.environnement.getCase(posX/64, posY/64));
						int rand = 0;
						if(voisinsTraversables.length - 1 > 0)
						{
							rand = r.nextInt(voisinsTraversables.length);
						}
						goCase(voisinsTraversables[rand]);
				}
				else if(notified)
				{
					if(!this.needCharge()){
						goCase(this.environnement.getCase(currentTask.getRow(), currentTask.getCol()));
						moving = true;
						if(posX/64 == currentTask.getRow() && posY/64 == currentTask.getCol())
						{	
							try 
							{
								Thread.sleep(currentTask.getDuree_realisation());
							} 
							catch (InterruptedException e) 
							{
								e.printStackTrace();
							}
							if(currentTask != null)
							{
								notified = false;
								working = false;
								currentTask.setActive(false);
								currentTask.setAccepted(false);
								if(!paid)
								{
									this.setScore(currentTask.getRecompense());
									paid = true;
									currentTask = null;
								}
							}
						}
					}
				}
			}
			else 
			{
				this.station = (Station)optiCharge();
				goCase(station);
				moving = true;
				if(station.getPositionCase()[0] == posX  && station.getPositionCase()[1] == posY)
				{
					try 
					{
						Thread.sleep(10000);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					this.recharger();
				}
			}
		}
	}
	
}
