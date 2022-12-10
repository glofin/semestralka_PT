import java.util.ArrayList;
import java.util.Random;

/**
 * Trida slouzi ke generovani nahodnych vstupnich dat
 */
public class Generator {
	
	/** Maximalni pocet skladu */
	private final static int STOCK_MAX = 30;
	/** Maximalni pocet oaz */
	private final static int OASIS_MAX = 30;
	/** Maximalni pocet ruznyh druhu velblouda */
	private final static int TYPE_MAX = 6;
	/** Maximalni pocet pozadavku */
	private final static int TASK_MAX = 30;
	
	/** Maximalni hodnata x,y skladu a oaz */
	private final static int POS_MAX = 60;
	/** Maximalni pocet kosu ve skladu */
	private final static int STOCK_BASKET_MAX = 60;
	/** Maximalni doba, za kterou sklad doplni kose */
	private final static int STOCK_REFILL_MAX = 50;
	/** Maximalni doba nakladani/vykladani kosu skladu */
	private final static int STOCK_MANIPULATION_MAX = 10;

	/** Maximalni hodnota pro minimum rychlosti druhu */
	private final static int MIN_SPEED_MAX = 5;
	/** Maximalni hodnota, o kterou se muze lisit min a max rychlosti druhu */
	private final static int MAX_SPEED_RANGE = 60;
	/** Maximalni hodnota pro minimun usle vzdalenosti druhu */
	private final static int MIN_DIST_MAX = 5;
	/** Maximalni hodnota, o kterou se muze lisit min a max usle vzdalenosti druhu */
	private final static int MAX_DIST_RANGE = 60;
	/** Maximalni doba piti druhu */
	private final static int DRINK_MAX = 10;
	/** Maximalni pocet kosu, co muze druh unest */
	private final static int CARRY_MAX = 60;

	/** Maximalni doba, ve ktere muze pozadavek prijit */
	private final static int TASK_TIME_MAX = 200;
	/** Maximalni doba od prichodu pozadavku, do ktere musi byt pozadavek obslouzen */
	private final static int TASK_DEADLINE_MAX = 1000;
	/** Maximalni pocet kosu, co muze pozadavek vyzadovat */
	private final static int TASK_BASKET_MAX = 60;

	
	public static String generateRandomInput() {
		Random r = new Random(104); //seed 20 error?, seed 64 splnitelny 
		String s = "";
		int i;
		
		//SKLADY
		int stockCount = r.nextInt(STOCK_MAX) + 1;
		s += stockCount;
		for (i = 0; i < stockCount; i++) {
			s += genStock(r);
		}
		//OAZY
		int oasisCount = r.nextInt(OASIS_MAX) + 1;
		s += " " + oasisCount;
		for (i = 0; i < oasisCount; i++) {
			s += genOasis(r);
		}
		//CESTY
		int verticeCount = oasisCount + stockCount;
		int pathCount = r.nextInt((verticeCount*(verticeCount-1)/2));
		ArrayList<Vector> vertices = new ArrayList<Vector>(pathCount+1);
		s += " " + pathCount;
		for (i = 0; i < pathCount; i++) {
			s+= genVertice(r, verticeCount, vertices);
		}
		
		//DRUHY
		int typeCount = r.nextInt(TYPE_MAX) + 1;
		s += " " + typeCount;
		s += genTypes(r, typeCount);
		
		//POZADAVKY
		int taskCount = r.nextInt(TASK_MAX) + 1;
		s += " " + taskCount;
		for(i = 0; i <taskCount; i++) {
			s+= genTask(r, oasisCount);
		}
		
		return s;
	}

	private static String genTask(Random r, int oasisCount) {
		String s = " ";
		double arrTime = r.nextDouble() * TASK_TIME_MAX;
		s += arrTime + " ";
		s += (r.nextInt(oasisCount) + 1) + " ";
		s += r.nextInt(TASK_BASKET_MAX) + " ";
		s += r.nextInt(TASK_DEADLINE_MAX);
		
		return s;
	}

	private static String genTypes(Random r, int typeCount) {
		String s = "";
		double nextChance = 1;
		for(int i = 1; i <= typeCount; i++) {
			s += " Type" + i + " ";
			double minSpeed = r.nextDouble() * MIN_SPEED_MAX;
			s += minSpeed + " ";
			s += (r.nextDouble() * MAX_SPEED_RANGE + minSpeed) + " ";
			double minDist = r.nextDouble() * MIN_DIST_MAX;
			s += minDist + " ";
			s += r.nextDouble() * MAX_DIST_RANGE + " ";
			s += r.nextDouble() * DRINK_MAX + " ";
			s += r.nextInt(CARRY_MAX) + " ";
			if (i != typeCount) {
				double chance = r.nextDouble() * nextChance;
				nextChance = nextChance - chance;
				s += chance;
			} else {
				s += nextChance;
			}
		}
		return s;
	}
	
	/** Vnitrni trida pro reprezentaci hran grafu (aby nebyli generovani k duplicitni hrany) */
	private static class Vector implements Comparable<Vector> {
		
		int a;
		int b;

		@Override
		public int compareTo(Vector o) {
			if((this.a == o.b && this.b == o.a) || (this.a == o.a && this.b == o.b)) {
				return 0;
			} else {
				return -1;
			}
		}
		@Override
		public String toString() {
			return " " + this.a + " " + this.b + " ";
		}
		
	}

	private static String genVertice(Random r, int verticeCount, ArrayList<Vector> vertices) {
		int i1 = 0;
		int i2 = 0;
		Vector vertice = new Vector();
			do {
				i1 = r.nextInt(verticeCount) + 1;
				i2 = r.nextInt(verticeCount) + 1;
				vertice.a = i1;
				vertice.b = i2;

			} while (vertices.contains(vertice) || i1 == i2);
		vertices.add(vertice);
		return vertice.toString();
	}

	private static String genOasis(Random r) {
		String s = "";
		s += " " + r.nextDouble() * POS_MAX;
		s += " " + r.nextDouble() * POS_MAX;
		return s;
	}

	private static String genStock(Random r) {
		String s = "";
		s += " " + r.nextDouble() * POS_MAX;
		s += " " + r.nextDouble() * POS_MAX;
		s += " " + (r.nextInt(STOCK_BASKET_MAX) + 1) ;
		s += " " + (r.nextInt(STOCK_REFILL_MAX) + 1);
		s += " " + (r.nextInt(STOCK_MANIPULATION_MAX) + 1);
		return s;
	}
	
}