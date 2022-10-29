import java.util.SortedSet;

/**
 * Instance tridy {@code Sklad} predstavuji jednotlive sklady, ze kterych jsou vysilani velbloudi s nakladem kosu
 */
public class Sklad extends AbstractNode {

	int pocetKosu;
	int casDoplneni;
	int casNalozeni;
	
	/** Pocet kosu pri inicializaci a zaroven pocet nove vytvorenych kosu pri jejich vytvareni */
	private final int pocetKosuDefault;
	
	
	
	SortedSet<Velbloud> set;
	
	
	
	
	
	public Sklad(double x, double y, int pocetKosu, int casDoplneni, int casNalozeni) {
		this.x = x;
		this.y = y;
		this.pocetKosu = pocetKosu;
		this.pocetKosuDefault = pocetKosu;
		this.casDoplneni = casDoplneni;
		this.casNalozeni = casNalozeni;
	}
	
	/**
	 * Vytvori ve skladu nove kose
	 */
	public void vytvorKose() {
		pocetKosu += pocetKosuDefault;
	}
	
	
}
