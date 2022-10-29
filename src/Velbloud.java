/**
 * Instance tridy {@code Velbloud} predstavuji jednotlive velbloudy
 */
public class Velbloud implements Comparable<Velbloud> {
	
	/** Druhy, ktere je mozne generovat */
 	private static DruhVelblouda[] druhy;
 	
 	/** Rychlost velblouda */
 	public final double rychlost;
 	
 	/** Maximalni vzdalenost, kterou velbloud ujde po napiti */
 	private final double maxUjde;
 	
 	/** Doba, za kterou se velbloud napije */
 	public final double casPiti;
 	
 	/** Aktualni vzdalenost, kterou zvladne velbloud ujit pred napitim */
 	public double ujde;

 	
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
 		double random = Math.random();
 		
 		for(int i = 0; i < druhy.length; i++) {
 			
 			sance += druhy[i].pomer;
 			
 			if(random <= sance) {
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

	/**
	 * Vrati kladne cilso pokud aktualni velbloud ujde vice nez velboud v parametru
	 */
	@Override
	public int compareTo(Velbloud o) {
		return (int) (o.maxUjde - this.maxUjde);
	}
	
}
