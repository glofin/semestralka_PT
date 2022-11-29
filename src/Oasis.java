/**
 * Instance tridy {@code Oaza} predstavuji jednotlive oazy, do kterych mohou chodit velbloudi
 */
public class Oasis extends AbstractNode {

	/**
	 * Vytvori Oazu na urcenych souradnicich
	 * 
	 * @param x		souradnice x
	 * @param y		souradnice y
	 */
	public Oasis(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Oasis{" +
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
