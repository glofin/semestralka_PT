/**
 * Trida typu prepravka, jednotlive instance odpovidaji jednotlivym pozadavkum behem simulace
 */
public class Task {
	public final double arrivalTime;
	public final int idOaza;
	public final int basketCount;
	public final double deadline;
	
	/** Cas, kdy byl pozadavek splnen */
	public double finishTime;
	
	/** velbloud, ktery pozadavek splnil */
	public Camel finishCamel;
	
	/**
	 * @param arrivalTime	cas prichodu pozadavku
	 * @param idOaza		oaza, do ktere se ma dorucovat
	 * @param basketCount	pocet kosu k doruceni
	 * @param dealine		deadline splneni pozadavku
	 */
	public Task(double arrivalTime, int idOaza, int basketCount, double dealine) {
		this.arrivalTime = arrivalTime;
		this.idOaza = idOaza;
		this.basketCount = basketCount;
		this.deadline = dealine;
	}

	@Override
	public String toString() {
		return  "Pozadavek - " +
				"cas zadani:" + arrivalTime +
				", idOaza: " + idOaza +
				", pocet kosu: " + basketCount +
				", deadline cas: " + deadline;
	}
}
