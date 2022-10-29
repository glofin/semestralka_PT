/**
 * Instance tridy {@code DruhVelblouda} predstavuji druhy velbloudu, ke kterym mohou prisluset jednotlivy velbloudi
 */
public class DruhVelblouda {
	
	final String name;
	final double minV;
	final double maxV;
	final double minD;
	final double maxD;
	final double drinkTime;
	final int maxLoad;
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
	public DruhVelblouda(String name, double minV, double maxV,
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

}
