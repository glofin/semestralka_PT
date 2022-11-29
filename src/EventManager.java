import java.util.*;

/**
 * Trida se stara o nadchazejici udalosti casove linie
 * vzor: jedinacek
 */
public class EventManager {
	//TODO PRIORITA ERROR stav
	//TODO spatne poradi vypisu v dense_small.txt - stejny cas kazi se poradi
	
	/** Pocet skladu */
	final int count;
	/** Vsechny pozadavky (i nesplnene) */
	final Task[] tasks;
	/** Prioritni fronta nadchazejicich eventu serazena podle toho, kdy maji nastat */
	final PriorityQueue<Event> events;

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();

	int numberOftravelingCamels = -1;
	
	public EventManager(PriorityQueue<Event> events, Task[] tasks, int count) {
		this.events = events;
		this.tasks = tasks;
		this.count = count;
	}

	/**
	 * Metoda zpracovava dalsi pozadavek eventy ve fronte events
	 * @return true - pozadavek neni errorType; false - pozadavek je errorType, konec programu
	 */
	public boolean nextEvent() {
		
		//events.add(new Event(100000, EventType.End, 0)); //automaticky zastavi program pokud prekroci cas 100 000
		if (events.size()<1) System.exit(1);
		Event e = events.poll();
		//System.out.println(events.toString());

		switch (Objects.requireNonNull(e).type) {
			case StorageRefill -> reffilStorage(e);
			case NewTask -> doTask(e);
			case CamelDeparting -> camelDeparting(e);
			case CamelFinished -> camelFinished(e);
			case CamelDrinks -> cameldrinks(e);
			case CamelTransit -> camelTransit(e);
			case CamelHome -> camelHome(e);
			case ErrorTask -> {
				errorTask(e);
				return false;
			}
			default -> System.exit(0);
		}
		return true;
	}

	/*--------------------------------------------------------------------------------------------------------------*/


	private void camelHome(Event e) {
		assert e.type != EventType.CamelHome : "Wrong EventType";

		numberOftravelingCamels--;
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Navrat do skladu: %d\n",
								Math.round(e.time),
								e.camel.name,
								e.index + 1);
		e.camel.home.addCamelToSet(e.camel);
	}


	private void camelTransit(Event e) {
		assert e.type != EventType.CamelTransit : "Wrong EventType";

		if(e.index >= count) {
			System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Oaza: %d, Kuk na velblouda\n",
						Math.round(e.time),
								e.camel.name,
								e.index - count - 1);
		}

	}


	private void cameldrinks(Event e) {
		assert e.type != EventType.CamelDrinks : "Wrong EventType";

		String s;
		if(e.index < count) {
			s = "Sklad";
		} else {
			s = "Oaza";
			e.index -= count;
		}
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, %s: %d, Ziznivy %s, Pokracovani mozne v: %d\n",
					Math.round(e.time),
								e.camel.name,
								s,
								e.index + 1,
								e.camel.druh.name,
						Math.round(e.time + e.camel.drinkTime)
		);
		
	} 


	private void camelFinished(Event e) {
		assert e.type != EventType.CamelFinished : "Wrong EventType";

		//double finishedTime = e.time + (e.velbloud.task.basketCount * e.velbloud.home.loadingTime);
		double finishedTime = e.time + basketsManipulationTime(e.camel);
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Oaza: %d, Vylozeno kosu: %d, Vylozeno v: %d, Casova rezerva: %d\n",
				Math.round(e.time),
								e.camel.name,
								e.index + 1,
								e.camel.task.basketCount,
				Math.round(finishedTime),
				Math.round(e.camel.task.deadline - finishedTime));
		
	}


	private void camelDeparting(Event e) {
		assert e.type != EventType.CamelDeparting : "Wrong EventType";

		numberOftravelingCamels = numberOftravelingCamels == -1 ? numberOftravelingCamels +2 : numberOftravelingCamels +1;
		System.out.printf(Locale.US, "Cas: %d, Velbloud: %s, Sklad %d, Nalozeno kosu: %d, Odchod v %d\n",
				Math.round(e.time),
								e.camel.name,
								e.index + 1,
								e.camel.task.basketCount,//TODO neni realny pocet co nese -> kose na vice velbloudu
								//e.time + (((Sklad) Parser.graph.getNodebyId(e.index)).loadingTime * e.velbloud.task.basketCount)
				Math.round(e.time + basketsManipulationTime(e.camel))
							);
		//TODO odstranit kose ze skladu, odecist camel ze skladu

		//e.velbloud.home.removeBaskets(e.velbloud.task.basketCount);
	}
	/* pomocna trida k metode vyse ^ */
	private double basketsManipulationTime(Camel camel){
		return (camel.home.loadingTime * camel.task.basketCount);
	}


	private void reffilStorage(Event e) {
		assert e.type != EventType.StorageRefill : "Wrong EventType";

		//System.out.println("reffill");
		Stock refill = ((Stock) Main.graph.getNodebyId(e.index));
		refill.makeBaskets();
		if (numberOftravelingCamels != 0) events.add(new Event(e.time + refill.basketMakingTime, EventType.StorageRefill, e.index));
	}


	private void errorTask(Event e) {
		assert e.type != EventType.ErrorTask : "Wrong EventType";

		System.out.printf(Locale.US, "Cas: %d, Oaza: %d, Vsichni vymreli, Harpagon zkrachoval, Konec simulace",
							Math.round(e.time),
							e.index + 1
							);
	}


	/**
	 * Metoda zaridi vyhodnoceni pozadavku
	 * vysle velblouda, generuje velblouda, posle ho ...
	 * @param e event kde je task a zpracovava se
	 */
	private void doTask(Event e) {
		assert e.type != EventType.NewTask : "Wrong EventType";

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
		double maxCamelTypeDistance = Camel.getDruhMaxDistance();

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
		double idealCamelTime = Camel.getDruhMaxSpeed() / distance;
		if (distance != 0 && idealCamelTime > maxTimeforTask)
			events.add(new Event(t.arrivalTime, EventType.ErrorTask, t.oaza));
			//throw  new Exception("Neexistuje druh velblouda, který zvládne tuto cestu ve stanovenem case");
			//misto exception errorEvent

		// HLEDANI jestli sklad ze ktereho jde akutalni cesta ma velblouda co to zvladne
		Camel selectedCamel = null;

		//pokracovani v prochazeni seznamu pathstoOasis
		for (int i = idPathtoOasis; i < pathstoOasis.size(); i++) {
			currentPath = pathstoOasis.get(i);

			Stock startStock = currentPath.getStartStock();
			Set<Camel> camelSet = startStock.getVelbloudSet();

			for (Camel camel :
					camelSet) {
				double velbloudDistance = camel.getMaxDistance();
				if (velbloudDistance > currentPath.getMaxDistance()){
					selectedCamel = camel;
					planEventsforTask(t.arrivalTime, currentPath, selectedCamel, t.basketCount);
					break;
				}
			}//TODO zkontrolovat razeni velbloudu -> zefektivnit tenhle cyklus
		}//neexistuje zadny velbloud ve vsech skladech, ktery to zvladne

		//GENEROVANI velblouda
		if (selectedCamel == null){//TODO velbloud vcas dorucil ten uz realny
			currentPath = pathstoOasis.get(idPathtoOasis);//vracime se k nejkratsi ceste kterou druh zvladne
			maxDistanceOnPath = currentPath.getMaxDistance();
			Stock startStock = currentPath.getStartStock();
			double velbloudMaxDistance;
			long startTime = System.currentTimeMillis();
			do {
				assert ((System.currentTimeMillis() - startTime)/1000)>10 : "Velbloudi se generuji dele nez 10s";

				selectedCamel = Camel.generujVelblouda(startStock);
				velbloudMaxDistance = selectedCamel.getMaxDistance();
			} while (velbloudMaxDistance < maxDistanceOnPath);
		}
		selectedCamel.setTask(t);
		selectedCamel.home.removeCamelFromSet(selectedCamel);
		planEventsforTask(t.arrivalTime, currentPath, selectedCamel, t.basketCount);

	}

	/*------------------------------------------------------------------------------------------------------------*/

	/**
	 * Naplanuje Eventy do EventManageru ktery jsou tvoreny
	 * pri zpracovavani jednoho Tasku
	 * @param path
	 * @param camel
	 */
	private void planEventsforTask(double startTime, MyPath path, Camel camel, int basketCount){
		double time = startTime;
		double travellingTime = path.getFullDistance() / camel.getSpeed();
		int idStock = path.getStartStock().getId();
		double basketManipTime = basketsManipulationTime(camel);

		//TODO KONTROLA JESTLI MA SKLAD DOST KOSU a cekani na vygenerovani
		/*int busketCntStock = path.getStartStock().getBasketCount();
		if (busketCntStock < basketCount){
			int difference = basketCount - busketCntStock;
			for (Event event :
					events) {
				if (event.type == EventType.StorageRefill)
			}
		}*/

		//CAMEL DEPARTING
		events.add(new Event(time, EventType.CamelDeparting, idStock, camel));
		time += basketManipTime;
		//TODO kontrola jestli unese velboloud kose (rozdeleni kosu mezi nekolik velbloudu)

		//CAMEL TRAVELING
		Edge[] edges = path.getEdgesArr();
		double velbloudMaxDistance = camel.getMaxDistance();
		double disFromDrin = 0; //vzdalenost usla od posledniho piti

		for (int i = 0; i < edges.length - 1; i++) {
			Edge currentEdge = edges[i];
			Edge nextEdge = edges[i+1];

			time += currentEdge.getWeight() / camel.getSpeed(); //cas po dokonceni aktualni hrany

			//                {cas na konci             }  {cas straveny od konce sem{cas straveny cestou tam}}
			double backTime = startTime + travellingTime + (travellingTime - (time - startTime));

			//CAMEL DRINKING
			//pokud velbloud nezvladne aktualni hrany a nasledujici pije na konci aktualni hrane
			if ((currentEdge.getWeight() + nextEdge.getWeight() + disFromDrin) >
					velbloudMaxDistance){
				time += camel.drinkTime;
				backTime += camel.drinkTime;
				//cesta tam
				events.add(new Event(time, EventType.CamelDrinks,
						currentEdge.getEndNode().getId(), camel));
				//cesta zpatky
				events.add(new Event(backTime, EventType.CamelDrinks,
						currentEdge.getEndNode().getId(), camel));

				disFromDrin = 0;
			}
			//CAMEL TRANSIT
			else {
				disFromDrin += currentEdge.getWeight();
				//cesta tam
				events.add(new Event(time, EventType.CamelTransit,
						currentEdge.getEndNode().getId(), camel));
				//cesta zpatky
				events.add(new Event(backTime, EventType.CamelTransit,
						currentEdge.getEndNode().getId(), camel));
			}
		}

		//CAMEL FINISHED
		Edge lastEdge = edges[edges.length-1];
		time += lastEdge.getWeight() / camel.getSpeed(); //cas po dokonceni cesty
		events.add(new Event(time, EventType.CamelFinished,
				lastEdge.getEndNode().getId(), camel));

		//CAMEL HOME
		assert (time - startTime) != travellingTime;//cas cestovani tam 'time - startime' protoze velbloud jde zpatky
		time += travellingTime + basketManipTime;
		events.add(new Event(time, EventType.CamelHome, idStock, camel));

	}


	
	
	
	
	
	

}
