import java.util.SortedSet;
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
	
	/** Mnozina vsech velbloudu, kteri jsou aktualne ve skladu */
	SortedSet<Velbloud> set;
	
	
	/**
	 * @param x					x-ova souradnice skladu
	 * @param y					y-ova souradnice skladu
	 * @param basketCount		aktualni pocet kosu ve skladu
	 * @param basketMakingTime	doba, za kterou se ve skladu vytvori nove kose
	 * @param loadingTime
	 */
	public Sklad(int id, double x, double y, int basketCount, double basketMakingTime, double loadingTime) {
		this.id = id;
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

	@Override
	public String toString() {
		return "Stock{" +
				"id=" + id +
				", x=" + x +
				", y=" + y +
				'}';
	}

	/**
	 * pro serazeni adjNodes v Graph podle id
	 * @param o the object to be compared.
	 * @return
	 */
	@Override
	public int compareTo(AbstractNode o) {
		return this.id - o.id;
	}
	
	
}
