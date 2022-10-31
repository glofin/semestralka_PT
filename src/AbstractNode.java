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

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getId() {
		return id;
	}

	//TODO: hashCode a equals pro Node
	@Override
	public int hashCode() {
		return super.hashCode();
	}


	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}

