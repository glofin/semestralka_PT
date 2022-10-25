
/**
 *  Hrany grafu (zatim existuje jen pro ukladani vstupnich dat
 */
public class Edge {

	AbstractNode a;
	AbstractNode b;
	double vzdalenost;
	
	public Edge(AbstractNode a, AbstractNode b) {
		this.a = a;
		this.b = b;
		vzdalenost = Math.sqrt( Math.abs(a.x - b.x) + Math.abs(a.y - b.y) );
	}
	
	
}
