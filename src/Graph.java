import java.util.*;

/**
 * Trida reprezentujici mapu/graf
 * vytvoreny z vrcholu (sklady, oazy) a hran (Edge)
 * Navrhovy vzor: prepravka
 */
public class Graph {

    /** prepravka, jedinacek */
    private static Graph messenger;

    /** Mapa vrcholu a jeho cest */
    private final Map<AbstractNode, List<Edge>> adjNodes = new HashMap<>();

    /** Seznam vrcholu serazenych podle id pro rychly pristup getNodebyId() */
    private final List<AbstractNode> nodesList = new ArrayList<>();

    private Graph(){}

    /**
     * Vycisti graf pro novou mapu
     */
    public void clearGraph(){
        adjNodes.clear();
        nodesList.clear();
    }

    /** Jedinace dostat graf */
    public static Graph getInstance(){
        if(messenger==null) {
            messenger = new Graph();
        }
        return messenger;
    }

    /**
     * Prida vrcholy do grafu
     * @param node vrchol pro pridani do grafu
     */
    void addNode(AbstractNode node) {
        adjNodes.putIfAbsent(node, new ArrayList<>());
        if (nodesList.size() != node.id) {System.out.println("Chyba pri pridani vrcholu ve tride Graph addNode()");}
        nodesList.add(node);
    }

    /**
     * Prida hrany do grafu
     * @param node1 vrchol hrany
     * @param node2 vrhcol hrany
     */
    void addEdge(AbstractNode node1, AbstractNode node2){
        adjNodes.get(node1).add(new Edge(node1, node2));
        adjNodes.get(node2).add(new Edge(node2, node1));
    }

    /**
     * Vrati vrchol podle id
     */
    AbstractNode getNodebyId(int id){
        //TODO predelat
        return nodesList.get(id);
    }

    /**
     * Vypise graf ve tvaru
     * 'Vrchol
     * -> Hrana'
     * @return vypis
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

    /**
     * Vytvori list cest do oazi ze vsech skladu
     * @param idOasis id oazi
     * @return list cest
     */
    public List<MyPath> getPathtoOasisList(int idOasis){
        //TODO dlouhy soubory padaji
        Oasis oasis = (Oasis) nodesList.get(idOasis);
        runDijkstraOnNodes(oasis);

        List<MyPath> pathList = new ArrayList<>();
        //System.out.println(nodesList.toString());

        for (AbstractNode node : nodesList){
            if (node instanceof Stock){

                List<Edge> edgeList = new ArrayList<>();
               // System.out.println(node.getClass() + " id " + node.getId());
                //System.out.println("Sklad distance " + node.getDistance() + " shortest " + node.getShortestPaths().get(oasis));
                AbstractNode currentNode = node;
                AbstractNode lastNode = currentNode.getShortestPaths().get(oasis);
                while (currentNode != lastNode){
                    lastNode = currentNode.getShortestPaths().get(oasis);

                    List<Edge> edgesOfNode = adjNodes.get(currentNode);
                    for (Edge edge :
                            edgesOfNode) {
                        if (edge.getEndNode() == lastNode) {
                            edgeList.add(edge);
                            break;
                        }
                    }
                    currentNode = lastNode;
                }
                pathList.add(new MyPath(edgeList.toArray(new Edge[0])));
            }
        }
        Collections.sort(pathList);
        return pathList;
    }

    /**
     * Nastavi do vrcholu distance od zvoleneho vrcholu
     * a do shortest Path nejkratsi cestu k vrcholu
     * @param source pocatecni vrchol
     */
    private void runDijkstraOnNodes(AbstractNode source){
       // System.out.println(source);

        nodesList.forEach(abstractNode -> abstractNode.setDistance(1000000000));
        source.setDistance(0);

        Set<AbstractNode> doneNodes = new HashSet<>();
        Set<AbstractNode> workingNodes = new HashSet<>();

        workingNodes.add(source);

        while (workingNodes.size() != 0){
            AbstractNode currentNode = findMinDistanceNode(workingNodes);
            //System.out.println("workingNodes " + workingNodes);
            //System.out.println("findMinDistanceNode " + findMinDistanceNode(workingNodes));
            List<Edge> currentNeighbours = adjNodes.get(currentNode);
            for (Edge edge :
                    currentNeighbours) {
                AbstractNode endNode = edge.getEndNode();

                if (!doneNodes.contains(endNode)){
                    if ((currentNode.getDistance() + edge.getWeight()) < endNode.getDistance()){
                        endNode.setDistance(currentNode.getDistance() + edge.getWeight());
                        endNode.putInShortestPaths(source, currentNode);
                        //System.out.println("id = " + endNode.getId() +"distance" + endNode.distance+ " shortPath: " + endNode.getShortestPaths());
                    }
                    workingNodes.add(endNode);
                }
            }
            workingNodes.remove(currentNode);
            doneNodes.add(currentNode);
        }
    }

    /**
     * Najde v listu workingNodes Node s nejmensi distance
     * @param workingNodes list vrcholu ktere se zpracovavaji
     * @return vrchol ktery ma nejkratsi vzdalenost z workingNodes
     */
    private AbstractNode findMinDistanceNode(Set<AbstractNode> workingNodes){
        AbstractNode minNode = null;
        double minDistance = Double.MAX_VALUE;
      // minNode = workingNodes.size()==1?node:null;
        for (AbstractNode node :
                workingNodes) {

            double distance = node.getDistance();
            if (distance - minDistance <= 0.1) {
                minDistance = distance;
                minNode = node;
            }
        }
        return minNode;
    }

    public List<AbstractNode> getNodesList() {
        return nodesList;
    }
}
