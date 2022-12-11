/**
 * Instance tridy {@code Camel} predstavuji jednotlive velbloudy
 */
public class Camel implements Comparable<Camel> {
	
	/** Druhy, ktere je mozne generovat */
 	private static CamelType[] types;
 	
 	/** Druh, ke kteremu velbloud nalezi */
 	public final CamelType type;
 	
 	/** Jmeno konkretniho velbloda */
 	public final String name;
 	
 	/** index domovskeho skladu */
 	public final Stock home;
 	
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


 	private Camel(double spd, double mDistance, CamelType drh, Stock skld) {

 		type = drh;
 		type.count++;
 		name = type.name + "_" + type.count;
		//System.out.println("Novy velbloud " + name);
 		speed = spd;
 		maxDistance = mDistance;
 		drinkTime = type.drinkTime;
 		distance = mDistance;
 		home = skld;
 	}
 	
 	/**
 	 * Nahodne vygeneruje velblouda a prida ho do mnoziny velbloudu, ve skladu na urcenem indexu
 	 * @param stock 	sklad, do ktereho bude velbloud prirazen
 	 * @return 			nove vygenerovany velbloud
 	 */
 	public static Camel generateCamel(Stock stock) {

 		CamelType type = generateType();
		 assert type != null;
		double rangeSpeed = type.maxV - type.minV;
		double speed = type.maxV - (Math.random() * rangeSpeed);
		double rangeDistance = type.maxD - type.minD;
		double mDistance = type.maxD - (Math.random() * rangeDistance);
		
		Camel v = new Camel(speed, mDistance, type, stock);
		stock.getCamelSet().add(v);
		return v;
 	}
 	
 	/**
 	 * Nahodne vybere druh velblouda (s ohledem na atribut pomer)
 	 * @return	nahodny druh velblouda
 	 */
 	private static CamelType generateType() {
 		double chance = 0;
 		double random = Math.random();
 		
 		for(int i = 0; i < types.length; i++) {

 			chance += types[i].chance;

 			if(random <= chance) {
 				return types[i];
 			}
 		}
		return null;
	}

	/**
 	 *  Velbloud se napije a doplni si zasobu vody
 	 */
 	public void drink() {//TODO neni vyuzita ale asi by mela
 		distance = maxDistance;
 	}
 
	/**
	 * @param array 	Druhy velbloudu, ktere je mozne generovat
	 */
	public static void setTypes(CamelType[] array) {
		types = array;

		double maxDistance = -1;
		double maxSpeed = -1;

		for (CamelType druh :
				array) {
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

	public static double getTypeMaxDistance() {
		return druhMaxDistance;
	}

	public static double getTypeMaxSpeed() {
		return druhMaxSpeed;
	}

	public double getSpeed() {
		return speed;
	}

	/**
	 * Vrati kladne cilso pokud aktualni velbloud ujde vice nez velboud v parametru
	 */
	@Override
	public int compareTo(Camel o) {
		if(o.maxDistance > this.maxDistance) {
			return 1;
		} else if (o.maxDistance < this.maxDistance) {
			return -1;
		}
		if(super.equals(o)) {
			return 0;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "Velbloud {" +
				"typ=" + type.toString() +
				", jmeno='" + name + '\'' +
				", rychlost=" + speed +
				", vzdalenost=" + distance +
				'}';
	}
}
