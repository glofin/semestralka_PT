
public class Task {
	double arrivalTime;
	int idOaza;
	int basketCount;
	double deadline;
	
	/**
	 * @param arrivalTime	cas prichodu pozadavku
	 * @param idOaza			oaza, do ktere se ma dorucovat
	 * @param basketCount	pocet kosu k doruceni
	 * @param dealine		deadline splneni pozadavku
	 */
	public Task(double arrivalTime, int idOaza, int basketCount, double dealine) {
		this.arrivalTime = arrivalTime;
		this.idOaza = idOaza;
		this.basketCount = basketCount;
		this.deadline = dealine;
	}
}
