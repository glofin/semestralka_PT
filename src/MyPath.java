import java.util.Arrays;

public class MyPath implements Comparable<MyPath>{
    private final Edge[] edgesArr;

    private final double fullDistance;

    private final double maxDistance;

    public MyPath(Edge[] edgesArr) {
        this.edgesArr = edgesArr;
        double[] distances = computeFullDistanceAndFindMax();
        fullDistance = distances[0];
        maxDistance = distances[1];
    }

    private double[] computeFullDistanceAndFindMax() {
        double count = 0;
        double max = -1;
        for (Edge edge :
                edgesArr) {
            double weight = edge.getWeight();
            count += weight;
            max = Math.max(weight, max);
        }
        return new double[]{count,max};
    }
    /**
     * Zjisti celkovy cas, ktery velbloud stravi na trase
     * @param camel	velbloud, pro ktereho se zjistuje cas
     * @return		cas straveny na trase
     */
    public double getTravelTime(Camel camel) {
    	double travelTime = this.fullDistance / camel.speed;
    	double distance = 0;
    	for(Edge e: edgesArr) {
    		distance += e.getWeight();
    		if(distance > camel.distance) {
    			distance = 0;
    			travelTime += camel.drinkTime;
    		}
    	}
    	
    	return travelTime;
    }

    public Stock getStartStock(){
        return (Stock) edgesArr[0].getStartNode();
    }

    public Edge[] getEdgesArr() {
        return edgesArr;
    }

    public double getFullDistance() {
        return fullDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    @Override
    public String toString() {
        return "MyPath{" +
                "edgesArr=" + Arrays.toString(edgesArr) +
                ", fullDistance=" + fullDistance +
                ", maxDistance=" + maxDistance +
                '}';
    }

	@Override
	public int compareTo(MyPath o) {
		if(o.getFullDistance() > this.getFullDistance()) {
			return -1;
		} else if (o.getFullDistance() < this.getFullDistance()) {
			return 1;
		}
		if(super.equals(o)) {
			return 0;
		}
		return 1;
	}
}
