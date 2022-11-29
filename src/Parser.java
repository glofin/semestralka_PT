import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();
	
	private static EventManager manager;
	
	/*public static void main(String[] args) throws Exception {
		
		try {
			String input = fileToString("data/centre_small.txt");
			setUp(input);
			//System.out.println(graph.toString());//vypis grafu
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		while(true) {
			manager.nextEvent();
		}
	}*/

	/**
	 * metoda precte soubor z fileName a nastaviho do datovych struktur
	 * pouzivanych v aplikaci a nastavi eventManager
	 * @param fileName jmeno souboru kde jsou data pro chod aplikace
	 * @throws Exception chyba ve cteni
	 */
	public static void readFile(String fileName) throws Exception {
		try {
			String input = fileToString(fileName);
			setUp(input);
			//System.out.println(graph.toString());//vypis grafu
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		while(true) {//TODO konci jen vyjimkou predelat
			manager.nextEvent();
		}
	}
	
	/**
	 * Nacte cely vstupni soubor a ulozi ho do Stringu, ze ktereho pak odstrani komentare
	 * 
	 * @param file		cely nazev vstupniho souboru
	 * @return				vstupni soubor jako String bez komentaru
	 * @throws IOException	chyba ve vstupnim souboru nebo jeho jmene
	 */
	private static String fileToString(String file) throws IOException {
		String s = new String(Files.readAllBytes(Paths.get(file)));

		int end;
		int start = 0;
		while((end = s.indexOf("🏜", start)) > 0) {
			start = s.lastIndexOf("🐪", end);
			s = (s.substring(0, start)) + " " + (s.substring(end+2));
		}
	
		return s;
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
