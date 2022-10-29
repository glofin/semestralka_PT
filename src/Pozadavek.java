
public class Pozadavek {
	
	double arrivalTime;
	int oaza;
	int basketCount;
	double deadline;
	
	/**
	 * @param arrivalTime	cas prichodu pozadavku
	 * @param oaza			oaza, do ktere se ma dorucovat
	 * @param basketCount	pocet kosu k doruceni
	 * @param dealine		deadline splneni pozadavku
	 */
	public Pozadavek(double arrivalTime, int oaza, int basketCount, double dealine) {
		this.arrivalTime = arrivalTime;
		this.oaza = oaza;
		this.basketCount = basketCount;
		this.deadline = dealine;
	}
}
