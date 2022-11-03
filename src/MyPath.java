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

    public Sklad getStartStock(){
        return (Sklad) edgesArr[0].getStartNode();
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
		}
		return 1;
	}
}
