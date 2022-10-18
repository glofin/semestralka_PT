/**
 * Instance tridy {@code Sklad} predstavuji jednotlive sklady, ze kterych jsou vysilani velbloudi s nakladem kosu
 */
public class Sklad {

	double x;
	double y;
	int pocetKosu;
	int casDoplneni;
	int casNalozeni;
	
	
	public Sklad(double x, double y, int pocetKosu, int casDoplneni, int casNalozeni) {
		this.x = x;
		this.y = y;
		this.pocetKosu = pocetKosu;
		this.casDoplneni = casDoplneni;
		this.casNalozeni = casNalozeni;
	}
	
	
}
