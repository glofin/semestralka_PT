import java.util.Locale;
import java.util.PriorityQueue;

/**
 * Trida se stara o nadchazejici udalosti casove linie
 * 
 * vzor: jedinacek
 */
public class EventManager {
	
	/** Pocet skladu */
	final int count;
	/** Vsechny pozadavky (i nesplnene) */
	final Task[] tasks;
	/** Prioritni fronta nadchazejicich eventu serazena podle toho, kdy maji nastat */
	final PriorityQueue<Event> events;
	
	public EventManager(PriorityQueue<Event> events, Task[] tasks, int count) {
		this.events = events;
		this.tasks = tasks;
		this.count = count;
	}
	
	public void nextEvent() {
		
		events.add(new Event(100000, EventType.End, 0)); //automaticky zastavi program pokud prekroci cas 100 000
		
		Event e = events.poll();
		switch(e.type) {
			case StorageRefill:
				reffilStorage(e);
				break;
			
			case NewTask:
				doTask(e);
				break;
				
			case CamelDeparting:
				camelDeparted(e);
				break;
					
			case CamelFinished:
				camelFinished(e);
				break;
					
			case CamelDrinks:
				cameldrinks(e);
				break;
					
			case CamelTransit:
				camelTransit(e);
				break;
				
			case CamelHome:
				camelHome(e);
				break;
					
			case End:
				System.out.println("\nUkoncuji v case 100 000"); //TODO ve finalni verzi odstranit
				
			default:
				System.exit(0);					
		}
	}

	private void camelHome(Event e) {
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Navrat do skladu: %d\n",
								e.time,
								e.velbloud.name,
								e.index + 1);
	}


	private void camelTransit(Event e) {
		if(e.index >= count) {
			System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Oaza: %d, Kuk na velblouda\n",
								e.time,
								e.velbloud.name,
								e.index - count - 1);
		}

	}


	private void cameldrinks(Event e) {
		String s;
		if(e.index < count) {
			s = "Sklad";
		} else {
			s = "Oaza";
			e.index -= count;
		}
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, %s: %d, Ziznivy %s, Pokracovani mozne v: %f\n",
								e.time,
								e.velbloud.name,
								s,
								e.index + 1,
								e.velbloud.druh.name,
								e.time + e.velbloud.drinkTime);
		
	} 


	private void camelFinished(Event e) {
		double finishedTime = e.time + (e.velbloud.task.basketCount * e.velbloud.home.loadingTime);
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Oaza: %d, Vylozeno kosu: %d, Vylozeno v: %f, Casova rezerva: %f\n",
								e.time,
								e.velbloud.name,
								e.index,
								e.velbloud.task.basketCount,
								finishedTime,
								e.velbloud.task.deadline - finishedTime);
		
	}


	private void camelDeparted(Event e) {
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Sklad %d, Nalozeno kosu: %d, Odchod v %f\n",
								e.time,
								e.velbloud.name,
								e.index,
								e.velbloud.task.basketCount,
								e.time + (((Sklad) Parser.graph.getNodebyId(e.index)).loadingTime * e.velbloud.task.basketCount));
	}

	private void doTask(Event e) {
		Task t = tasks[e.index];
		System.out.printf(Locale.US, "Cas: %f, Pozadavek: %d, Oaza: %d, Pocet kosu: %d, Deadline: %f\n", 
								t.arrivalTime,
								e.index + 1,
								tasks[e.index].oaza,
								t.basketCount,
								t.deadline);
		
		//TODO zpracovat pozadavek
	}

	private void reffilStorage(Event e) {
		Sklad refill = ((Sklad) Parser.graph.getNodebyId(e.index));
		refill.makeBaskets();
		events.add(new Event(e.time + refill.loadingTime, EventType.StorageRefill, e.index));
	}
	
	
	
	
	
	

}
