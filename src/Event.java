
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
	 * Pouziva se pro EventType: CamelDeparting, CamelFinished, CamelDrinks, CamelTransit, CamelHome
	 *
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
	public int compareTo(Event o) {//TODO dodelat aby fungovala priorityQueue
		if(o.time > this.time) {
			return -1;
		} else if(o.time < this.time) {
			return 1;
		}
		
		if(o.type == EventType.StorageRefill) {
			return 1;
		} else if(this.type == EventType.StorageRefill) {
			return -1;
		}
		
		if(o.type == EventType.NewTask) {
			return -1;
		} else if(this.type == EventType.NewTask) {
			return 1;
		}
		if(o.type == EventType.NewTask && this.type == EventType.NewTask) {//TODO always false
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
