import java.util.TreeSet;

/**
 * Instance tridy {@code Sklad} predstavuji jednotlive sklady, ze kterych jsou vysilani velbloudi s nakladem kosu
 */
public class Sklad extends AbstractNode {

	/** Aktualni pocet kosu ve skladu */
	int basketCount;
	
	/** doba, za kterou se ve skladu vytvori nove kose */
	final double basketMakingTime;
	/** doba potrebna pro nalozeni kosu na velblouda */
	final double loadingTime;
	
	/** Pocet kosu pri inicializaci a zaroven pocet nove vytvorenych kosu pri jejich vytvareni */
	final int newBaskets;
	
	TreeSet<Velbloud> set;
	
	
	/**
	 * @param x					x-ova souradnice skladu
	 * @param y					y-ova souradnice skladu
	 * @param basketCount		aktualni pocet kosu ve skladu
	 * @param basketMakingTime	doba, za kterou se ve skladu vytvori nove kose
	 * @param loadingTime
	 */
	public Sklad(double x, double y, int basketCount, double basketMakingTime, double loadingTime) {
		this.x = x;
		this.y = y;
		this.basketCount = basketCount;
		newBaskets = basketCount;
		this.basketMakingTime = basketMakingTime;
		this.loadingTime = loadingTime;
		set = new TreeSet<Velbloud>();
	}
	
	/**
	 * Vytvori ve skladu nove kose
	 */
	public void makeBaskets() {
		basketCount += newBaskets;
	}
	
	
}
