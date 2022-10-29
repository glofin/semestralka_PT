/**
 * Instance tridy {@code Velbloud} predstavuji jednotlive velbloudy
 */
public class Velbloud implements Comparable<Velbloud> {
	
	/** Druhy, ktere je mozne generovat */
 	private static DruhVelblouda[] druhy;
 	
 	/** Rychlost velblouda */
 	public final double speed;
 	
 	/** Maximalni vzdalenost, kterou velbloud ujde po napiti */
 	private final double maxDistance;
 	
 	/** Doba, za kterou se velbloud napije */
 	public final double drinkTime;
 	
 	/** Aktualni vzdalenost, kterou zvladne velbloud ujit pred napitim */
 	public double distance;

 	
 	private Velbloud(double speed, double mDistance, double drink) {
 		this.speed = speed;
 		maxDistance = mDistance;
 		drinkTime = drink;
 		distance = mDistance;
 	}
 	
 	/**
 	 * Nahodne vygeneruje velblouda 
 	 * @return 	nove vygenerovany velbloud
 	 */
 	public static Velbloud generujVelblouda() {

 		DruhVelblouda druh = generujDruh();
		double rangeSpeed = druh.maxV - druh.minV;
		double speed = druh.maxV - (Math.random() * rangeSpeed);
		double rangeDistance = druh.maxD - druh.minD;
		double mDistance = druh.maxD - (Math.random() * rangeDistance);
		
		return new Velbloud(speed, mDistance, druh.drinkTime);
 	}
 	
 	/**
 	 * Nahodne vybere druh velblouda (s ohledem na atribut pomer)
 	 * @return	nahodny druh velblouda
 	 */
 	private static DruhVelblouda generujDruh() {
 		double sance = 0;
 		double random = Math.random();
 		
 		for(int i = 0; i < druhy.length; i++) {
 			
 			sance += druhy[i].chance;
 			
 			if(random <= sance) {
 				return druhy[i];
 			}
 		}
		return null;
	}

	/**
 	 *  Velbloud se napije a doplni si zasobu vody
 	 */
 	public void Drink() {
 		distance = maxDistance;
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
		return (int) (o.maxDistance - this.maxDistance);
	}
	
}
