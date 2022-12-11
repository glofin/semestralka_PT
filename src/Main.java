import java.io.IOException;
import java.util.*;

public class Main {
	private static Main messenger;

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();

	private static double waitTimeMsMin = 0;
	private static double waitTimeMsMax = 1000;
	private static double waitTimeMs = waitTimeMsMax / 2;

	public static GUI gui = GUI.getInstance();

	public static int i = 0;

	/** manager pro zpracovavani pozadavku a dalsich eventu*/
	private static EventManager manager;
	public static Main getInstance(){
		if(messenger==null) {
			messenger = new Main();
		}
		return messenger;
	}

	private void Main(){};

	/**
	 * metoda precte soubor z fileName a nastaviho do datovych struktur
	 * pouzivanych v aplikaci a nastavi eventManager
	 */
	public static void main(String[] args) {

		//NACTENI SOUBORU
		try {
			String input = Parser.fileToString("data/dense_small.txt");
			setUp(input);
			//System.out.println(graph.toString());//vypis grafu
		} catch (IOException e) {//chyba ve vstupnim souboru nebo jeho jmene
			e.printStackTrace();
			System.exit(0);
		}

		//ZPRACOVANI POZADAVKU
		boolean isErrorEvent;
		do{
			isErrorEvent = manager.nextEvent();
		}
		while(isErrorEvent);
	}

	public static void start(String fileName) {
		//System.out.println(fileName);
		//NACTENI SOUBORU
		try {
			String input = Parser.fileToString(fileName);
			setUp(input);
			//System.out.println(graph.toString());//vypis grafu
		} catch (IOException e) {//chyba ve vstupnim souboru nebo jeho jmene
			e.printStackTrace();
			System.exit(0);
		}
		/*int i = 0;
		while (i!=1000000){
			i++;
			System.out.println(i);
			//GUI.stopController();
			gui.addToOutputGUI(i + "\n");
		}*/

		//ZPRACOVANI POZADAVKU
		boolean isErrorEvent;
		do{
			try {
				Thread.sleep((long) waitTimeMs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			isErrorEvent = manager.nextEvent();
		}
		while(isErrorEvent);


	}

	public static void start2() {
		while (true) {
			i++;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			System.out.println(i);
			GUI.getInstance().addToOutputGUI(i + "\n");
		}
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
				events.add(new Event(stock.loadingTime, EventType.StorageRefill, i));    //vytvori skladu event typu storageRefill
			}

			int oasisCount = sc.nextInt();
			for (int i = 0; i < oasisCount; i++) {
				Oasis oasis = new Oasis(nodesId++, sc.nextDouble(), sc.nextDouble());
				graph.addNode(oasis);//pridani vrcholu do grafu
			}

			int edgesCount = sc.nextInt();
			for (int i = 0; i < edgesCount; i++) {
				graph.addEdge(graph.getNodebyId(sc.nextInt() - 1), graph.getNodebyId(sc.nextInt() - 1));//TODO zmenit getNodebyId
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

	public static void changeSpeed(double speedValue, double speedMin, double speedMax) {
		double convertedSpeedValue = speedValue / (speedMax - speedMin + 1);

		double finalWaitTime = ((1 - convertedSpeedValue) * (waitTimeMsMax - waitTimeMsMin)) + waitTimeMsMin;

		waitTimeMs = finalWaitTime;
		System.out.println(waitTimeMs);
	}
}
