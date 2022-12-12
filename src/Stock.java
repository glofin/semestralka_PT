import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Instance tridy {@code Stock} predstavuji jednotlive sklady, ze kterych jsou vysilani velbloudi s nakladem kosu
 */
public class Stock extends AbstractNode {

	/** Aktualni pocet kosu ve skladu */
	int basketCount;
	/** Cas kdy se aktualizoval naposledy pocet kosu ve skladu
	 *  kdyz se generuji kose v dany cas u EventManager*/
	double basketCounRefreshTime;
	
	/** doba, za kterou se ve skladu vytvori nove kose */
	final double basketMakingTime;
	/** doba potrebna pro nalozeni kosu na velblouda */
	final double loadingTime;
	
	/** Pocet kosu pri inicializaci a zaroven pocet nove vytvorenych kosu pri jejich vytvareni */
	final int newBaskets;
	
	/** Mnozina vsech velbloudu, kteri jsou aktualne ve skladu */
	SortedSet<Camel> camelSet;
	
	/** Seznam vsech doplneni skladu behem simulace */
	List<Refill> refills = new ArrayList<Refill>();
	
	
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
		basketCounRefreshTime = 0;
		newBaskets = basketCount;
		this.basketMakingTime = basketMakingTime;
		this.loadingTime = loadingTime;
		camelSet = new TreeSet<Camel>();
	}
	
	/**
	 * Vytvori ve skladu nove kose
	 */
	public void makeBaskets(double time) {
		refills.add(new Refill(time, basketCount, basketCount+newBaskets));
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
		assert basketCount<0 : "kose jdou ve skladu: " + id + " do minusu";
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
	
	public String getArchivedRefill(int i) {
		return refills.get(i).toString();
	}

	@Override
	public String toString() {
		String s ="Sklad - " +
				", id=" + id +
				", pocet kosu=" + basketCount +
				", velbloudi={";
		StringBuilder returnStr = new StringBuilder(s);
		returnStr.append("\n");
		for (Camel camel :
				camelSet) {
			returnStr.append("    ").append(camel.toString()).append("\n");
		}
		returnStr.append("}");
		return returnStr.toString();
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

	private static class Refill{
		
		public final double time;
		public final int before;
		public final int after;
		
		public Refill(double time, int before, int after) {
			this.time = time;
			this.before = before;
			this.after = after;
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US, "Cas: %.2f, Pred: %d, Po: %d", time, before, after);
		}
	}

}
