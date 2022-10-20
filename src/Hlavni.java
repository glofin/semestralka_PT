import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class Hlavni {
	
	public static void main(String[] args) {
		
		try {
			String vstup = souborDoStringu("data/centre_small.txt");
			nacti(vstup);
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

		int konec;
		int zacatek = 0;
		while((konec = s.indexOf("đźŹś", zacatek)) > 0) {
			zacatek = s.lastIndexOf("đź�Ş", konec);
			s = (s.substring(0, zacatek)) + " " + (s.substring(konec+2));
		}
	
		return s;
	}

	/**
	 * Z atributu vstup nacte data a ulozije do jednotlivych poli
	 * 
	 * @param vstup			vstupni data 
	 * @throws IOException	
	 */
	private static void nacti(String vstup) {
		
		Scanner sc = new Scanner(vstup);
		sc.useLocale(Locale.US);
		
		Sklad[] sklady = new Sklad[sc.nextInt()];
		for(int i = 0; i < sklady.length; i++) {
			sklady[i] = new Sklad(sc.nextDouble(),sc.nextDouble(),sc.nextInt(),sc.nextInt(),sc.nextInt());
		}
		
		Oaza[] oazy = new Oaza[sc.nextInt()];
		for(int i = 0; i < oazy.length; i++) {
			oazy[i] = new Oaza(sc.nextDouble(),sc.nextDouble());
		}
		
		//cesty zatim preskoci a nijak je neuklada 
		int cesty = sc.nextInt();
		for(int i = 0; i < cesty; i++) {
			sc.nextInt();	sc.nextInt();
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
		
		sc.close();
		
	}
	
	
	
}
