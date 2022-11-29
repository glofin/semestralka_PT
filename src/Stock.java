import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Instance tridy {@code Stock} predstavuji jednotlive sklady, ze kterych jsou vysilani velbloudi s nakladem kosu
 */
public class Stock extends AbstractNode {

	/** Aktualni pocet kosu ve skladu */
	int basketCount;
	
	/** doba, za kterou se ve skladu vytvori nove kose */
	final double basketMakingTime;
	/** doba potrebna pro nalozeni kosu na velblouda */
	final double loadingTime;
	
	/** Pocet kosu pri inicializaci a zaroven pocet nove vytvorenych kosu pri jejich vytvareni */
	final int newBaskets;
	
	/** Mnozina vsech velbloudu, kteri jsou aktualne ve skladu */
	SortedSet<Camel> camelSet;
	
	
	/**
	 * @param x					x-ova souradnice skladu
	 * @param y					y-ova souradnice skladu
	 * @param basketCount		aktualni pocet kosu ve skladu
	 * @param basketMakingTime	doba, za kterou se ve skladu vytvori nove kose
	 * @param loadingTime
	 */
	public Stock(int id, double x, double y, int basketCount, double basketMakingTime, double loadingTime) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.basketCount = basketCount;
		newBaskets = basketCount;
		this.basketMakingTime = basketMakingTime;
		this.loadingTime = loadingTime;
		camelSet = new TreeSet<Camel>();
	}
	
	/**
	 * Vytvori ve skladu nove kose
	 */
	public void makeBaskets() {
		basketCount += newBaskets;
	}

	public SortedSet<Camel> getSet() {
		return camelSet;
	}

	public void addCamelToSet(Camel camel) {
		camelSet.add(camel);
	}
	public void removeCamelFromSet(Camel camel){
		if(!camelSet.remove(camel)) {
			System.out.println("Velblouda se nepodarilo odstranit ze skladu");
		}
	}

	public void removeBaskets(int count){
		basketCount -= count;
	}

	public int getBasketCount() {
		return basketCount;
	}

	public double getBasketMakingTime() {
		return basketMakingTime;
	}

	public double getLoadingTime() {
		return loadingTime;
	}

	public int getNewBaskets() {
		return newBaskets;
	}

	public SortedSet<Camel> getCamelSet() {
		return camelSet;
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
