package model;

//Le principe de cette classe est de fournir des integer permettant de simuler des heures et des minutes
public class Time {
	public int hour;
	public int minutes;
	public boolean jour;
	
	public Time()
	{
		hour = 8;
		minutes = 0;
		jour = true;
	}

	public void updateTime()
	{
		minutes++;
		if(minutes > 59)
		{
			minutes = 0;
			hour++;
		}
		if(hour > 23)
		{
			hour = 0;
		}
		
		if(hour < 8)
		{
			jour = false;
		}
		else if(hour >= 8)
		{
			jour = true;
		}
	}
	
	public void affiche()
	{
		System.out.println(hour + " Heure " + minutes + " Minutes" + jour + " Jours ");
	}
}
