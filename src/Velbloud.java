
public class Velbloud {
	
	/** Druhy, ktere je mozne generovat */
 	private static DruhVelblouda[] druhy;
 	
 	/** Rychlost velblouda */
 	public double rychlost;
 	
 	/** Maximalni vzdalenost, kterou velbloud ujde po napiti */
 	private double maxPiti;
 	
 	/** Aktualni vzdalenost, kterou zvladne velbloud ujit pred napitim */
 	public double piti;
 	
 	/** Doba, za kterou se velbloud napije */
 	public double casPiti;

 	
 	private Velbloud(double rych, double mPiti, double cPiti) {
 		rychlost = rych;
 		maxPiti = mPiti;
 		casPiti = cPiti;
 		piti = mPiti;
 	}
 	
 	/**
 	 * Vygeneruje noveho velblouda s nahodnym druhem
 	 * @return 	nove vygenerovany velbloud
 	 */
 	public Velbloud generujVelblouda() {
 		//TODO
 		return new Velbloud(1,1,1);
 	}
 	
 	/**
 	 *  Velbloud se napije a doplni si zasobu vody
 	 */
 	public void Napij() {
 		piti = maxPiti;
 	}
 		
 	
	/**
	 * @param druhy 	Druhy velbloudu, ktere je mozne generovat
	 */
	public static void setDruhy(DruhVelblouda[] pole) {
		druhy = pole;
	}
	
}
