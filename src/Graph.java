import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trida reprezentujici mapu/graf
 * vytvoreny z vrcholu (sklady, oazy) a hran (Edge)
 *
 * Navrhovy vzor: prepravka
 */
public class Graph {

    /** prepravka, jedinacek */
    private static Graph messenger;
    /** Mapa vrcholu a jeho cest */
    private final Map<AbstractNode, List<Edge>> adjNodes = new HashMap<>();
    /** Seznam vrcholu serazenych podle id pro rychly pristup getNodebyId() */
    private final List<AbstractNode> nodesList = new ArrayList<>();

    private Graph(){};

    /** Jedinace dostat graf */
    public static Graph getInstance(){
        if(messenger==null) {
            messenger = new Graph();
        }
        return messenger;
    }

    void addNode(AbstractNode node) {
        adjNodes.putIfAbsent(node, new ArrayList<>());
        if (nodesList.size() != node.id) System.out.println("Chyba pri pridani vrcholu ve tride Graph addNode()");
        nodesList.add(node);
    }

    void removeNode(AbstractNode node){
        adjNodes.values().forEach(edges -> edges.forEach(edge -> {
            if (edge.getStartNode() == node
                    || edge.getEndNode() == node)
                edges.remove(edge);
        })); //TODO udelat efektivnejsi
        adjNodes.remove(node);
        nodesList.set(node.id, null);
    }

    void addEdge(AbstractNode node1, AbstractNode node2){
        adjNodes.get(node1).add(new Edge(node1, node2));
        adjNodes.get(node2).add(new Edge(node2, node1));
    }

    void removeEdge(Edge edge){
        AbstractNode node1 = edge.getStartNode();
        AbstractNode node2 = edge.getEndNode();

        List<Edge> edgesNode1 = adjNodes.get(node1);
        List<Edge> edgesNode2 = adjNodes.get(node2);

        if (edgesNode1 != null)edgesNode1.remove(edge);
        if (edgesNode2 != null)edgesNode2.remove(edge);
    }

    AbstractNode getNodebyId(int id){
        //TODO predelat
        return nodesList.get(id);
    }

    List<Edge> getEdgesofNode(AbstractNode node){
        return adjNodes.get(node);
    }

    /**
     * Vypise graf ve tvaru
     * 'Vrchol
     * -> Hrana
     *
     * '
     * @return
     */
    public String toString(){
        StringBuffer output = new StringBuffer();
        adjNodes.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> {
            output.append(entry.getKey().toString());
            output.append("\n");
            entry.getValue().forEach(edge -> output.append("-> ").append(edge.toString()).append("\n"));
            output.append("\n");
        });
        return output.toString();
    }

}
