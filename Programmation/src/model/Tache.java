package model;

import java.util.Observable;
import java.util.Random;

import javafx.scene.image.Image;

//Classe Tâche, représentant une action effectuable par un robot
public class Tache extends Observable implements Runnable{
	
	private static Image img = new Image("/application/img/Quest.png");
	public static Image getImg() {
		return img;
	}
	
	public int row;
	public int col;
	public String sujet;
	public long duree_actif;
	public long duree_realisation;
	public boolean active = false;
	public boolean accepted = false;
	public int recompense = 0;  
	public boolean isRobotArrived = false;
	
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getSujet() {
		return sujet;
	}

	public void setSujet(String sujet) {
		this.sujet = sujet;
	}

	public long getDuree_actif() {
		return duree_actif;
	}

	public void setDuree_actif(long duree_actif) {
		this.duree_actif = duree_actif;
	}

	public long getDuree_realisation() {
		return duree_realisation;
	}

	public void setDuree_realisation(long duree_realisation) {
		this.duree_realisation = duree_realisation;
	}

	public int getRecompense() {
		return recompense;
	}

	public void setRecompense(int recompense) {
		this.recompense = recompense;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public boolean isRobotArrived() {
		return isRobotArrived;
	}


	
	public enum TaskType
	{
		TYPE_1(1,2000),
		TYPE_2(2,4000),
		TYPE_3(3,6000),
		TYPE_4(4,8000);
		
		public int rec;
		public long duree_real;
		
		private TaskType(int re, long duree)
		{
			this.rec = re;
			this.duree_real = duree;
		}
	}
	
	public Tache(int row, int col) {
		this.row = row;
		this.col = col;
		this.duree_actif = 5000;
	}
	
	public void setTask()
	{
		this.duree_actif = 5000;
		int choice = new Random().nextInt(TaskType.values().length);
		this.duree_realisation = TaskType.values()[choice].duree_real;
		this.recompense = TaskType.values()[choice].rec;
		this.sujet = TaskType.values()[choice].name();
	}
	
	public void setActive(boolean t)
	{
		this.active = t;
	}
	
	public void setRobotArrived(boolean t)
	{
		this.isRobotArrived = t;
	}
	
	public void setAccepted(boolean t)
	{
		this.accepted = t;
	}
	
	@Override
	public void run()
	{
		long currentUse = this.duree_realisation;
		int a = 1;
		if(!active)
		{
			this.active = true;
			while(this.active)
			{

//D�but du block non accept�e
				if(!this.accepted)
				{
					try {
						this.setChanged();
						this.notifyObservers();
						Thread.sleep(1000);
						this.duree_actif = this.duree_actif - 1000;
						if(this.duree_actif == 0)
						{
							try {
								this.active = false;
								this.finalize();
							} catch (Throwable e) {

								e.printStackTrace();
							}
						}
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
//Fin du block non accept�e
//D�but du block accept�
				else if(this.accepted && this.isRobotArrived)
				{
					while(currentUse > 0)
					{
						try {
							Thread.sleep(1000);
							currentUse = currentUse - 1000;
							if(currentUse <= 0)
							{
								this.active = false;
								this.accepted = false;
							}
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						finally
						{
							try {
								this.finalize();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}
				}
				else
				{
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//Fin du block accept�
			}
		}
	}
	
	
}
