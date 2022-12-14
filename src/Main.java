import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Trida ktera ovlada spusteni aplikace
 * a vytvari propojeni mezi GUI a ostatnimi tridami
 */
public class Main {
	/**pro jedinacka*/
	private static Main messenger;

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();

	/**minimalni cas cekani pro ovlivnenni rychlosti vypisu*/
	private static double waitTimeMsMin = 0;
	/**maximalni cas cekani pro ovlivnenni rychlosti vypisu*/
	private static double waitTimeMsMax = 1000;
	/**vychozi cas cekani pro ovlivnenni rychlosti vypisu*/
	private static double waitTimeMs = waitTimeMsMax / 2;

	/**aktualni id eventu pri krokovani dopredu a zpet*/
	private static int currentGUIEventId = -1;

	/**pro zastaveni vypisu pri kliknuti na tlacitko stop v GUI*/
	private static boolean isRunningOutput;

	/** manager pro zpracovavani pozadavku a dalsich eventu*/
	private static EventManager manager;
	/**pro jedinacka*/
	public static Main getInstance(){
		if(messenger==null) {
			messenger = new Main();
		}
		return messenger;
	}

	/**
	 * metoda precte soubor z fileName a nastaviho do datovych struktur
	 * pouzivanych v aplikaci a nastavi eventManager
	 */
	public static void main(String[] args) {

		//NACTENI SOUBORU
		try {
			String input = Parser.fileToString("data/tutorial.txt");
			setUp(input);
			//System.out.println(graph.toString());//vypis grafu
		} catch (IOException e) {//chyba ve vstupnim souboru nebo jeho jmene
			e.printStackTrace();
			System.exit(0);
		}

		//ZPRACOVANI POZADAVKU
		boolean isNotErrorEvent;
		do{
			isNotErrorEvent = manager.nextEvent();
		}
		while(isNotErrorEvent);

		//ZAPIS STATISTIK DO SOUBORU
		try {
			makeOutputFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stejne jako main jen pro nastartovani z GUI
	 * @param fileName jmeno souboru kde je mapa
	 */
	public static void start(String fileName) {
		//kdyz se nacita druhy soubor, vycisteni
		graph.clearGraph();
		waitTimeMs = waitTimeMsMax / 2;
		currentGUIEventId = -1;

		//System.out.println(fileName);
		//NACTENI SOUBORU
		try {
			String input = Parser.fileToString(fileName);
			setUp(input);
			System.out.println("NACTEN SOUBOR: " + fileName + "\n\n");
			//System.out.println(graph.toString());//vypis grafu
		} catch (IOException e) {//chyba ve vstupnim souboru nebo jeho jmene
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Dalsi krok pri kliknuti z GUI
	 * @return 0 - neni to error event
	 * 			1 - pridavame dalsi krok nevracime se zpet
	 */
	public static boolean[] nextStepEvent(){
		boolean[] rtrBool = new boolean[2];
		//System.out.println("dalsi krok:" + manager.getOutputHistory().size());
		if (currentGUIEventId <(manager.getOutputHistory().size() - 1) && currentGUIEventId !=-1){
			//System.out.println("dalsi krok v ifu");
			currentGUIEventId++;
			System.out.print(manager.getOutputHistory().get(currentGUIEventId));
			rtrBool[0] = true;
			return rtrBool;
		}
		//System.out.println("dalsi krok");
		boolean isNotErrorEvent = manager.nextEvent();
		if (isNotErrorEvent) {
			currentGUIEventId = manager.getOutputHistory().size() - 1;
		}else {
			try {
				makeOutputFile();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		rtrBool[0] = isNotErrorEvent;
		rtrBool[1] = true;
		return rtrBool;
	}

	/**
	 * Krok zpet kliknuti z GUI
	 */
	public static void previusStepEvent(){
		System.out.println("KROK ZPET:");
		if((currentGUIEventId -1)<0){
			System.out.println("NELZE KROK ZPET");
			return;
		}
		currentGUIEventId--;
		System.out.println(manager.getOutputHistory().get(currentGUIEventId));
	}

	/**
	 * Zastaveno kliknuti z GUI
	 */
	public static void stopRunningOutput(){
		System.out.println("-------------------------ZASTAVENO-------------------------");
		isRunningOutput = false;
	}

	/**
	 * Dobehnout dokonce kliknuti z GUI
	 */
	public static void runToEnd(){
		isRunningOutput = true;
		boolean isNotErrorEvent;
		do{
			isNotErrorEvent = nextStepEvent()[0];

			//rychlost vypisu
			try {
				Thread.sleep((long) waitTimeMs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		while(isNotErrorEvent && isRunningOutput);
	}

	/**
	 * Z atributu vstup nacte data a ulozi je do jednotlivych poli
	 * 
	 * @param vstup			vstupni data
	 */
	private static void setUp(String vstup) {
		try (Scanner sc = new Scanner(vstup)) {
			sc.useLocale(Locale.US);

			int nodesId = 0;//id vrcholu pro pridani id do instanci Sklad, Oaza

			PriorityQueue<Event> events = new PriorityQueue<>();

			int stockCount = sc.nextInt();
			for (int i = 0; i < stockCount; i++) {
				Stock stock = new Stock(nodesId++, sc.nextDouble(), sc.nextDouble(), sc.nextInt(), sc.nextDouble(), sc.nextDouble());
				//System.out.println("BasketMakingTime: " + stock.basketMakingTime);
				graph.addNode(stock);//pridani vrcholu do grafu
				//System.out.println("setup stock.loadingTime=" + stock.loadingTime);
				events.add(new Event(stock.basketMakingTime, EventType.StorageRefill, i));    //vytvori skladu event typu storageRefill
			}

			int oasisCount = sc.nextInt();
			for (int i = 0; i < oasisCount; i++) {
				Oasis oasis = new Oasis(nodesId++, sc.nextDouble(), sc.nextDouble());
				graph.addNode(oasis);//pridani vrcholu do grafu
			}

			int edgesCount = sc.nextInt();
			for (int i = 0; i < edgesCount; i++) {
				graph.addEdge(graph.getNodebyId(sc.nextInt() - 1), graph.getNodebyId(sc.nextInt() - 1));
			}

			CamelType[] camelTypes = new CamelType[sc.nextInt()];
			for (int i = 0; i < camelTypes.length; i++) {
				camelTypes[i] = new CamelType(sc.next(), sc.nextDouble(), sc.nextDouble(),
						sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.nextInt(), sc.nextDouble());
			}
			Camel.setTypes(camelTypes);

			//Tasks maji stejny index jako Event typ:NewTask - index
			Task[] tasks = new Task[sc.nextInt()];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i] = new Task(sc.nextDouble(), sc.nextInt(), sc.nextInt(), sc.nextDouble());
				events.add(new Event(tasks[i].arrivalTime, EventType.NewTask, i));    //vytvori event typu newTask
			}

			manager = new EventManager(events, tasks, stockCount);
		}
		
	}

	/**
	 * Zmena rychlosti vypisu z GUI
	 * prevod na cekaci cas
	 * @param speedValue vybrana hodnota
	 * @param speedMin maximalni hodnota
	 * @param speedMax minimalni hodnota
	 */
	public static void changeSpeed(double speedValue, double speedMin, double speedMax) {
		double convertedSpeedValue = speedValue / (speedMax - speedMin + 1);

		double finalWaitTime = ((1 - convertedSpeedValue) * (waitTimeMsMax - waitTimeMsMin)) + waitTimeMsMin;

		waitTimeMs = finalWaitTime;
		//System.out.println(waitTimeMs);
	}

	/**
	 * Aktualni stav prepravy kliknuti z GUI
	 * @return String se zpravou
	 */
	public static String getAppReport(){
		StringBuilder returnStr = new StringBuilder();

		returnStr.append("PRACUJE SE NA POZADAVCICH:").append("\n");
		for (Task task : manager.getProcessingTasks()) {
			returnStr.append(task.toString()).append("\n");
		}
		returnStr.append("\n");

		returnStr.append("ZPRACOVANE POZADAVKY:").append("\n");
		for (Task task : manager.getFinishedTasks()) {
			returnStr.append(task.toString()).append("\n");
		}
		returnStr.append("\n");

		returnStr.append("STAV SKLADU:").append("\n");
		for (AbstractNode node: graph.getNodesList()){
			if (node.getClass() == Oasis.class) {continue;}

			returnStr.append(node).append("\n");
		}

		return returnStr.toString();
	}
	
	/**
	 * Vytvori soubor Statistics.txt a zapise do nej statistiky z cele simulace
	 * @throws FileNotFoundException pokud se nepodari soubor vytvorit
	 */
	public static void makeOutputFile() throws FileNotFoundException {
		String out;
		System.setOut(new PrintStream(new File("Statistics.txt")));
		if(manager.tasks.length != manager.getFinishedTasks().size()) {
			System.out.println("Vsichni vymreli, Harpagon zkrachoval, Konec simulace");
			System.exit(0);
		}

		String[] camelStats = camelStats();
		out = camelStats[0];
		double travelAll = Double.parseDouble(camelStats[1]);
		double waitAll = Double.parseDouble(camelStats[2]);
		out += taskStats();
		out += stockStats();
		out += String.format(Locale.US, " Delka simulace: %.2f\nCelkova usla vzdalenost: %.2f\nCelkova doba cekani: %.2f\n",manager.endTime, travelAll, waitAll);
		for(CamelType typ: Camel.getCamelTypes()) {
			out += "Jedincu druhu " + typ.name + " vygenerovano celkem: " + typ.count + "\n";
			}
		System.out.print(out);

		System.setOut(GUI.getPrintStream());
	}

	/**
	 * Vypis statistik o Skladech
	 */
	private static String stockStats() {
		String out = "Sklady:\n";
		for(int i = 0; i < manager.stockCount; i++) {
			Stock stock = (Stock) graph.getNodebyId(i);
			out += String.format(Locale.US, " Sklad %d doplnen %dkrat:\n", i+1, stock.refills.size());
			for(int j = 0; j < stock.refills.size(); j++) {
				out += "  " + (j+1) + " - " + stock.getArchivedRefill(j) + "\n";
			}
		}
		return out;
	}

	/**
	 * Vypis statistik o Pozadavcich
	 */
	private static String taskStats() {
		String out = "Celkovy pocet pozadavku: " + manager.tasks.length + "\n";
		for(int i = 0; i < manager.tasks.length; i++) {
			Task task = manager.tasks[i];
			out += String.format(Locale.US, " %d - Prichod: %.2f, Deadline: %.2f, Doruceno v %.2f ze skladu %d velbloudem %s\n",
							i+1, task.arrivalTime, task.deadline, task.finishTime, task.finishCamel.home.id+1, task.finishCamel.name);
		}
		return out;
	}

	/**
	 * Vypis statistik o velbloudech
	 */
	private static String[] camelStats() {
		double allTravel = 0;
		double allWait = 0;
		String out = "";
		int count = 0;
		for(int i = 0; i < manager.stockCount; i++) {
			Stock stock = (Stock) graph.getNodebyId(i);
			out += "Pocet ve skladu " + (i+1) + " je " + stock.camelSet.size() +":\n";
			count += stock.camelSet.size();
			for(Camel camel: stock.camelSet) {
				String camelsPath = "";
				double waitTime = manager.endTime - camel.generatedTime;
				allWait += waitTime;
				out += String.format(Locale.US,"  %s - Druh: %s, Rychl: %.2f, Ujde: %.2f, Trasy: ",
						camel.name, camel.type.name, camel.getSpeed(), camel.getMaxDistance());
				if(camel.paths != null) {
					out += camel.paths.size();
					double travelDistance = 0;
					double travelTime = 0;
					for(MyPath path: camel.paths) {
						travelDistance += path.getFullDistance() * 2;
						travelTime += path.getTravelTime(camel);
						camelsPath += camelsPath(path.getEdgesArr(), camel);
					}
					allTravel += travelDistance;
					allWait -= travelTime;
					out += String.format(Locale.US,", Usel: %.2f, Cekal: %.2f\n", travelDistance, waitTime - travelTime);
				} else {
					out += String.format(Locale.US,"0, Usel: 0.00, Cekal: %.2f\n", manager.endTime - camel.generatedTime);
				}
				out += camelsPath;
			}
		}
		out = String.format(Locale.US,"Celkovy pocet velbloudu: %d\n %s", count, out);
		String[] ret = {out, "" + allTravel, "" + allWait};
		return ret;
	}

	/**
	 * Vypis statistik o vypisech velbloudu a jejich cestovani
	 */
	private static String camelsPath(Edge[] edgesArr, Camel camel) {
    	double distance = 0;
    	String path = "\tSklad " + (camel.home.id+1) + " ";
    	for(int i = 1; i < edgesArr.length; i++) {
    		Edge e = edgesArr[i];
    		distance += e.getWeight();
    		if(distance > camel.distance) {
    			distance = 0;
    			path += "-> " + node(e.getStartNode().id) + "(pije) ";
    		} else {
    			path += "-> " + node(e.getStartNode().id) + "(prochazi) ";
    		}
    	}
    	path += "-> " + node(edgesArr[edgesArr.length-1].getEndNode().id) + "\n";

		return path;
	}

	/**
	 * Vypis statistik o vrcholu
	 * @param id vrcholu v tasks
	 */
	private static String node(int id) {
		String ret;
		if(id >= manager.stockCount) {
			ret = "Oaza " + (id-manager.stockCount+1);
		} else {
			ret = "Sklad " + (id+1);
		}

		return ret;
	}

}
