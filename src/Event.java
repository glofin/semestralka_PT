/**
 * Trida vyjadrujici udalost, ktere se nasledne vykonavaji
 * v EventManageru typy specifikovany v EventType
 */
public class Event implements Comparable<Event> {
	/** cas udalosti */
	double time;
	/** typ udalosti */
	EventType type;
	/** index vrcholu, tasku vice konstruktor*/
	int idInfo;
	/** velbloud jen pro typy zacinajici Camel*/
	Camel camel;
	
	/**
	 * 	 * Pouziva se pro EventType: CamelDeparting, CamelFinished, CamelDrinks, CamelTransit, CamelHome
	 *
	 * @param time		cas, kdy ma tato udalost nastat
	 * @param type		typ udalosti
	 * @param idInfo 	index vrcholu (nebo pozadavku), ke kteremu se event vztahuje
	 * @param camel	velbloud ke kteremu se event vztahuje
	 */
	public Event(double time, EventType type, int idInfo, Camel camel) {
		this.time = time;
		this.type = type;
		this.idInfo = idInfo;
		this.camel = camel;
	}

	/**
	 * Pouziva se pro EventType: StorageReffill, ErrorTask, StorageReffill, NewTask,
	 *
	 * @param time	cas, kdy ma tato udalost nastat
	 * @param type	typ udalosti EventType
	 * @param idInfo index vrcholu - EventType: StorageReffill, CamelDeparting, CamelFinished,
	 *             							   CamelDrinks, CamelTransit, CamelHome, ErrorTask
	 *               index Task - EventType: NewTask
	 *              		    - indexovane v Main stejne)
	 */
	public Event(double time, EventType type, int idInfo) {
		this.time = time;
		this.type = type;
		this.idInfo = idInfo;
		this.camel = null;
	}

	@Override
	public int compareTo(Event o) {
		//Nejprve podle casu
		if(o.time > this.time) {
			return -1;
		} else if(o.time < this.time) {
			return 1;
		}
		//pak podle typu (priorita typu je jejich poradi v Enumu)
		if(o.type != this.type) {
			return (this.type.ordinal() - o.type.ordinal());
		}
		
		//tasky s mensim indexem ze vstupniho souboru se zpracuji prvni
		if(o.type == EventType.NewTask && this.type == EventType.NewTask) {
			return this.idInfo - o.idInfo;
		}
		
		return 0;
	}

	@Override
	public String toString() {
		return "Event{" +
				"time=" + time +
				", type=" + type +
				", index=" + idInfo +
				", velbloud=" + camel +
				'}';
	}
}
