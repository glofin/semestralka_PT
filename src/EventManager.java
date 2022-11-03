import java.util.*;

/**
 * Trida se stara o nadchazejici udalosti casove linie
 * 
 * vzor: jedinacek
 */
public class EventManager {//TODO PRIORITA ERROR stav
	
	/** Pocet skladu */
	final int count;
	/** Vsechny pozadavky (i nesplnene) */
	final Task[] tasks;
	/** Prioritni fronta nadchazejicich eventu serazena podle toho, kdy maji nastat */
	final List<Event> events;

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();

	int numberOftravelingCamels = -1;
	
	public EventManager(List<Event> events, Task[] tasks, int count) {
		this.events = events;
		this.tasks = tasks;
		this.count = count;
	}
	
	public void nextEvent() throws Exception {
		
		//events.add(new Event(100000, EventType.End, 0)); //automaticky zastavi program pokud prekroci cas 100 000
		if (events.size()<1) System.exit(1);
		Event e = events.get(0);
		//System.out.println(events.toString());
		events.remove(0);

		switch(Objects.requireNonNull(e).type) {
			case StorageRefill:
				reffilStorage(e);
				break;
			
			case NewTask:
				doTask(e);
				break;
				
			case CamelDeparting:
				camelDeparting(e);
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
					
			case ErrorTask:
				//System.out.println("\nUkoncuji v case 100 000"); //TODO ve finalni verzi odstranit
				errorTask(e);
				
			default:
				System.exit(0);					
		}
	}

	private void camelHome(Event e) {
		numberOftravelingCamels--;
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Navrat do skladu: %d\n",
								Math.round(e.time),
								e.velbloud.name,
								e.index + 1);
		e.velbloud.home.addCamelToSet(e.velbloud);
	}


	private void camelTransit(Event e) {
		if(e.index >= count) {
			System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Oaza: %d, Kuk na velblouda\n",
						Math.round(e.time),
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
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, %s: %d, Ziznivy %s, Pokracovani mozne v: %d\n",
					Math.round(e.time),
								e.velbloud.name,
								s,
								e.index + 1,
								e.velbloud.druh.name,
						Math.round(e.time + e.velbloud.drinkTime)
		);
		
	} 


	private void camelFinished(Event e) {
		//double finishedTime = e.time + (e.velbloud.task.basketCount * e.velbloud.home.loadingTime);
		double finishedTime = e.time + basketsManipulationTime(e.velbloud);
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Oaza: %d, Vylozeno kosu: %d, Vylozeno v: %d, Casova rezerva: %d\n",
				Math.round(e.time),
								e.velbloud.name,
								e.index + 1,
								e.velbloud.task.basketCount,
				Math.round(finishedTime),
				Math.round(e.velbloud.task.deadline - finishedTime));
		
	}


	private void camelDeparting(Event e) {
		numberOftravelingCamels = numberOftravelingCamels == -1 ? numberOftravelingCamels +2 : numberOftravelingCamels +1;
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Sklad %d, Nalozeno kosu: %d, Odchod v %d\n",
				Math.round(e.time),
								e.velbloud.name,
								e.index + 1,
								e.velbloud.task.basketCount,//TODO neni realny pocet co nese -> kose na vice velbloudu
								//e.time + (((Sklad) Parser.graph.getNodebyId(e.index)).loadingTime * e.velbloud.task.basketCount)
				Math.round(e.time + basketsManipulationTime(e.velbloud))
							);
		//TODO odstranit kose ze skladu, odecist camel ze skladu

		//e.velbloud.home.removeBaskets(e.velbloud.task.basketCount);
	}

	private double basketsManipulationTime(Velbloud velbloud){
		return (velbloud.home.loadingTime * velbloud.task.basketCount);
	}

	private void reffilStorage(Event e) {
		//System.out.println("reffill");
		Sklad refill = ((Sklad) Parser.graph.getNodebyId(e.index));
		refill.makeBaskets();
		if (numberOftravelingCamels != 0) events.add(new Event(e.time + refill.basketMakingTime, EventType.StorageRefill, e.index));
	}

	private void errorTask(Event e){
		System.out.printf(Locale.US, "Cas: %d, Oaza: %d, Vsichni vymreli, Harpagon zkrachoval, Konec simulace",
							Math.round(e.time),
							e.index + 1
							);
		System.exit(0);
	}

	private void doTask(Event e) throws Exception {
		//vice velbloudu obslouzi task
		Task t = tasks[e.index];
		System.out.printf(Locale.US, "Cas: %d, Pozadavek: %d, Oaza: %d, Pocet kosu: %d, Deadline: %d\n",
								Math.round(t.arrivalTime),
								e.index + 1,
								tasks[e.index].oaza + 1,
								t.basketCount,
								Math.round(t.deadline));

		// KONTROLA nejdelsi hrana na ceste jestli zvladne max druh velblouda
		//TODO predelat do pripravy grafu(pri vytvatvareni grafu vyhodit hrany, ktere velbloud nezvladne)
		List<MyPath> pathstoOasis = graph.getPathtoOasisList(t.oaza + count - 1);

		int idPathtoOasis;//id cesty v seznamu pathstoOasis ktera je mozna s max druhem (nejrychlejsi cesta pri generovani)
		double maxDistanceOnPath = Double.MAX_VALUE;
		MyPath currentPath = null;
		double maxCamelTypeDistance = Velbloud.getDruhMaxDistance();

		for (idPathtoOasis = 0; idPathtoOasis < pathstoOasis.size(); idPathtoOasis++) {
			currentPath = pathstoOasis.get(idPathtoOasis);
			maxDistanceOnPath = currentPath.getMaxDistance();//max vzdalenost jedne hrany na ceste
			if (maxCamelTypeDistance > maxDistanceOnPath){
				break;
			}
		}

		// KONTROLA jestli idealni velbloud zvladne cestu v case
		assert currentPath != null;
		double distance = currentPath.getFullDistance();
		double maxTimeforTask = t.deadline - t.arrivalTime;
		double idealCamelTime = Velbloud.getDruhMaxSpeed() / distance;
		if (idealCamelTime > maxTimeforTask)
			events.add(new Event(t.arrivalTime, EventType.ErrorTask, t.oaza));
			//throw  new Exception("Neexistuje druh velblouda, který zvládne tuto cestu ve stanovenem case");
			//misto exception errorEvent

		// HLEDANI jestli sklad ze ktereho jde akutalni cesta ma velblouda co to zvladne
		Velbloud selectedVelbloud = null;

		//pokracovani v prochazeni seznamu pathstoOasis
		for (int i = idPathtoOasis; i < pathstoOasis.size(); i++) {
			currentPath = pathstoOasis.get(i);

			Sklad startStock = currentPath.getStartStock();
			Set<Velbloud> velbloudSet = startStock.getVelbloudSet();

			for (Velbloud velbloud :
					velbloudSet) {
				double velbloudDistance = velbloud.getMaxDistance();
				if (velbloudDistance > currentPath.getMaxDistance()){
					selectedVelbloud = velbloud;
					planEventsforTask(t.arrivalTime, currentPath, selectedVelbloud, t.basketCount);
					break;
				}
			}//TODO zkontrolovat razeni velbloudu -> zefektivnit tenhle cyklus
		}//neexistuje zadny velbloud ve vsech skladech, ktery to zvladne

		//GENEROVANI velblouda
		if (selectedVelbloud == null){//TODO velbloud vcas dorucil ten uz realny
			currentPath = pathstoOasis.get(idPathtoOasis);//vracime se k nejkratsi ceste kterou druh zvladne
			maxDistanceOnPath = currentPath.getMaxDistance();
			Sklad startStock = currentPath.getStartStock();
			double velbloudMaxDistance;
			long startTime = System.currentTimeMillis();
			do {
				if (((System.currentTimeMillis() - startTime)/1000)>10){
					throw new Exception("Velbloudi se generuji dele nez 10s");
				}
				selectedVelbloud = Velbloud.generujVelblouda(startStock);
				velbloudMaxDistance = selectedVelbloud.getMaxDistance();
			} while (velbloudMaxDistance < maxDistanceOnPath);
		}
		selectedVelbloud.setTask(t);
		selectedVelbloud.home.removeCamelFromSet(selectedVelbloud);
		planEventsforTask(t.arrivalTime, currentPath, selectedVelbloud, t.basketCount);

	}

	/**
	 * Naplanuje Eventy do EventManageru ktery jsou tvoreny
	 * pri zpracovavani jednoho Tasku
	 * @param path
	 * @param velbloud
	 */
	private void planEventsforTask(double startTime, MyPath path, Velbloud velbloud, int basketCount){
		double time = startTime;
		double travellingTime = path.getFullDistance() / velbloud.getSpeed();
		int idStock = path.getStartStock().getId();
		double basketManipTime = basketsManipulationTime(velbloud);

		//KONTROLA JESTLI MA SKLAD DOST KOSU
		/*int busketCntStock = path.getStartStock().getBasketCount();
		if (busketCntStock < basketCount){
			int difference = basketCount - busketCntStock;
			for (Event event :
					events) {
				if (event.type == EventType.StorageRefill)
			}
		}*/

		//CAMEL DEPARTING
		events.add(new Event(time, EventType.CamelDeparting, idStock, velbloud));
		time += basketManipTime;

		//CAMEL TRAVELING
		Edge[] edges = path.getEdgesArr();
		double velbloudMaxDistance = velbloud.getMaxDistance();
		double disFromDrin = 0; //vzdalenost usla od posledniho piti

		for (int i = 0; i < edges.length - 1; i++) {
			Edge currentEdge = edges[i];
			Edge nextEdge = edges[i+1];

			time += currentEdge.getWeight() / velbloud.getSpeed(); //cas po dokonceni aktualni hrany

			//                {cas na konci             }  {cas straveny od konce sem{cas straveny cestou tam}}
			double backTime = startTime + travellingTime + (travellingTime - (time - startTime));

			//CAMEL DRINKING
			//pokud velbloud nezvladne aktualni hrany a nasledujici pije na konci aktualni hrane
			if ((currentEdge.getWeight() + nextEdge.getWeight() + disFromDrin) >
					velbloudMaxDistance){
				time += velbloud.drinkTime;
				backTime += velbloud.drinkTime;
				//cesta tam
				events.add(new Event(time, EventType.CamelDrinks,
						currentEdge.getEndNode().getId(),velbloud));
				//cesta zpatky
				events.add(new Event(backTime, EventType.CamelDrinks,
						currentEdge.getEndNode().getId(),velbloud));

				disFromDrin = 0;
			}
			//CAMEL TRANSIT
			else {
				disFromDrin += currentEdge.getWeight();
				//cesta tam
				events.add(new Event(time, EventType.CamelTransit,
						currentEdge.getEndNode().getId(), velbloud));
				//cesta zpatky
				events.add(new Event(backTime, EventType.CamelTransit,
						currentEdge.getEndNode().getId(), velbloud));
			}
		}

		//CAMEL FINISHED
		Edge lastEdge = edges[edges.length-1];
		time += lastEdge.getWeight() / velbloud.getSpeed(); //cas po dokonceni cesty
		events.add(new Event(time, EventType.CamelFinished,
				lastEdge.getEndNode().getId(), velbloud));

		//CAMEL HOME
		assert (time - startTime) != travellingTime;//cas cestovani tam 'time - startime' protoze velbloud jde zpatky
		time += travellingTime + basketManipTime;
		events.add(new Event(time, EventType.CamelHome, idStock, velbloud));

		//SORT EVENTSLIST
		events.sort((o1, o2) -> (int) (o1.time - o2.time));

	}


	
	
	
	
	
	

}
