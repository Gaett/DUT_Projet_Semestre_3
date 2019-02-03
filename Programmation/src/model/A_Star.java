package model;

import java.util.ArrayList;
import java.util.LinkedList;

//Cette classe contient un algorithme similaire à A* permettant de trouver un chemin d'une case à une autre
public class A_Star {
	private LinkedList<Case> openList ;
	private LinkedList<Case> closedList ;
	private ArrayList<Case[]> from;
	private ArrayList<Case> wayBack;
	
	private Carte currentMap;
	
	private Case[] path;
	
	private boolean etat;
	
//Constructeur initialisant les attributs
	public A_Star(Carte c)
	{
		this.currentMap = c;
		openList = new LinkedList<Case>();
		closedList = new LinkedList<Case>();
		from  = new ArrayList<Case[]>();
		wayBack  = new ArrayList<Case>();
		etat = false;
	}
	
//Renvoi le chemin d'une case à une autre
	public Case[] getPath()
	{
		return path;
	}
	
//Algorithme similaire à A*
	public boolean algorithm(Case current,Case goal)
	{
		closedList.add(current);
		if( (current.getPositionCase()[0] == goal.getPositionCase()[0])&&(current.getPositionCase()[1] == goal.getPositionCase()[1])  )
		{
			openList.remove(current);
			return etat = true;
		}
		else if(current.isTraversable() == false)
		{
			openList.remove(current);
			return etat = false;
		}
		while(!etat)
		{
			for(Case c : this.currentMap.voisins(current))
			{
				if(!openList.contains(c) && !closedList.contains(c))
				{
					Case[] provisoire= {c,current};
					from.add(provisoire);
					openList.add(c);
				}
			}
			for(Case cas : openList)
			{
				if(!closedList.contains(cas) && openList.contains(cas))
				{
					etat = algorithm(cas,goal);
					if(etat)
						{
							break;
						}
						else
						{
							break;
						}
					}
				}
			}
		openList.remove(current);
		return etat;
	}

//Méthode remontant la liste des cases vues et validées
	public Case returnPreviousNeighbor(Case start, Case end)
	{
		Case result = start;
		for(Case[] c : from)
		{
			if(c[0]==end)
			{
				result = c[1];
			}
		}
		return result;
	}
	
//Méthode renvoyant les cases permettant d'aller d'une case à une autre
	public Case[] returnTo(Case start, Case end)
	{
		Case c = end;
		wayBack.add(c);
		while(! ( (c.getPositionCase()[0] == start.getPositionCase()[0])&&(c.getPositionCase()[1] == start.getPositionCase()[1]) ) )
		{
			c = returnPreviousNeighbor(start,c);
			wayBack.add(c);
		}
		
		path = new Case[wayBack.size()];
		wayBack.toArray(path);
		return path;
	}

//Méthode permettant d'effectuer tout les fonctionnalités précédente
	public void path(Case start, Case end)
	{
		algorithm(start,end);
		returnTo(start,end);
	}
	
}
	
	
