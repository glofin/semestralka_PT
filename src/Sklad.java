import java.util.SortedSet;

/**
 * Instance tridy {@code Sklad} predstavuji jednotlive sklady, ze kterych jsou vysilani velbloudi s nakladem kosu
 */
public class Sklad extends AbstractNode {

	/** Aktualni pocet kosu ve skladu */
	int basketCount;
	
	/** doba, za kterou se ve skladu vytvori nove kose */
	final double BASKET_MAKING_TIME;
	/** doba potrebna pro nalozeni kosu na velblouda */
	final double LOADING_TIME;
	
	/** Pocet kosu pri inicializaci a zaroven pocet nove vytvorenych kosu pri jejich vytvareni */
	final int NEWBASKETS;
	
	SortedSet<Velbloud> set;
	
	
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
		NEWBASKETS = basketCount;
		BASKET_MAKING_TIME = basketMakingTime;
		LOADING_TIME = loadingTime;
	}
	
	/**
	 * Vytvori ve skladu nove kose
	 */
	public void makeBaskets() {
		basketCount += NEWBASKETS;
	}
	
	
}
