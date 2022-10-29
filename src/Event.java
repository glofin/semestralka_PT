
public class Event implements Comparable<Event> {
	
	double time;
	EventType type;
	int index;
	Velbloud velbloud;
	
	/**
	 * 
	 * @param time		cas, kdy ma tato udalost nastat
	 * @param type		typ udalosti
	 * @param index 	index vrcholu (nebo pozadavku), ke kteremu se event vztahuje
	 * @param velbloud	velbloud ke kteremu se event vztahuje
	 */
	public Event(double time, EventType type, int index, Velbloud velbloud) {
		this.time = time;
		this.type = type;
		this.index = index;
		this.velbloud = velbloud;
	}
	public Event(double time, EventType type, int index) {
		this.time = time;
		this.type = type;
		this.index = index;
		this.velbloud = null;
	}

	@Override
	public int compareTo(Event o) {
		if(o.time > this.time) {
			return -1;
		} else if(o.time < this.time) {
			return 1;
		}
		//TODO priorita pokud jsou ve stejnem case
		return 0;
	}
	
	
}
