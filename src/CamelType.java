/**
 * Instance tridy {@code DruhVelblouda} predstavuji druhy velbloudu, ke kterym mohou prisluset jednotlivy velbloudi
 */
public class CamelType {
	
	final String name;
	/** minimalni rychlost*/
	final double minV;
	/** maximalni rychlost*/
	final double maxV;
	/** minimalni vzdalenost co ujde velbloud bez napiti*/
	final double minD;
	/** maximalni vzdalenost co ujde velbloud bez napiti*/
	final double maxD;
	/**jak dlouho velbloud pije*/
	final double drinkTime;
	/**maximum kosu co velbloud unese*/
	final int maxLoad;
	/** pomer vyskytu*/
	final double chance;
	
	/** Pocet vygenerovanych velbloudu tohoto druhu */
	int count;
	
	/**
	 * @param name		jmeno druhu
	 * @param minV		minimalni generovatelna rychlost
	 * @param maxV		maximalni generovatelna rychlost
	 * @param minD		minimalni generovatelna vzdalenost po napiti
	 * @param maxD		maximalni generovatelna rychlost
	 * @param drinkTime	cas piti
	 * @param maxLoad	maximalni pocet kosu, co tento druh unese
	 * @param chance	pomer vyskytu ve stadu oproti ostatnim druhum
	 */
	public CamelType(String name, double minV, double maxV,
					 double minD, double maxD, double drinkTime, int maxLoad, double chance) {
		
		this.name= name;
		this.minV = minV;
		this.maxV = maxV;
		this.minD = minD;
		this.maxD = maxD;
		this.drinkTime = drinkTime;
		this.maxLoad= maxLoad;
		this.chance = chance;
		count = 0;
		
	}

	public double getMaxDistance() {
		return maxD;
	}

	public double getMaxSpeed() {
		return maxV;
	}

	public int getMaxLoad() {
		return maxLoad;
	}

	@Override
	public String toString() {
		return name;
	}
}
