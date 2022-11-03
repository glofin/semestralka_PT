import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {

	/** graf reprezentujici mapu */
	public static Graph graph = Graph.getInstance();
	
	private static EventManager manager;
	
	public static void main(String[] args) throws Exception {
		
		try {
			String input = fileToString("data/tutorial.txt");
			setUp(input);
			//System.out.println(graph.toString());//vypis grafu
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		while(true) {
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
	 * @throws IOException	pri spatnem formatu vstupniho souboru
	 */
	private static void setUp(String vstup) {
		Scanner sc = null;
		try {
			sc = new Scanner(vstup);
			sc.useLocale(Locale.US);

			int nodesId = 0;//id vrcholu pro pridani id do instanci Sklad, Oaza
			
			PriorityQueue<Event> events = new PriorityQueue<Event>();
			
			int sklady = sc.nextInt();
			for(int i = 0; i < sklady; i++) {
				Sklad stock = new Sklad(nodesId++, sc.nextDouble(),sc.nextDouble(),sc.nextInt(),sc.nextDouble(),sc.nextDouble());
				//System.out.println("BasketMakingTime: " + stock.basketMakingTime);
				graph.addNode(stock);//pridani vrcholu do grafu
				events.add(new Event(stock.loadingTime, EventType.StorageRefill, i));	//vytvori skladu event typu storageRefill
			}
			
			int oazy = sc.nextInt();
			for(int i = 0; i < oazy; i++) {
				Oaza oasis = new Oaza(nodesId++, sc.nextDouble(),sc.nextDouble());
				graph.addNode(oasis);//pridani vrcholu do grafu
			}
			
			int edgesCount = sc.nextInt();
			for(int i = 0; i < edgesCount; i++) {
				graph.addEdge(graph.getNodebyId(sc.nextInt() - 1), graph.getNodebyId(sc.nextInt() - 1));//TODO zmenit getNodebyId
			}
			
			DruhVelblouda[] DruhyVelblouda = new DruhVelblouda[sc.nextInt()];
			for(int i = 0; i < DruhyVelblouda.length; i++) {
				DruhyVelblouda[i] = new DruhVelblouda(sc.next(), sc.nextDouble(), sc.nextDouble(),
						sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.nextInt(), sc.nextDouble());
			}
			Velbloud.setDruhy(DruhyVelblouda);
			
			Task[] tasks =  new Task[sc.nextInt()];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i] = new Task(sc.nextDouble(), sc.nextInt(), sc.nextInt(), sc.nextDouble());
				events.add(new Event(tasks[i].arrivalTime, EventType.NewTask, i));	//vytvori event typu newTask
			}
			
			manager = new EventManager(events, tasks, sklady);
			
		} finally {
			sc.close();
		}
		
	}
	
}
