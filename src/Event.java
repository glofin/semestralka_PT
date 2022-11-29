
public class Event implements Comparable<Event> {
	
	double time;
	EventType type;
	int index;
	Camel camel;
	
	/**
	 * 
	 * @param time		cas, kdy ma tato udalost nastat
	 * @param type		typ udalosti
	 * @param index 	index vrcholu (nebo pozadavku), ke kteremu se event vztahuje
	 * @param camel	velbloud ke kteremu se event vztahuje
	 */
	public Event(double time, EventType type, int index, Camel camel) {
		this.time = time;
		this.type = type;
		this.index = index;
		this.camel = camel;
	}
	
	public Event(double time, EventType type, int index) {
		this.time = time;
		this.type = type;
		this.index = index;
		this.camel = null;
	}

	@Override
	public int compareTo(Event o) {
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
		if(o.type == EventType.NewTask && this.type == EventType.NewTask) {
			return this.index - o.index;
		}
		
		return 0;
	}

	@Override
	public String toString() {
		return "Event{" +
				"time=" + time +
				", type=" + type +
				", index=" + index +
				", velbloud=" + camel +
				'}';
	}
}
