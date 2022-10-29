import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class Parser {
	
	public static void main(String[] args) {
		
		try {
			String input = souborDoStringu("data/parser.txt");
			nacti(input);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		EventManager.timeline();

	}
	
	/**
	 * Nacte cely vstupni soubor a ulozi ho do Stringu, ze ktereho pak odstrani komentare
	 * 
	 * @param file		cely nazev vstupniho souboru
	 * @return				vstupni soubor jako String bez komentaru
	 * @throws IOException	chyba ve vstupnim souboru nebo jeho jmene
	 */
	private static String souborDoStringu(String file) throws IOException {
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
	private static void nacti(String vstup) {
		Scanner sc = null;
		try {
			sc = new Scanner(vstup);
			sc.useLocale(Locale.US);
			
			Sklad[] sklady = new Sklad[sc.nextInt()];
			for(int i = 0; i < sklady.length; i++) {
				sklady[i] = new Sklad(sc.nextDouble(),sc.nextDouble(),sc.nextInt(),sc.nextDouble(),sc.nextDouble());
				EventManager.events.add(new Event(sklady[i].loadingTime, EventType.StorageRefill, i));	//vytvori skladu event typu storageRefill
			}
			EventManager.sklady = sklady;
			
			Oaza[] oazy = new Oaza[sc.nextInt()];
			for(int i = 0; i < oazy.length; i++) {
				oazy[i] = new Oaza(sc.nextDouble(),sc.nextDouble());
			}
			EventManager.oazy = oazy;
			
			udelejVrcholy(sklady, oazy);
			
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
			
			Task[] tasks =  new Task[sc.nextInt()];
			for (int i = 0; i < tasks.length; i++) {
				tasks[i] = new Task(sc.nextDouble(), sc.nextInt(), sc.nextInt(), sc.nextDouble());
				EventManager.events.add(new Event(tasks[i].arrivalTime, EventType.NewTask, i));	//vytvori pozadavku event typu newTask
			}
			EventManager.tasks = tasks;
			
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
	
}
