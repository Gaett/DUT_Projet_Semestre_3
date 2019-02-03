package model;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Classe permettant d'envoyer à tout les robots l'activation d'une tâche que sera passée en paramètre
public class TaskManager extends Observable implements Observer {

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof Tache)
		{
			Tache t = (Tache)o;
			if(t.getDuree_actif() > 1000)
			{
				setChanged();
				notifyObservers(t);
			}
		}
	}

}
