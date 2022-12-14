import java.util.*;

/**
 * Trida se stara o nadchazejici udalosti casove linie
 * vzor: jedinacek
 */
public class EventManager {
	/** Pocet skladu */
	public final int stockCount;

	/** Vsechny pozadavky (i nesplnene)
	 * indexovano stejne jako index v events.id - EventType.NewTask
	 * je tu kvuli ziskani informaci o Tasku, ktery nejsou v Eventu - type:NewTask
	 */
	public final Task[] tasks;
	
	/** Cas konce uspesne simulace */
	public double endTime = -1;
	
	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();
	/** pole tasku, ktere nemeli dostatek kosu ve skladu a cekaji na
	 * vygenerovani kosu odkazuje na ne id v Evene.idInfo*/
	private final List<DelayedTask> delayedTasks = new ArrayList<>();
	/**Dokoncene tasky pro Aktualni stav prepravy v GUI*/
	private final List<Task> finishedTasks = new ArrayList<>();
	/**Dokoncene tasky a jejich velbloudi dorazili domu
	 * pro ukonceni generovani kosu skladu*/
	private final List<Task> finishedTasksAndCamelsHome = new ArrayList<>();
	/**Zpracovavane tasky pro Aktualni stav prepravy v GUI*/
	private final List<Task> processingTasks = new ArrayList<>();


	/** Prioritni fronta nadchazejicich eventu serazena podle toho, kdy maji nastat */
	private final PriorityQueue<Event> events;
	/** Zaznam outputu pro krokovani zpet v GUI*/
	private final List<String> outputHistory = new ArrayList<>();

	private int numberOfTravelingCamels = -1;
	
	/**
	 * Vytvori novou instance tridy EventManager
	 * @param events	Prioritni fronta nadchazejicich eventu
	 * @param tasks		Pozadavky zpracovavane behem simulace
	 * @param count		Pocet skladu
	 */
	public EventManager(PriorityQueue<Event> events, Task[] tasks, int count) {
		this.events = events;
		this.tasks = tasks;
		this.stockCount = count;
	}

	/**
	 * Metoda zpracovava dalsi pozadavek eventy ve fronte events
	 * @return true - pozadavek neni errorType; false - pozadavek je errorType, konec programu
	 */
	public boolean nextEvent() {

		//kdyz nejsou eventy
		if (events.size()<1) {return false;}
		//kdyz nejsou Tasky
		if(endTime > 0){return false;}

		Event e = events.poll();

		switch (Objects.requireNonNull(e).type) {
			case StorageRefill -> reffilStorage(e);
			case NewTask -> doTask(e);
			case DelayedTask -> delayedTask(e);
			
			default -> {return nextEvent2(e);}
		}
		return true;
	}

	/** Metoda nextEvent rozdelena kvuli PMD
	 * @param e event
	 */
	private boolean nextEvent2(Event e) {
		switch(e.type ) {
			case CamelDeparting -> camelDeparting(e);
			case CamelFinished -> camelFinished(e);
			case CamelDrinks -> cameldrinks(e);
			case CamelTransit -> camelTransit(e);
			case CamelHome -> camelHome(e);
			case ErrorTask -> {
					errorTask(e);
					return false;
					}
		default -> {return false;}
		}
		return true;
	}

	/*--------------------------------------------------------------------------------------------------------------*/

	/**
	 * obslouzeni eventu velbloud dorazil domu
	 * @param e event
	 */
	private void camelHome(Event e) {
		assert e.type != EventType.CamelHome : "Wrong EventType";

		numberOfTravelingCamels--;
		printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, Navrat do skladu: %d\n",
								Math.round(e.time),
								e.camel.name,
								e.idInfo + 1));
		e.camel.home.addCamelToSet(e.camel);
		finishedTasksAndCamelsHome.add(e.camel.task);
		if(finishedTasksAndCamelsHome.size() >= tasks.length && numberOfTravelingCamels == 0) {
			endTime = e.time;
		}
	}

	/**
	 * obslouzeni eventu velbloud prochazi jinym vrcholem
	 * @param e event
	 */
	private void camelTransit(Event e) {
		assert e.type != EventType.CamelTransit : "Wrong EventType";

		if(e.idInfo >= stockCount) {
			printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, Oaza: %d, Kuk na velblouda\n",
						Math.round(e.time),
								e.camel.name,
								e.idInfo - stockCount - 1));
		}

	}

	/**
	 * obslouzeni eventu velbloud pije
	 * @param e event
	 */
	private void cameldrinks(Event e) {
		assert e.type != EventType.CamelDrinks : "Wrong EventType";

		String s;
		if(e.idInfo < stockCount) {
			s = "Sklad";
		} else {
			s = "Oaza";
			e.idInfo -= stockCount;
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

	/**
	 * obslouzeni eventu velbloud dorazil do cilove oazy
	 * @param e event
	 */
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
		e.camel.setCarryBasketsOnTask(0);
	}


	/**
	 * obslouzeni eventu velbloud vyrazil na cestu
	 * @param e event
	 */
	private void camelDeparting(Event e) {
		assert e.type != EventType.CamelDeparting : "Wrong EventType";
		
		numberOfTravelingCamels = numberOfTravelingCamels == -1 ? numberOfTravelingCamels +2 : numberOfTravelingCamels +1;
		printOutput(String.format(Locale.US, "Cas: %d, Velbloud: %s, Sklad %d, Nalozeno kosu: %d, Odchod v %d\n",
				Math.round(e.time),
								e.camel.name,
								e.idInfo + 1,
								e.camel.getCarryBasketsOnTask(),
				Math.round(e.time + basketsManipulationTime(e.camel))
							));

		e.camel.home.removeBaskets(e.camel.getCarryBasketsOnTask());

		//pro vypis stavu prepravy
		processingTasks.add(e.camel.task);
	}

	/**
	 * obslouzeni eventu doplneni kosu po casovem intervalu na sklad
	 * @param e event
	 */
	private void reffilStorage(Event e) {
		assert e.type != EventType.StorageRefill : "Wrong EventType";


		Stock refill = ((Stock) Main.graph.getNodebyId(e.idInfo));

		refill.makeBaskets(e.time);
		//System.out.println("reffill Time=" + e.time  + " stockBasketCount=" + refill.basketCount);
		events.add(new Event(e.time + refill.basketMakingTime, EventType.StorageRefill, e.idInfo));
	}

	/**
	 * obslouzeni eventu kdyz nelze obslouzit Pozadavek
	 * @param e event
	 */
	private void errorTask(Event e) {
		assert e.type != EventType.ErrorTask : "Wrong EventType";

		printOutput(String.format(Locale.US, "Cas: %d, Oaza: %d, Vsichni vymreli, Harpagon zkrachoval, Konec simulace",
							Math.round(e.time),
							e.idInfo + 1
							));
	}

	/**
	 * obslouzeni eventu kdyz se muze vykonat task co
	 * cekal na doplneni kosu ve skladu
	 * @param event event
	 */
	private void delayedTask(Event event){
		DelayedTask currentDelayedTask = delayedTasks.get(event.idInfo);
		MyPath currentPath = currentDelayedTask.path;
		Task currentTask = currentDelayedTask.task;
		List<Camel> camelsOnTask = currentDelayedTask.camelsOnTask;

		int startStockBasketCount = currentPath.getStartStock().getBasketCount();
		if (currentTask.basketCount > startStockBasketCount){
			//nema dostatek kosu
			double eventTime = event.time +
					(currentTask.basketCount - startStockBasketCount) * currentPath.getStartStock().getBasketMakingTime();

			addToEventscheckDeadline(new Event(eventTime, EventType.DelayedTask, event.idInfo), events, currentTask);
		}
		else {
			//ma dostatek kosu
			for (Camel camel :
					camelsOnTask) {
				planEventsforCamelTravel(event.time, currentPath, camel);
			}
		}
	}



	/**
	 * Metoda zaridi vyhodnoceni pozadavku
	 * vysle velblouda, generuje velblouda, posle ho ...
	 * @param event event kde je task a zpracovava se
	 */
	private void doTask(Event event) {

		assert event.type != EventType.NewTask : "Wrong EventType";

		Task currentTask = tasks[event.idInfo];

		printOutput(String.format(Locale.US, "Cas: %d, Pozadavek: %d, Oaza: %d, Pocet kosu: %d, Deadline: %d\n",
				Math.round(currentTask.arrivalTime),
				event.idInfo + 1,
				tasks[event.idInfo].idOaza + 1,
				currentTask.basketCount,
				Math.round(currentTask.deadline)));

		/*-------------------------HLEDANI CESTY A VELBLOUDA-------------------------*/

		List<MyPath> pathstoOasis = graph.getPathtoOasisList(currentTask.idOaza + stockCount - 1);
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
		//hodit ErrorEvent
		assert currentPath != null;
		if ((idPathtoOasis==(pathstoOasis.size() - 1) &&
			maxCamelTypeDistance < pathstoOasis.get(pathstoOasis.size()-1).getMaxDistance()) || idPathtoOasis ==pathstoOasis.size()) {
				events.add(new Event(currentTask.arrivalTime, EventType.ErrorTask, currentTask.idOaza));
				return;
			}

		// KONTROLA jestli idealni velbloud zvladne cestu v case
		double distance = currentPath.getFullDistance();
		double maxTimeforTask = currentTask.deadline - currentTask.arrivalTime;
		double idealCamelTime = Camel.getTypeMaxSpeed() / distance;
		//hodit ErrorEvent
		if (distance != 0 && idealCamelTime > maxTimeforTask) {
			events.add(new Event(currentTask.arrivalTime, EventType.ErrorTask, currentTask.idOaza));
			return;
		}

		// HLEDANI jestli sklad ze ktereho jde akutalni cesta, ma velblouda co to zvladne
		Camel selectedCamel = checkCurrentCamels(idPathtoOasis, pathstoOasis);

		//GENEROVANI velblouda
		if (selectedCamel == null){
			currentPath = pathstoOasis.get(idPathtoOasis);//vracime se k nejkratsi ceste kterou druh zvladne
			selectedCamel = generateCamel(currentPath);
		}
		selectedCamel.setTask(currentTask);
		selectedCamel.addPath(currentPath);
		doTask2(selectedCamel, currentPath);
	}

	/**Rozdelene z duvodu PMD
	 *
	 * @param selectedCamel vybrany velbloud
	 * @param currentPath vybrana cesta
	 */
	private void doTask2(Camel selectedCamel, MyPath currentPath) {
		/*----------VYSLANI VELBLOUDU----------*/
		List<Camel> camelsOnTask = new ArrayList<>();
		Task currentTask = selectedCamel.task;
		selectedCamel.home.removeCamelFromSet(selectedCamel);
		camelsOnTask.add(selectedCamel);
		//planEventsforCamelTravel(currentTask.arrivalTime, currentPath, selectedCamel);

		//JESTLI VELBLOUD UNESE VSECHNY KOSE PRO POZADAVEK
		if (selectedCamel.getType().getMaxLoad()<currentTask.basketCount) {
			//NEUNESE
			selectedCamel.setCarryBasketsOnTask(selectedCamel.getType().getMaxLoad());
			int processedBasketsCount = selectedCamel.getCarryBasketsOnTask();
			//hledani dalsich velbloudu pro task
			while (processedBasketsCount < currentTask.basketCount) {
				Camel camel = findCamelforPath(currentPath);
				camel.setTask(currentTask);
				camel.home.removeCamelFromSet(camel);
				camelsOnTask.add(camel);
				camel.addPath(currentPath);
				//planEventsforCamelTravel(currentTask.arrivalTime, currentPath, selectedCamel);

				//posledni velbloud
				if ((currentTask.basketCount - processedBasketsCount) < camel.getType().getMaxLoad()){
					camel.setCarryBasketsOnTask(currentTask.basketCount - processedBasketsCount);
					break;
				}
				camel.setCarryBasketsOnTask(camel.getType().getMaxLoad());
				processedBasketsCount += camel.getCarryBasketsOnTask();
			}
		}
		else {
			//UNESE
			selectedCamel.setCarryBasketsOnTask(currentTask.basketCount);
		}

		//KONTROLA JESTLI SKLAD MA DOSTATEK KOSU
		checkBaskets(currentPath, currentTask, camelsOnTask);

	}

	/**
	 * Konrola jestli ma na cestu dostatek kosu
	 */
	private void checkBaskets(MyPath currentPath, Task currentTask, List<Camel> camelsOnTask) {
		int startStockBasketCount = currentPath.getStartStock().getBasketCount();
		if (currentTask.basketCount > startStockBasketCount){
			//nema dostatek kosu
			delayedTasks.add(new DelayedTask(currentTask, currentPath, camelsOnTask));
			double eventTime = currentTask.arrivalTime +
					(currentTask.basketCount - startStockBasketCount) * currentPath.getStartStock().getBasketMakingTime();

			addToEventscheckDeadline(new Event(eventTime, EventType.DelayedTask, (delayedTasks.size() - 1)), events, currentTask);
		}
		else {
			//ma dostatek kosu
			for (Camel camel :
					camelsOnTask) {
				planEventsforCamelTravel(currentTask.arrivalTime, currentPath, camel);
			}
		}
	}

	/**
	 * Hledani velblouda pro cestu
	 */
	private Camel checkCurrentCamels(int idPathtoOasis,List<MyPath> pathstoOasis) {
		//pokracovani v prochazeni seznamu pathstoOasis
		for (int i = idPathtoOasis; i < pathstoOasis.size(); i++) {
			MyPath currentPath = pathstoOasis.get(i);
			Stock startStock = currentPath.getStartStock();
			SortedSet<Camel> camelSet = startStock.getCamelSet();
			if (camelSet.isEmpty()) {
				continue;
			}
			Camel camel = camelSet.first();
			if (camel.getMaxDistance() > currentPath.getMaxDistance()) {
				return camel;
			}
		}//neexistuje zadny velbloud ve vsech skladech na vsech cestach, ktery to zvladne
		return null;
	}

	/**
	 * Metoda najde velblouda, ktery zvladne cestu
	 * @param currentPath cesta
	 * @return velbloud
	 */
	private Camel findCamelforPath(MyPath currentPath) {
		//PROJIT JESTLI SKLAD NEMA VELBLOUDA CO TO ZVLADNE
		Stock startStock = currentPath.getStartStock();
		if (!startStock.camelSet.isEmpty() && 
				startStock.camelSet.first().getMaxDistance()>currentPath.getMaxDistance()){
			return startStock.camelSet.first();
		}

		//GENEROVANI VELBLOUDA
		return generateCamel(currentPath);
	}

	/**
	 * Metoda vygeneruje velbloudy dokud jeden nezvladne cestu
	 * @param currentPath cesta
	 * @return vygenerovany velbloud co zvladne cestu
	 */
	private Camel generateCamel(MyPath currentPath){
		Camel selectedCamel;

		double maxDistanceOnPath = currentPath.getMaxDistance();
		Stock startStock = currentPath.getStartStock();
		double velbloudMaxDistance;
		long startTime = System.currentTimeMillis();
		do {
			assert ((System.currentTimeMillis() - startTime)/1000)>2 : "Velbloudi se generuji dele nez 2s";

			selectedCamel = Camel.generateCamel(startStock);
			velbloudMaxDistance = selectedCamel.getMaxDistance();
		} while (velbloudMaxDistance < maxDistanceOnPath);

		return selectedCamel;
	}

	/*------------------------------------------------------------------------------------------------------------*/

	/**
	 * Metoda vypocte cas, pro nalozeni kosu
	 * @param camel velbloud na ktereho se nakladaji kose
	 * @return cas pro nalozeni kosu
	 */
	private double basketsManipulationTime(Camel camel){
		return (camel.home.loadingTime * camel.getCarryBasketsOnTask());
	}

	/**
	 * Naplanuje Eventy do EventManageru ktery jsou tvoreny
	 * pri zpracovavani jednoho Tasku
	 * @param path vybrana cesta
	 * @param camel velbloud
	 */
	private void planEventsforCamelTravel(double startTime, MyPath path, Camel camel){
		double time = startTime;
		double travellingTime = path.getFullDistance() / camel.getSpeed();
		int idStock = path.getStartStock().getId();
		double basketManipTime = basketsManipulationTime(camel);

		//CAMEL DEPARTING
		addToEventscheckDeadline(
				new Event(time, EventType.CamelDeparting, idStock, camel),
				events,
				camel.task);
		time += basketManipTime;

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
				addToEventscheckDeadline(
						new Event(time, EventType.CamelDrinks,
								currentEdge.getEndNode().getId(), camel),
						events,
						camel.task);
				//cesta zpatky
				addToEventscheckDeadline(
						new Event(backTime, EventType.CamelDrinks,
								currentEdge.getEndNode().getId(), camel),
						events,
						camel.task);

				disFromDrin = 0;
			}
			//CAMEL TRANSIT
			else {
				disFromDrin += currentEdge.getWeight();
				//cesta tam
				addToEventscheckDeadline(
						new Event(time, EventType.CamelTransit,
								currentEdge.getEndNode().getId(), camel),
						events,
						camel.task);
				//cesta zpatky
				addToEventscheckDeadline(
						new Event(backTime, EventType.CamelTransit,
								currentEdge.getEndNode().getId(), camel),
						events,
						camel.task);
			}
		}

		//CAMEL FINISHED
		Edge lastEdge = edges[edges.length-1];
		time += lastEdge.getWeight() / camel.getSpeed(); //cas po dokonceni cesty
		addToEventscheckDeadline(
				new Event(time, EventType.CamelFinished,
						lastEdge.getEndNode().getId(), camel),
				events,
				camel.task);

		//CAMEL HOME
		assert (time - startTime) != travellingTime;//cas cestovani tam 'time - startime' protoze velbloud jde zpatky
		time += travellingTime + basketManipTime;
		addToEventscheckDeadline(
				new Event(time, EventType.CamelHome, idStock, camel),
				events,
				camel.task);

	}

	/**
	 * Kontrola pri pridavani eventu z planEventsforCamel
	 * jestli nejsou pres deadline tasku
	 * @param addEvent event co se pridava do events
	 * @param events prioritni fronta do ktere se pridava event
	 * @param task obslouzovany pozadavek
	 */
	private void addToEventscheckDeadline(Event addEvent, PriorityQueue<Event> events, Task task){
		if (addEvent.time >= task.deadline) {
			//System.out.println("addToEventscheckDeadline error v podmince");
			events.add(new Event(task.deadline, EventType.ErrorTask, task.idOaza));
		}
		else {
			//System.out.println("addToEventscheckDeadline pridano mimo podminku");
			events.add(addEvent);
		}
	}

	/**
	 * Metoda vypisuje zpravy z eventu do vypisu
	 * a uklada historii vypisu do outputHistory
	 * @param output vypisovany text
	 */
	private void printOutput(String output) {
		//GUI.getInstance().addToOutputGUI(output);
		System.out.print(output);
		outputHistory.add(output);
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
