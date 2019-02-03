package model;


import java.util.ArrayList;
import java.util.Observer;
import java.util.Random;

//Cette classe gère l'activation des tâches de manières aléatoire sur les commerces de la carte
public class TaskBoard implements Runnable{

	private Tache[][] taskBoard;
	private int[][] coordCommerce;
	private Carte carte;
	private boolean stop = true;

//Constructeur utilisant une carte
	public TaskBoard(Carte c)
	{
		this.carte = c;
		this.coordCommerce = c.getCommerce();
		taskBoard = new Tache[c.rowN][c.colN];
		for(int i = 0; i < c.rowN; ++i)
		{
			for(int j = 0; j < c.colN; ++j)
			{
				taskBoard[i][j] = new Tache(i,j);			
			}
		}
	}
	
//Méthode renvoyant une tâche précise de la carte selon ses indices dans le tableau 2 dimension
	public Tache getTache(int r, int c)
	{
		return this.taskBoard[r][c];
	}
	
//Méthode renvoyant toutes les tâches active de la carte
	public Tache[] getActiveTask()
	{
		ArrayList<Tache> actives = new ArrayList<Tache>();
		for(int i = 0; i < this.carte.rowN; ++i)
		{
			for(int j = 0; j < this.carte.colN; ++j)
			{
				if(this.taskBoard[i][j].isActive())
				{
					actives.add(this.taskBoard[i][j]);
				}
			}
		}
		Tache[] tacheActives = new Tache[actives.size()];
		actives.toArray(tacheActives);
		return tacheActives;
	}
	
//Méthode ajoutant le même observer à toutes les tâches
	public void addTaskObserver(Observer o)
	{
		for(int i = 0; i < this.carte.rowN; ++i)
		{
			for(int j = 0; j < this.carte.colN; ++j)
			{
				this.taskBoard[i][j].addObserver(o);		
			}
		}
	}

//Méthode permettant l'activation continue des tâches selon un chronomètre de 5secondes
	@Override
	public void run() {
		Random rand = new Random();
		while(this.stop)
		{
			try {
				int i = rand.nextInt(this.coordCommerce.length);
				taskBoard[this.coordCommerce[i][0]][this.coordCommerce[i][1]].setTask();
				Thread t = new Thread (taskBoard[this.coordCommerce[i][0]][this.coordCommerce[i][1]]);
				t.start();
				Thread.sleep(5000);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally
			{
				if(!this.stop)
				{
					try {
						this.finalize();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void stop()
	{
		this.stop = false;
	}
	
	
	
}
