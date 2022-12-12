import java.util.HashMap;
import java.util.Map;

/**
 *  Vrcholy grafu
 */
public abstract class AbstractNode implements Comparable<AbstractNode> {

	/** id vrcholu v poradi zadanem ze souboru */
	protected int id;

	/** Souradnice x */
	protected double x;
	/** Souradnice y */
	protected double y;

	/** distance pro dijkstra na hledani nejkratsi cesty*/
	protected double distance = Double.MAX_VALUE;

	/** zaznam z dajkstry pocitajici do 1.Node je nejkratsi cesta pres 2.Node*/
	Map<AbstractNode, AbstractNode> shortestPaths = new HashMap<>();

	public int getId() {
		return id;
	}

	public double getDistance() {
		return distance;
	}

	public void putInShortestPaths(AbstractNode sourceNode, AbstractNode nextNode){
		shortestPaths.put(sourceNode, nextNode);
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Map<AbstractNode, AbstractNode> getShortestPaths() {
		return shortestPaths;
	}
}

