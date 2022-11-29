import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();

	/** manager pro zpracovavani pozadavku a dalsich eventu*/
	private static EventManager manager;

	/**
	 * metoda precte soubor z fileName a nastaviho do datovych struktur
	 * pouzivanych v aplikaci a nastavi eventManager
	 */
	public static void main(String[] args) {

		//NACTENI SOUBORU
		try {
			String input = Parser.fileToString("data/centre_small.txt");
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

			int sklady = sc.nextInt();
			for (int i = 0; i < sklady; i++) {
				Stock stock = new Stock(nodesId++, sc.nextDouble(), sc.nextDouble(), sc.nextInt(), sc.nextDouble(), sc.nextDouble());
				//System.out.println("BasketMakingTime: " + stock.basketMakingTime);
				graph.addNode(stock);//pridani vrcholu do grafu
				events.add(new Event(stock.loadingTime, EventType.StorageRefill, i));    //vytvori skladu event typu storageRefill
			}

			int oazy = sc.nextInt();
			for (int i = 0; i < oazy; i++) {
				Oasis oasis = new Oasis(nodesId++, sc.nextDouble(), sc.nextDouble());
				graph.addNode(oasis);//pridani vrcholu do grafu
			}

			int edgesCount = sc.nextInt();
			for (int i = 0; i < edgesCount; i++) {
				graph.addEdge(graph.getNodebyId(sc.nextInt() - 1), graph.getNodebyId(sc.nextInt() - 1));//TODO zmenit getNodebyId
			}

			CamelType[] DruhyVelblouda = new CamelType[sc.nextInt()];
			for (int i = 0; i < DruhyVelblouda.length; i++) {
				DruhyVelblouda[i] = new CamelType(sc.next(), sc.nextDouble(), sc.nextDouble(),
						sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.nextInt(), sc.nextDouble());
			}
			Camel.setDruhy(DruhyVelblouda);

			Task[] tasks = new Task[sc.nextInt()];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i] = new Task(sc.nextDouble(), sc.nextInt(), sc.nextInt(), sc.nextDouble());
				events.add(new Event(tasks[i].arrivalTime, EventType.NewTask, i));    //vytvori event typu newTask
			}

			manager = new EventManager(events, tasks, sklady);
		}
		
	}
	
	
	/**
	 * Spoji pole skladu a pole velbloudu do jednoho pole reprezentujici vrcholy grafu
	 * 
	 * @param sklady
	 * @param oazy
	 */
	/**
	private static void udelejVrcholy(Sklad[] sklady, Oaza[] oazy) {
		AbstractNode[] locations = new AbstractNode[oazy.length + sklady.length];
		for(int i = 0, j = 0; i < locations.length; i++, j++) {
			if(j < sklady.length) {
				locations[i] = sklady[j];
			} else {
				locations[i] = oazy[j - sklady.length];
			}
		}
		EventManager.locations = locations;
	}
	*/
}
