package model;

import javafx.scene.control.Alert;

public class CarteDefault {
// Permet la création de carte selon une génération pseudo aléatoire
	public Carte map;
	
	public CarteDefault(int size)
	{
		if(size <= 20){
		map = new Carte(size, size);
			if(size > 6) {
				for(int i = 1; i < (size-1); ++i)
				{
					for(int j = 1; j < (size-1); ++j)
					{
						map.setCase(new Route(), i, j);
						if(i < (size-2) && j < (size-2) && i%2 == 0 && j%2 == 0)
						{
							if(((i - 2)%5 == 0 || i == 2) && ((j - 2)%5 == 0 || j == 2)){
								map.setCase(new Station(100), i, j);
							}
							else{
								map.setCase(new Habitation(), i, j);
							}
						}
					}
				}
				int k = 0;
				while(k < (((size / 3 )-2)*((size / 3 )-2))) {
					int x = (int)(Math.random() * size);
					int y = (int)(Math.random() * size);
					if(x >= 2 && x < (size-2) && y >= 2 && y < (size-2) && (x%2 != 0 || y%2 != 0)) {
						map.setCase( new Commerce(), x, y);
						k++;
					}
				}	
			}
		}
		else
		{
			Alert erreur = new Alert(Alert.AlertType.ERROR, "La taille de la carte ne peut pas être supérieur à 20.");
			erreur.setResizable(true);
			erreur.getDialogPane().setMinHeight(175);
	        erreur.showAndWait();
		}
	}
}