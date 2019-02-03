package model;

import javafx.scene.image.Image;

//Classe représentant une case de la carte
public class Case {
	/** Identifiant de case */
	public static int idCases = 0;
	/** Attribut de classe definit par la taille des images PNG*/
	public static int imgSize = 64;
	
	/** Attribut d'instance */
	private boolean traversable;
	private Image image;
	
	/** L'identifiant de la premiere case creee dans l'application sera 0 */
	private int id = 0;
	
	private int posX = 0;
	private int posY = 0;
	
	/** Constructeur de la classe Case avec pour paramètre si la case est traversable par un robot ou non*/
	public Case(boolean t) 
	{
		this.traversable = t;
		this.image = new Image("application/img/1.png");
		this.id = idCases;
		++idCases;
	}
	
	/** renvoit la valeur de l'attribut traversable */ 
	public boolean isTraversable() 
	{
		return this.traversable;
	}
	
	/** modifie la valeur de l'attribut traversable */ 
	public void setTraversable(boolean t) 
	{
		this.traversable = t;
	}
	
	/** M�thode attribuant la position X, Y de la case*/
	public void setPositionsCase(int x, int y)
	{
		this.posX = x;
		this.posY = y;
	}
	
	/** M�thode renvoyant la position X, Y de la case*/
	public int[] getPositionCase()
	{
		int[] res = new int[2];
		res[0] = this.posX;
		res[1] = this.posY;	
		
		/** 0 renvoit le X, 1 renvoit le Y */
		return res;
	}
	
	public int getIndex()
	{
		return this.id;
	}
	
	/** renvoit la valeur de l'attribut image */ 
	public Image getImage() 
	{
		return this.image;
	}
	
	/** modifie la valeur de l'attribut image */ 
	public void setImage(String str) 
	{
		this.image = new Image(str);
	}
}
