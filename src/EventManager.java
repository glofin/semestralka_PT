import java.util.*;

/**
 * Trida se stara o nadchazejici udalosti casove linie
 * vzor: jedinacek
 */
public class EventManager {
	//TODO udelat z EventManager jedinacek
	/** Pocet skladu */
	final int count;

	/** Vsechny pozadavky (i nesplnene)
	 * indexovano stejne jako index v events.id - EventType.NewTask
	 * je tu kvuli ziskani informaci o Tasku, ktery nejsou v Eventu - type:NewTask
	 */
	final Task[] tasks;

	private List<Task> finishedTasks = new ArrayList<>();
	private List<Task> processingTasks = new ArrayList<>();


	/** Prioritni fronta nadchazejicich eventu serazena podle toho, kdy maji nastat */
	final PriorityQueue<Event> events;

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();

	private List<String> outputHistory = new ArrayList<>();

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

		if (events.size()<1) return false;
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
			//default -> System.exit(0);
		}
		return true;
	}

	/*--------------------------------------------------------------------------------------------------------------*/


	private void camelHome(Event e) {
		assert e.type != EventType.CamelHome : "Wrong EventType";

		numberOftravelingCamels--;
		printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, Navrat do skladu: %d\n",
								Math.round(e.time),
								e.camel.name,
								e.idInfo + 1));
		e.camel.home.addCamelToSet(e.camel);//todo nefunguje
	}


	private void camelTransit(Event e) {
		assert e.type != EventType.CamelTransit : "Wrong EventType";

		if(e.idInfo >= count) {
			printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, Oaza: %d, Kuk na velblouda\n",
						Math.round(e.time),
								e.camel.name,
								e.idInfo - count - 1));
		}

	}


	private void cameldrinks(Event e) {
		assert e.type != EventType.CamelDrinks : "Wrong EventType";

		String s;
		if(e.idInfo < count) {
			s = "Sklad";
		} else {
			s = "Oaza";
			e.idInfo -= count;
		}
		printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, %s: %d, Ziznivy %s, Pokracovani mozne v: %d\n",
					Math.round(e.time),
								e.camel.name,
								s,
								e.idInfo + 1,
								e.camel.type.name,
						Math.round(e.time + e.camel.drinkTime)
		));
		
	} 


	private void camelFinished(Event e) {
		assert e.type != EventType.CamelFinished : "Wrong EventType";

		double finishedTime = e.time + basketsManipulationTime(e.camel);
		printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, Oaza: %d, Vylozeno kosu: %d, Vylozeno v: %d, Casova rezerva: %d\n",
				Math.round(e.time),
								e.camel.name,
								e.idInfo + 1,
								e.camel.task.basketCount,
				Math.round(finishedTime),
				Math.round(e.camel.task.deadline - finishedTime)));

		//pro vypis stavu prepravy
		processingTasks.remove(e.camel.task);
		finishedTasks.add(e.camel.task);

		e.camel.task.finishTime = finishedTime;
		e.camel.task.finishCamel = e.camel;
	}


	private void camelDeparting(Event e) {
		assert e.type != EventType.CamelDeparting : "Wrong EventType";

		numberOftravelingCamels = numberOftravelingCamels == -1 ? numberOftravelingCamels +2 : numberOftravelingCamels +1;
		printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, Sklad %d, Nalozeno kosu: %d, Odchod v %d\n",
				Math.round(e.time),
								e.camel.name,
								e.idInfo + 1,
								e.camel.task.basketCount,//TODO neni realny pocet co nese -> kose na vice velbloudu
				Math.round(e.time + basketsManipulationTime(e.camel))
							));
		//TODO odstranit kose ze skladu, odecist camel ze skladu

		//e.velbloud.home.removeBaskets(e.velbloud.task.basketCount);

		//pro vypis stavu prepravy
		processingTasks.add(e.camel.task);
	}

	private void reffilStorage(Event e) {
		assert e.type != EventType.StorageRefill : "Wrong EventType";

		//System.out.println("reffill");
		Stock refill = ((Stock) Main.graph.getNodebyId(e.idInfo));
		refill.makeBaskets();
		if (numberOftravelingCamels != 0) events.add(new Event(e.time + refill.basketMakingTime, EventType.StorageRefill, e.idInfo));
	}


	private void errorTask(Event e) {
		assert e.type != EventType.ErrorTask : "Wrong EventType";

		printOutput(String.format(Locale.US, "Cas: %d, Oaza: %d, Vsichni vymreli, Harpagon zkrachoval, Konec simulace",
							Math.round(e.time),
							e.idInfo + 1
							));
	}


	/**
	 * Metoda zaridi vyhodnoceni pozadavku
	 * vysle velblouda, generuje velblouda, posle ho ...
	 * @param event event kde je task a zpracovava se
	 */
	private void doTask(Event event) {
		assert event.type != EventType.NewTask : "Wrong EventType";

		//TODO vice velbloudu obslouzi task
		Task currentTask = tasks[event.idInfo];

		printOutput(String.format(Locale.US, "Cas: %d, Pozadavek: %d, Oaza: %d, Pocet kosu: %d, Deadline: %d\n",
								Math.round(currentTask.arrivalTime),
								event.idInfo + 1,
								tasks[event.idInfo].idOaza + 1,
								currentTask.basketCount,
								Math.round(currentTask.deadline)));

		// KONTROLA nejdelsi hrana na ceste jestli zvladne max druh velblouda
		//TODO predelat do pripravy grafu(pri vytvatvareni grafu vyhodit hrany, ktere velbloud nezvladne)
		List<MyPath> pathstoOasis = graph.getPathtoOasisList(currentTask.idOaza + count - 1);
		/* id cesty v seznamu pathstoOasis ktera je mozna s max druhem (nejrychlejsi cesta pri generovani) */
		int idPathtoOasis;
		double maxDistanceOnPath;
		MyPath currentPath = null;

		// KONTROLA nejdelsi hrana na ceste jestli zvladne max druh velblouda
		double maxCamelTypeDistance = Camel.getTypeMaxDistance();
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
		double maxTimeforTask = currentTask.deadline - currentTask.arrivalTime;
		double idealCamelTime = Camel.getTypeMaxSpeed() / distance;
		if (distance != 0 && idealCamelTime > maxTimeforTask) {
			events.add(new Event(currentTask.arrivalTime, EventType.ErrorTask, currentTask.idOaza));
		}

		// HLEDANI jestli sklad ze ktereho jde akutalni cesta, ma velblouda co to zvladne
		Camel selectedCamel = null;

		//pokracovani v prochazeni seznamu pathstoOasis
		for (int i = idPathtoOasis; i < pathstoOasis.size(); i++) {
			currentPath = pathstoOasis.get(i);

			Stock startStock = currentPath.getStartStock();
			SortedSet<Camel> camelSet = startStock.getCamelSet();
			if (camelSet.isEmpty()) {
				continue;
			}
			Camel camel = camelSet.first();
			if (camel.getMaxDistance() > currentPath.getMaxDistance()) {
				selectedCamel = camel;
				break;
			}
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

				selectedCamel = Camel.generateCamel(startStock);
				velbloudMaxDistance = selectedCamel.getMaxDistance();
			} while (velbloudMaxDistance < maxDistanceOnPath);
		}
		selectedCamel.setTask(currentTask);
		selectedCamel.home.removeCamelFromSet(selectedCamel);
		planEventsforTask(currentTask.arrivalTime, currentPath, selectedCamel, currentTask.basketCount);

	}

	/*------------------------------------------------------------------------------------------------------------*/

	private double basketsManipulationTime(Camel camel){
		//TODO nesmime nasobit task.basketCount ale tolik kolik nese Camel
		return (camel.home.loadingTime * camel.task.basketCount);
	}

	/**
	 * Naplanuje Eventy do EventManageru ktery jsou tvoreny
	 * pri zpracovavani jednoho Tasku
	 * @param path vybrana cesta
	 * @param camel velbloud
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

	private void printOutput(String output) {
		//GUI.getInstance().addToOutputGUI(output);
		System.out.print(output);
		outputHistory.add(output);
	}

	public void addEvent(Event event){
		events.add(event);
	}

	public List<String> getOutputHistory() {
		return outputHistory;
	}

	public List<Task> getFinishedTasks() {
		return finishedTasks;
	}

	public List<Task> getProcessingTasks() {
		return processingTasks;
	}
}
