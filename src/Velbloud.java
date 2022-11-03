/**
 * Instance tridy {@code Velbloud} predstavuji jednotlive velbloudy
 */
public class Velbloud implements Comparable<Velbloud> {
	
	/** Druhy, ktere je mozne generovat */
 	private static DruhVelblouda[] druhy;
 	
 	/** Druh, ke kteremu velbloud nalezi */
 	public final DruhVelblouda druh;
 	
 	/** Jmeno konkretniho velbloda */
 	public final String name;
 	
 	/** index domovskeho skladu */
 	public final Sklad home;
 	
 	/** Rychlost velblouda */
 	public final double speed;
 	
 	/** Maximalni vzdalenost, kterou velbloud ujde po napiti */
 	private final double maxDistance;
 	
 	/** Doba, za kterou se velbloud napije */
 	public final double drinkTime;
 	
 	/** Aktualni vzdalenost, kterou zvladne velbloud ujit pred napitim */
 	public double distance;
 	
 	/** Aktualni pozadavek, ktery tento velbloud vykonava */
 	public Task task = null;

	 private static double druhMaxDistance;

	 private static double druhMaxSpeed;


 	private Velbloud(double spd, double mDistance, DruhVelblouda drh, Sklad skld) {
 		druh = drh;
 		druh.count++;
 		name = druh.name + "_" + druh.count;
 		speed = spd;
 		maxDistance = mDistance;
 		drinkTime = druh.drinkTime;
 		distance = mDistance;
 		home = skld;
 	}
 	
 	/**
 	 * Nahodne vygeneruje velblouda a prida ho do mnoziny velbloudu, ve skladu na urcenem indexu
 	 * @param sklad 	sklad, do ktereho bude velbloud prirazen
 	 * @return 			nove vygenerovany velbloud
 	 */
 	public static Velbloud generujVelblouda(Sklad sklad) {

 		DruhVelblouda druh = generujDruh();
		double rangeSpeed = druh.maxV - druh.minV;
		double speed = druh.maxV - (Math.random() * rangeSpeed);
		double rangeDistance = druh.maxD - druh.minD;
		double mDistance = druh.maxD - (Math.random() * rangeDistance);
		
		Velbloud v = new Velbloud(speed, mDistance, druh, sklad);
		sklad.set.add(v);
		return v;
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
	 * @param pole 	Druhy velbloudu, ktere je mozne generovat
	 */
	public static void setDruhy(DruhVelblouda[] pole) {
		druhy = pole;

		double maxDistance = -1;
		double maxSpeed = -1;

		for (DruhVelblouda druh :
				pole) {
			maxDistance = Math.max(druh.getMaxDistance(),maxDistance);
			maxSpeed = Math.max(druh.getMaxSpeed(),maxSpeed);
		}
		druhMaxDistance = maxDistance;
		druhMaxSpeed = maxSpeed;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public static double getDruhMaxDistance() {
		return druhMaxDistance;
	}

	public static double getDruhMaxSpeed() {
		return druhMaxSpeed;
	}

	public double getSpeed() {
		return speed;
	}

	/**
	 * Vrati kladne cilso pokud aktualni velbloud ujde vice nez velboud v parametru
	 */
	@Override
	public int compareTo(Velbloud o) {
		return (int) (o.maxDistance - this.maxDistance);
	}
	
}
