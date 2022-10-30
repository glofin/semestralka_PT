/**
 *  Hrany grafu
 */
public class Edge {

    /** pocatecni vrchol hrany */
    private final AbstractNode startNode;
    /** konecny vrchol hrany */
    private final AbstractNode endNode;
    /** vaha, delka hrany */
    private final double weight;

    public Edge(AbstractNode startNode, AbstractNode endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
        weight = Math.sqrt( Math.abs(startNode.x - endNode.x) + Math.abs(startNode.y - endNode.y) );
    }

    public AbstractNode getStartNode() {
        return startNode;
    }

    public AbstractNode getEndNode() {
        return endNode;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "startNode=" + startNode.id +
                ", endNode=" + endNode.id +
                ", weight=" + weight +
                '}';
    }
}
