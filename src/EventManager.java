import java.util.Locale;
import java.util.PriorityQueue;

public class EventManager {
	
	/** Vrcholy grafu */
	static AbstractNode[] locations;
	/** Vsechny sklady */
	static Sklad[] sklady;
	/** Vsechny oazy */
	static Oaza[] oazy;
	/** Vsechny pozadavky (i nesplnene) */
	static Task[] tasks;
	/** Prioritni fronta nadchazejicich eventu serazena podle toho, kdy maji nastat */
	static PriorityQueue<Event> events = new PriorityQueue<Event>();
	
	
	public static void timeline() {
		
		events.add(new Event(100000, EventType.End, 0)); //automaticky zastavi program pokud prekroci cas 100 000
		
		while(true) {
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
					
				default:
					System.out.println("\nUkoncuji v case 100 000"); //TODO ve finalni verzi odstranit
					System.exit(0);						
			}
		}
	}

	private static void camelHome(Event e) {
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Navrat do skladu: %d",
								e.time,
								e.velbloud.name,
								e.index);
	}


	private static void camelTransit(Event e) {
		if(e.index >= sklady.length) {
			e.index -= sklady.length - 1;
			System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Oaza: %d, Kuk na velblouda",
								e.time,
								e.velbloud.name,
								e.index);
		}

	}


	private static void cameldrinks(Event e) {
		String s;
		if(e.index < sklady.length) {
			s = "Sklad";
		} else {
			s = "Oaza";
		}
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, %s: %d, Ziznivy %s, Pokracovani mozne v: %f",
								e.time,
								e.velbloud.name,
								s,
								e.index,
								e.velbloud.druh.name,
								e.time + e.velbloud.drinkTime);
		
	} 


	private static void camelFinished(Event e) {
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Oaza: %d, Vylozeno kosu: %d, Vylozeno v: %f, Casova rezerva: %f",
								e.time,
								e.velbloud.name,
								e.index,
								e.velbloud.task.basketCount,
								e.time + (0), //TODO
								e.velbloud.task.deadline - e.time);
		
	}


	private static void camelDeparted(Event e) {
		System.out.printf(Locale.US, "Cas: %f, Velbloud: %s, Sklad %d, Nalozeno kosu: %d, Odchod v %f\n",
								e.time,
								e.velbloud.name,
								e.index,
								e.velbloud.task.basketCount,
								e.time + (sklady[e.index].loadingTime * e.velbloud.task.basketCount));
	}

	private static void doTask(Event e) {
		Task t = tasks[e.index];
		System.out.printf(Locale.US, "Cas: %f, Pozadavek: %d, Oaza: %d, Pocet kosu: %d, Deadline: %f\n", 
								t.arrivalTime,
								e.index + 1,
								t.oaza,
								t.basketCount,
								t.deadline);
		//TODO zpracovat pozadavek t a vytvorit nove eventy
		
	}

	private static void reffilStorage(Event e) {
		sklady[e.index].makeBaskets();
		events.add(new Event(e.time + sklady[e.index].loadingTime, EventType.StorageRefill, e.index));
	}
	
	
	
	
	
	

}
