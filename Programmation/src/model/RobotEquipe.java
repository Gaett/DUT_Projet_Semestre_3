package model;

import java.util.ArrayList;

public class RobotEquipe {

	/** attributs */
	private ArrayList<Robot> equipe;
	
	/**
	 * Constructeur 
	 * @param int n
	 */
	public RobotEquipe() {
		equipe = new ArrayList<Robot>();
	}

	/**
	 * getters et setters
	 */
	public int getScore() {
		int res = 0;
		for(Robot r : equipe)
		{
			if(r != null)
			{
				res += r.getScore();
			}
		}
		return res;
	}
	
	public ArrayList<Robot> getTeam()
	{
		return this.equipe;
	}
	
	public Robot getMember(int index)
	{
		return this.equipe.get(index);
	}
	
	/**
	 * Methode servant au calcul des robots 
	 * @param Robot r
	 */
	public void ajouterRobot(Robot r)
	{
		equipe.add(r);
		r.addToTeam(this);
	}
	
	
}
