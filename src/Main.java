import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {
	
	/** Vrcholy grafu */
	static AbstractNode[] locations;
	/** Vsechny sklady */
	static Sklad[] sklady;
	/** Vsechny oazy */
	static Oaza[] oazy;
	/** Vsechny pozadavky (i nesplnene) */
	static Task[] tasks;
	/** Prioritni fronta nadchazejicich eventu serazena podle toho, kdy maji nastat */
	static PriorityQueue<Event> events;
	
	
	public static void main(String[] args) {
		
		events = new PriorityQueue<Event>();
		try {
			String input = souborDoStringu("data/centre_small.txt");
			nacti(input);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		eventManager();

	}
	
	/**
	 * Nacte cely vstupni soubor a ulozi ho do Stringu, ze ktereho pak odstrani komentare
	 * 
	 * @param soubor		cely nazev vstupniho souboru
	 * @return				vstupni soubor jako String bez komentaru
	 * @throws IOException	chyba ve vstupnim souboru nebo jeho jmene
	 */
	private static String souborDoStringu(String soubor) throws IOException {
		String s = new String(Files.readAllBytes(Paths.get(soubor)));

		int end;
		int start = 0;
		while((end = s.indexOf("đźŹś", start)) > 0) {
			start = s.lastIndexOf("đź�Ş", end);
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
	private static void nacti(String vstup) {
		Scanner sc = null;
		try {
			sc = new Scanner(vstup);
			sc.useLocale(Locale.US);
			
			sklady = new Sklad[sc.nextInt()];
			for(int i = 0; i < sklady.length; i++) {
				sklady[i] = new Sklad(sc.nextDouble(),sc.nextDouble(),sc.nextInt(),sc.nextDouble(),sc.nextDouble());
				events.add(new Event(sklady[i].loadingTime, EventType.StorageRefill, i));	//vytvori skladu event typu storageRefill
			}
			
			oazy = new Oaza[sc.nextInt()];
			for(int i = 0; i < oazy.length; i++) {
				oazy[i] = new Oaza(sc.nextDouble(),sc.nextDouble());
			}
			
			udelejVrcholy();
			
			//cesty se zatim nijak neukladaji
			int c = sc.nextInt();
			for(int i = 0; i < c; i++) {
				sc.nextInt(); sc.nextInt();
			}
			
			DruhVelblouda[] DruhyVelblouda = new DruhVelblouda[sc.nextInt()];
			for(int i = 0; i < DruhyVelblouda.length; i++) {
				DruhyVelblouda[i] = new DruhVelblouda(sc.next(), sc.nextDouble(), sc.nextDouble(),
						sc.nextDouble(), sc.nextDouble(), sc.nextDouble(), sc.nextInt(), sc.nextDouble());
			}
			Velbloud.setDruhy(DruhyVelblouda);
			
			tasks =  new Task[sc.nextInt()];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i] = new Task(sc.nextDouble(), sc.nextInt(), sc.nextInt(), sc.nextDouble());
				events.add(new Event(tasks[i].arrivalTime, EventType.NewTask, i));	//vytvori pozadavku event typu newTask
			}
			
		} finally {
			sc.close();
		}
		
	}
	
	/**
	 * Spoji pole skladu a pole velbloudu do jednoho pole reprezentujici vrcholy grafu
	 * 
	 * @param sklady
	 * @param oazy
	 */
	private static void udelejVrcholy() {
		locations = new AbstractNode[oazy.length + sklady.length];
		for(int i = 0, j = 0; i < locations.length; i++, j++) {
			if(j < sklady.length) {
				locations[i] = sklady[j];
			} else {
				locations[i] = oazy[j - sklady.length];
			}
		}
	}
	
	
	private static void eventManager() {
		while(true) {
			Event e = events.poll();
			switch(e.type) {
				case StorageRefill:
					sklady[e.index].makeBaskets();
					events.add(new Event(e.time + sklady[e.index].loadingTime, EventType.StorageRefill, e.index));
					break;
				
				case NewTask:
					Task t = tasks[e.index];
					System.out.printf("Cas: %f, Pozadavek: %d, Oaza: %d, Pocet kosu: %d, Deadline: %f\n", 
													t.arrivalTime,
													e.index + 1,
													t.oaza,
													t.basketCount,
													t.deadline);
					if(e.index == tasks.length - 1)
						System.exit(0);
					//TODO zpracovat pozadavek t a vytvorit nove eventy
					break;
				
				default:
					System.exit(0);						
			} //konec switch
		} //konec while
	}
	
}
