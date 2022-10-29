import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class Main {
	
	static AbstractNode[] lokace;
	static Sklad[] sklady;
	static Oaza[] oazy;
	
	
	public static void main(String[] args) {
		
		try {
			String input = souborDoStringu("data/centre_small.txt");
			nacti(input);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

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
				sklady[i] = new Sklad(sc.nextDouble(),sc.nextDouble(),sc.nextInt(),sc.nextInt(),sc.nextInt());
			}
			
			oazy = new Oaza[sc.nextInt()];
			for(int i = 0; i < oazy.length; i++) {
				oazy[i] = new Oaza(sc.nextDouble(),sc.nextDouble());
			}
			
			udelejVrcholy();
			
			//ukladani cest do pole je pitomost a zatim slouzi jenom k ukladani a kontrole vstupniho parseru
			Edge[] cesty = new Edge[sc.nextInt()];
			for(int i = 0; i < cesty.length; i++) {
				cesty[i] = new Edge(lokace[sc.nextInt() - 1], lokace[sc.nextInt() - 1]);
			}
			
			DruhVelblouda[] DruhyVelblouda = new DruhVelblouda[sc.nextInt()];
			for(int i = 0; i < DruhyVelblouda.length; i++) {
				DruhyVelblouda[i] = new DruhVelblouda(sc.next(), sc.nextDouble(), sc.nextDouble(),
						sc.nextDouble(), sc.nextDouble(), sc.nextInt(), sc.nextInt(), sc.nextDouble());
			}
			Velbloud.setDruhy(DruhyVelblouda);
			
			Pozadavek[] pozadavky =  new Pozadavek[sc.nextInt()];
			for (int i = 0; i < pozadavky.length; i++) {
				pozadavky[i] = new Pozadavek(sc.nextDouble(), sc.nextInt(), sc.nextInt(), sc.nextDouble());
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
		lokace = new AbstractNode[oazy.length + sklady.length];
		for(int i = 0, j = 0; i < lokace.length; i++, j++) {
			if(j < sklady.length) {
				lokace[i] = sklady[j];
			} else {
				lokace[i] = oazy[j - sklady.length];
			}
		}
	}
	
	
}
