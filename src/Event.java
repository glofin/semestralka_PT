
public class Event implements Comparable<Event> {
	
	double time;
	EventType type;
	int index;
	
	/**
	 * 
	 * @param time	cas, kdy ma tato udalost nastat
	 * @param type	typ udalosti
	 */
	public Event(double time, EventType type, int index) {
		this.time = time;
		this.type = type;
		this.index = index;
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
