
public class Velbloud {
	
	/** Druhy, ktere je mozne generovat */
 	private static DruhVelblouda[] druhy;
 	
 	/** Rychlost velblouda */
 	public double rychlost;
 	
 	/** Maximalni vzdalenost, kterou velbloud ujde po napiti */
 	private double maxUjde;
 	
 	/** Aktualni vzdalenost, kterou zvladne velbloud ujit pred napitim */
 	public double ujde;
 	
 	/** Doba, za kterou se velbloud napije */
 	public double casPiti;

 	
 	private Velbloud(double rych, double mUjde, double cPiti) {
 		rychlost = rych;
 		maxUjde = mUjde;
 		casPiti = cPiti;
 		ujde = mUjde;
 	}
 	
 	/**
 	 * Nahodne vygeneruje velblouda 
 	 * @return 	nove vygenerovany velbloud
 	 */
 	public static Velbloud generujVelblouda() {

 		DruhVelblouda druh = generujDruh();
			double rangeRychlost = druh.maxV - druh.minV;
			double rychlost = druh.maxV - (Math.random() * rangeRychlost);
			double rangePiti = druh.maxD - druh.minD;
			double mPiti = druh.maxD - (Math.random() * rangePiti);
			
			return new Velbloud(rychlost, mPiti, druh.casPiti);

 	}
 	
 	/**
 	 * Nahodne vybere druh velblouda (s ohledem na atribut pomer)
 	 * @return	nahodny druh velblouda
 	 */
 	private static DruhVelblouda generujDruh() {
 		double sance = 0;
 		for(int i = 0; i < druhy.length; i++) {
 			sance += druhy[i].pomer;
 			if(Math.random() <= sance) {
 				return druhy[i];
 			}
 		}
		return null;
	}

	/**
 	 *  Velbloud se napije a doplni si zasobu vody
 	 */
 	public void Napij() {
 		ujde = maxUjde;
 	}
 		
 	
	/**
	 * @param druhy 	Druhy velbloudu, ktere je mozne generovat
	 */
	public static void setDruhy(DruhVelblouda[] pole) {
		druhy = pole;
	}
	
}
