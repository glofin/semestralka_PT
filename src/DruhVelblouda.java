/**
 * Instance tridy {@code DruhVelblouda} predstavuji druhy velbloudu, ke kterym mohou prisluset jednotlivy velbloudi
 */
public class DruhVelblouda {
	
	String jmeno;
	double minV;
	double maxV;
	double minD;
	double maxD;
	int casPiti;
	int maxLoad;
	double pomer;
	
	/**
	 * Vytvori novy druh velblouda
	 * 
	 * @param jmeno		jmeno
	 * @param minV		minimalni generovatelna rychlost
	 * @param maxV		maximalni generovatelna rychlost
	 * @param minD		minimalni generovatelna vzdalenost po napiti
	 * @param maxD		maximalni generovatelna rychlost
	 * @param casPiti	cas piti
	 * @param maxLoad	maximalni zatez
	 * @param pomer		pomer vyskytu ve stadu oproti ostatnim druhum
	 */
	public DruhVelblouda(String jmeno, double minV, double maxV,
				double minD, double maxD, int casPiti, int maxLoad, double pomer) {
		
		this.jmeno= jmeno;
		this.minV = minV;
		this.maxV = maxV;
		this.minD = minD;
		this.maxD = maxD;
		this.casPiti = casPiti;
		this.maxLoad= maxLoad;
		this.pomer = pomer;
		
	}

}
