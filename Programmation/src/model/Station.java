package model;

public class Station extends Case {
	private int nombrePrises;
	private int prisesOccupees;

	/** Ici le constructeur initialise la station traversable n prises */
	/** Système d'occupation des prises est prévu mais non effecuté */
	public Station(int n)
	{
		super(false);
		super.setImage("application/img/PowerPlant_01.png");
		nombrePrises = n;
		prisesOccupees = 0;
	}
}
